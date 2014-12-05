/**
 * The MIT License
 *
 * Copyright (c) 2014, Sebastian Sdorra
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */



package com.github.sdorra.buildfrontend;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import org.apache.maven.plugin.MojoExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zeroturnaround.exec.ProcessExecutor;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sebastian Sdorra
 */
public final class NodeExecutor
{

  /** Field description */
  private static final String[] CLI_BIN = new String[] {
    "node_modules/npm/cli.js",
    "bin/npm-cli.js" 
  };

  /** Field description */
  private static final String ENV_PATH = "PATH";

  /** Field description */
  private static final String PATH_NODE_MODULES = "node_modules";

  /**
   * the logger for NodeExecutor
   */
  private static final Logger logger =
    LoggerFactory.getLogger(NodeExecutor.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   * @param platform
   * @param workDirectory
   * @param node
   * @param npmDirectory
   */
  public NodeExecutor(Platform platform, File workDirectory, File node,
    File npmDirectory)
  {
    this.workDirectory = workDirectory;
    this.node = node.getPath();
    this.npmCli = findNpmCli(npmDirectory).getPath();
    logger.debug("use npm {}", npmCli);

    if (platform.isNeedExecutableInPath())
    {
      String key = ENV_PATH;
      StringBuilder buffer = new StringBuilder();

      for (Map.Entry<String, String> e : System.getenv().entrySet())
      {
        String k = e.getKey();

        if (ENV_PATH.equalsIgnoreCase(k))
        {
          key = k;
          buffer.append(Strings.nullToEmpty(e.getValue()));

          break;
        }
      }

      buffer.append(File.pathSeparator).append(node.getParent());
      env = new HashMap<String, String>(System.getenv());

      String p = buffer.toString();

      logger.debug("set {} environment {} for execution", key, p);
      env.put(key, p);
    }
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param args
   *
   * @return
   */
  public CommandExecutor cmd(Collection<String> args)
  {
    CommandExecutor executor = new CommandExecutor(env, workDirectory, node);

    if (args != null)
    {
      executor.args(args);
    }

    return executor;
  }

  /**
   * Method description
   *
   *
   * @param args
   * @return
   *
   * @throws MojoExecutionException
   */
  public CommandExecutor cmd(String... args) throws MojoExecutionException
  {
    CommandExecutor executor = new CommandExecutor(env, workDirectory, node);

    if (args != null)
    {
      executor.args(args);
    }

    return executor;
  }

  /**
   * Method description
   *
   *
   * @param module
   * @param version
   *
   * @throws MojoExecutionException
   */
  public void install(String module, String version)
    throws MojoExecutionException
  {
    if (isNodeMuduleInstalled(module))
    {
      String installedVersion = moduleVersion(module);

      if (!Strings.nullToEmpty(installedVersion).equals(version))
      {
        logger.warn(
          "installed version {} of {} does not match required version {}",
          installedVersion, module, version);
        logger.info("uninstall version {} of {}", installedVersion, module);
        npmCmd("uninstall", module).execute();
        logger.info("install version {} of {}", version, module);
        npmCmd("install", "--save-dev",
          module.concat("@").concat(version)).execute();
      }
      else
      {
        logger.info("{}@{} is allready installed", module, version);
      }
    }
    else
    {
      logger.info("install version {} of {}", version, module);
      npmCmd("install", "--save-dev",
        module.concat("@").concat(version)).execute();
    }

  }

  /**
   * Method description
   *
   *
   * @param args
   * @return
   */
  public CommandExecutor npmCmd(String... args)
  {
    CommandExecutor executor = new CommandExecutor(env, workDirectory, node,
                                 npmCli, "--color=false", "--parseable");

    if (args != null)
    {
      executor.args(args);
    }

    return executor;
  }

  /**
   * Method description
   *
   *
   * @param npmDirectory
   *
   * @return
   */
  private File findNpmCli(File npmDirectory)
  {
    File cli = findNpmCliBin(npmDirectory);

    if (cli == null)
    {
      for (File f : npmDirectory.listFiles())
      {
        if (f.isDirectory())
        {
          cli = findNpmCliBin(f);

          if (cli != null)
          {
            break;
          }
        }
      }
    }

    if (cli == null)
    {
      throw new RuntimeException("could not find npm");
    }

    return cli;
  }

  /**
   * Method description
   *
   *
   * @param directory
   *
   * @return
   */
  private File findNpmCliBin(File directory)
  {
    File cli = null;

    for (String bin : CLI_BIN)
    {
      File f = new File(directory, bin);

      if (f.exists())
      {
        cli = f;

        break;
      }
    }

    return cli;
  }

  /**
   * Method description
   *
   *
   * @param name
   *
   * @return
   *
   * @throws MojoExecutionException
   */
  private String moduleVersion(String name) throws MojoExecutionException
  {
    String version = null;

    try
    {
      String out = npmCmd().args("--long", "list", name).executeAndGet();
      String vline = null;

      logger.debug("npm cmd returned: {}", out);

      for (String part : out.split(":"))
      {
        int index = part.indexOf('@');
        if (index > 0)
        {
          version = part.substring(index + 1);
          break;
        }
      }
    }
    catch (Exception ex)
    {
      Throwables.propagateIfInstanceOf(ex, MojoExecutionException.class);

      throw Throwables.propagate(ex);
    }

    return version;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param name
   *
   * @return
   */
  private boolean isNodeMuduleInstalled(String name)
  {
    File moduleDir = new File(workDirectory, PATH_NODE_MODULES);

    return new File(moduleDir, name).exists();
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 14/04/26
   * @author         Enter your name here...
   */
  public static final class CommandExecutor
  {

    /**
     * Constructs ...
     *
     *
     *
     * @param environment
     * @param workDirectory
     * @param cmd
     */
    private CommandExecutor(Map<String, String> environment,
      File workDirectory, String... cmd)
    {
      this.environment = environment;
      this.workDirectory = workDirectory;
      this.command = Lists.newArrayList();
      args(cmd);
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param args
     *
     * @return
     */
    public final CommandExecutor args(String... args)
    {
      command.addAll(Arrays.asList(args));

      return this;
    }

    /**
     * Method description
     *
     *
     * @param args
     *
     * @return
     */
    public final CommandExecutor args(Collection<String> args)
    {
      command.addAll(args);

      return this;
    }

    /**
     * Method description
     *
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException
    {
      try
      {
        //J-
        createProcessExecutor()
          .redirectOutput(System.out)
          .execute();
        //J+
      }
      catch (Exception ex)
      {
        throw new MojoExecutionException("cuold not execute command", ex);
      }
    }

    /**
     * Method description
     *
     *
     * @return
     *
     * @throws MojoExecutionException
     */
    public String executeAndGet() throws MojoExecutionException
    {
      try
      {
        //J-
        return createProcessExecutor()
          .readOutput(true)
          .execute()
          .outputString()
          .trim();
        //J+
      }
      catch (Exception ex)
      {
        throw new MojoExecutionException("cuold not execute command", ex);
      }
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public ProcessExecutor process()
    {
      return createProcessExecutor().redirectOutput(System.out);
    }

    /**
     * Method description
     *
     *
     * @return
     */
    private ProcessExecutor createProcessExecutor()
    {
      logger.debug("create process executor for {}", command);

      //J-
      ProcessExecutor executor = new ProcessExecutor(command)
        .directory(workDirectory)
        .redirectErrorStream(true)
        .exitValueNormal();
      //J+

      if (environment != null)
      {
        executor = executor.environment(environment);
      }

      return executor;
    }

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final List<String> command;

    /** Field description */
    private final Map<String, String> environment;

    /** Field description */
    private final File workDirectory;
  }


  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final String node;

  /** Field description */
  private final String npmCli;

  /** Field description */
  private final File workDirectory;

  /** Field description */
  private Map<String, String> env;
}
