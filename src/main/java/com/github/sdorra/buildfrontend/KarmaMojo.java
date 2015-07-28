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

import com.google.common.collect.Lists;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zeroturnaround.exec.ProcessResult;

//~--- JDK imports ------------------------------------------------------------


import java.util.List;

/**
 *
 * @author Sebastian Sdorra
 */
@Mojo(name = "karma", defaultPhase = LifecyclePhase.TEST)
public class KarmaMojo extends AbstractNodeMojo
{

  /**
   * Field description
   */
  private static final String KARMA = "node_modules/karma/bin/karma";

  /**
   * Field description
   */
  private static final String MODULE = "karma";

  /**
   * Field description
   */
  private static final String VERSION = "0.12.14";

  /** Field description */
  private static final Logger logger = LoggerFactory.getLogger(KarmaMojo.class);

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param ignoreResult
   */
  public void setIgnoreResult(boolean ignoreResult)
  {
    this.ignoreResult = ignoreResult;
  }

  /**
   * Method description
   *
   *
   * @param karmaConfig
   */
  public void setKarmaConfig(String karmaConfig)
  {
    this.karmaConfig = karmaConfig;
  }

  /**
   * Method description
   *
   *
   * @param karmaVersion
   */
  public void setKarmaVersion(String karmaVersion)
  {
    this.karmaVersion = karmaVersion;
  }

  /**
   * Method description
   *
   *
   * @param singleRun
   */
  public void setSingleRun(boolean singleRun)
  {
    this.singleRun = singleRun;
  }

  /**
   * Method description
   *
   *
   * @param skipTests
   */
  public void setSkipTests(boolean skipTests)
  {
    this.skipTests = skipTests;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @throws MojoExecutionException
   * @throws MojoFailureException
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    if (!skipTests)
    {
      NodeExecutor node = createNodeExecutor();

      node.install(MODULE, karmaVersion);

      List<String> cmd = Lists.newArrayList(KARMA, "start", karmaConfig);

      if (singleRun)
      {
        cmd.add("--single-run");
      }

      NodeExecutor.CommandExecutor executor = node.cmd(cmd);

      if (ignoreResult)
      {
        try
        {
          ProcessResult result = executor.process().exitValueAny().execute();
          int ev = result.getExitValue();

          if (ev > 0)
          {
            logger.warn("karma ended with status code {}", ev);
          }
        }
        catch (Exception ex)
        {
          logger.error("karma test ended with an exception", ex);
        }
      }
      else
      {
        executor.execute();
      }
    }
    else
    {
      logger.warn("karma tests are skipped by skipTests parameter");
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Parameter(property = "ingoreResult")
  private boolean ignoreResult = false;

  /**
   * Field description
   */
  @Parameter(required = true)
  private String karmaConfig;

  /**
   * Field description
   */
  @Parameter(property = "skipTests")
  private boolean skipTests = false;

  /**
   * Field description
   */
  @Parameter
  private boolean singleRun = true;

  /**
   * Field description
   */
  @Parameter
  private String karmaVersion = VERSION;
}
