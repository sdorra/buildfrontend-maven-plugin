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

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.google.common.io.Files;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.repository.RepositorySystem;

import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Sebastian Sdorra
 */
public abstract class AbstractNodeMojo extends AbstractMojo
{

  /** Field description */
  private static final String ARTIFACTID_NODE = "buildfrontend-node";

  /** Field description */
  private static final String ARTIFACTID_NPM = "buildfrontend-npm";

  /** Field description */
  private static final String GROUPID = "com.github.sdorra";

  /** Field description */
  private static final String PACKAGETYPE_ZIP = "zip";

  /** Field description */
  private static final String PATH_NODE_BIN = "bin/node";

  /** Field description */
  private static final String URL_TEMPLATE_NPM =
    "http://nodejs.org/dist/npm/npm-%s.zip";

  /** Field description */
  private static final String VERSION_NODE = "v0.10.26";

  /** Field description */
  private static final String VERSION_NPM = "1.4.7";

  /**
   * the logger for AbstractNodeMojo
   */
  private static final Logger logger =
    LoggerFactory.getLogger(AbstractNodeMojo.class);

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param urlString
   * @param target
   *
   * @throws IOException
   */
  private static void download(String urlString, File target) throws IOException
  {
    URL url = new URL(urlString);
    URLConnection connection = url.openConnection();
    Closer closer = Closer.create();

    try
    {
      InputStream input = closer.register(connection.getInputStream());
      OutputStream output = closer.register(new FileOutputStream(target));

      ByteStreams.copy(input, output);
    }
    catch (IOException ex)
    {
      throw closer.rethrow(ex);
    }
    finally
    {
      closer.close();
    }
  }

  /**
   * Method description
   *
   *
   * @throws MojoExecutionException
   * @throws MojoFailureException
   */
  @Override
  public final void execute()
    throws MojoExecutionException, MojoFailureException
  {
    StaticLoggerBinder.getSingleton().setLog(getLog());
    doExecute();
  }

  /**
   * Method description
   *
   *
   * @throws MojoExecutionException
   * @throws MojoFailureException
   */
  protected abstract void doExecute()
    throws MojoExecutionException, MojoFailureException;

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param archiverManager
   */
  public void setArchiverManager(ArchiverManager archiverManager)
  {
    this.archiverManager = archiverManager;
  }

  /**
   * Method description
   *
   *
   * @param buildDirectory
   */
  public void setBuildDirectory(String buildDirectory)
  {
    this.buildDirectory = buildDirectory;
  }

  /**
   * Method description
   *
   *
   * @param installer
   */
  public void setInstaller(ArtifactInstaller installer)
  {
    this.installer = installer;
  }

  /**
   * Method description
   *
   *
   * @param nodeVersion
   */
  public void setNodeVersion(String nodeVersion)
  {
    this.nodeVersion = nodeVersion;
  }

  /**
   * Method description
   *
   *
   * @param npmVersion
   */
  public void setNpmVersion(String npmVersion)
  {
    this.npmVersion = npmVersion;
  }

  /**
   * Method description
   *
   *
   * @param repositorySystem
   */
  public void setRepositorySystem(RepositorySystem repositorySystem)
  {
    this.repositorySystem = repositorySystem;
  }

  /**
   * Method description
   *
   *
   * @param version
   */
  public void setVersion(String version)
  {
    this.nodeVersion = version;
  }

  /**
   * Method description
   *
   *
   * @param workDirectory
   */
  public void setWorkDirectory(String workDirectory)
  {
    this.workDirectory = workDirectory;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   *
   * @throws MojoExecutionException
   */
  protected NodeExecutor createNodeExecutor() throws MojoExecutionException
  {
    Platform platform = Platform.current();
    File nodeDirectory = extractNode(platform);
    File node = findNodeExecutable(platform, nodeDirectory);
    File npmDirectory = extractNpm();

    return new NodeExecutor(new File(workDirectory), node, npmDirectory);
  }

  /**
   * Method description
   *
   *
   * @param artifact
   * @param downloadUrl
   *
   * @throws MojoExecutionException
   */
  protected void installArtifact(Artifact artifact, String downloadUrl)
    throws MojoExecutionException
  {
    File tmpfile = null;

    try
    {
      ArtifactRepository repository =
        repositorySystem.createDefaultLocalRepository();

      if (!isInstalled(repository, artifact))
      {

        tmpfile = new File(buildDirectory, artifact.getArtifactId());

        logger.info("download {} from {}", artifact.getId(), downloadUrl);

        download(downloadUrl, tmpfile);

        installer.install(tmpfile, artifact,
          repositorySystem.createDefaultLocalRepository());
      }
      else
      {
        logger.info("{} is already installed", artifact.getId());
      }
    }
    catch (Exception ex)
    {
      throw new MojoExecutionException("could not download node", ex);
    }
    finally
    {
      if ((tmpfile != null) && tmpfile.exists())
      {
        tmpfile.delete();
      }
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
  protected File installNodeArtifact() throws MojoExecutionException
  {
    return installNodeArtifact(Platform.current());
  }

  /**
   * Method description
   *
   *
   * @param platform
   * @return
   *
   * @throws MojoExecutionException
   */
  protected File installNodeArtifact(Platform platform)
    throws MojoExecutionException
  {

    Artifact artifact = createNodeArtifact(platform);

    installArtifact(artifact, platform.getNodeUrl(nodeVersion));

    return artifact.getFile();
  }

  /**
   * Method description
   *
   *
   * @return
   *
   * @throws MojoExecutionException
   */
  protected File installNpmArtifact() throws MojoExecutionException
  {
    Artifact artifact = createNpmArtifact();
    String url = String.format(URL_TEMPLATE_NPM, npmVersion);

    installArtifact(artifact, url);

    return artifact.getFile();
  }

  /**
   * Method description
   *
   *
   * @param platform
   *
   * @return
   */
  private Artifact createNodeArtifact(Platform platform)
  {
    return repositorySystem.createArtifactWithClassifier(GROUPID,
      ARTIFACTID_NODE, nodeVersion, platform.getNodePackageType(),
      platform.getClassifier());
  }

  /**
   * Method description
   *
   *
   * @return
   */
  private Artifact createNpmArtifact()
  {
    return repositorySystem.createArtifact(GROUPID, ARTIFACTID_NPM, npmVersion,
      PACKAGETYPE_ZIP);
  }

  /**
   * Method description
   *
   *
   * @param artifact
   * @param target
   *
   * @throws MojoExecutionException
   */
  private void extract(File artifact, File target) throws MojoExecutionException
  {
    logger.info("extract {} to {}", artifact, target);

    try
    {
      UnArchiver unarchiver = archiverManager.getUnArchiver(artifact);

      unarchiver.setSourceFile(artifact);
      unarchiver.setDestDirectory(target);
      unarchiver.extract();
    }
    catch (NoSuchArchiverException ex)
    {
      throw new MojoExecutionException("could not find unarchiver", ex);
    }
  }

  /**
   * Method description
   *
   *
   *
   * @param platform
   * @return
   *
   * @throws MojoExecutionException
   */
  private File extractNode(Platform platform) throws MojoExecutionException
  {
    File nodeDirectory = new File(buildDirectory, "node");

    try
    {

      if (!nodeDirectory.exists())
      {

        if (!nodeDirectory.mkdirs())
        {
          throw new MojoExecutionException("could not create node directory");
        }

        File nodeArtifact = installNodeArtifact(platform);

        if (platform.isNodeUnpacked())
        {
          File nodeFile = new File(nodeDirectory, platform.getExecutableName());
          logger.info("copy node to {}", nodeFile);
          Files.copy(nodeArtifact, nodeFile);
        }
        else
        {
          extract(nodeArtifact, nodeDirectory);
        }
      }

    }
    catch (Exception ex)
    {
      Throwables.propagateIfInstanceOf(ex, MojoExecutionException.class);

      throw new MojoExecutionException("could not create npm executor", ex);
    }

    return nodeDirectory;
  }

  /**
   * Method description
   *
   *
   * @return
   *
   * @throws MojoExecutionException
   */
  private File extractNpm() throws MojoExecutionException
  {
    File directory = new File(buildDirectory, "npm");

    if (!directory.exists())
    {
      if (!directory.mkdirs())
      {
        throw new MojoExecutionException("could not create npm directory");
      }

      File npmArtifact = installNpmArtifact();

      extract(npmArtifact, directory);
    }

    return directory;
  }

  /**
   * Method description
   *
   *
   * @param platform
   * @param directory
   *
   * @return
   *
   * @throws MojoExecutionException
   */
  private File findNodeExecutable(Platform platform, File directory)
    throws MojoExecutionException
  {
    File node = null;

    if (platform.isNodeUnpacked())
    {
      for (File f : directory.listFiles())
      {
        if (f.getName().startsWith("node"))
        {
          node = f;

          break;
        }
      }
    }
    else
    {
      File nodeDirectory = null;

      for (File f : directory.listFiles())
      {
        if (f.getName().startsWith("node-".concat(nodeVersion)))
        {
          nodeDirectory = f;

          break;
        }
      }

      if (nodeDirectory == null)
      {
        throw new MojoExecutionException("could not find node directory");
      }

      node = new File(nodeDirectory, PATH_NODE_BIN);
    }

    if ((node == null) ||!node.exists())
    {
      throw new MojoExecutionException("could not find node executable");
    }

    return node;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   * @param artifact
   *
   * @return
   */
  private boolean isInstalled(ArtifactRepository repository, Artifact artifact)
  {
    boolean result = false;
    Artifact installed = repository.find(artifact);

    if (installed != null)
    {
      File file = installed.getFile();

      result = (file != null) && file.exists();
    }

    return result;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Component
  private ArchiverManager archiverManager;

  /** Field description */
  @Parameter(defaultValue = "${project.build.directory}/frontend")
  private String buildDirectory;

  /** Field description */
  @Component
  private ArtifactInstaller installer;

  /** Field description */
  @Parameter
  private String npmVersion = VERSION_NPM;

  /** Field description */
  @Parameter
  private String nodeVersion = VERSION_NODE;

  /** Field description */
  @Component
  private RepositorySystem repositorySystem;

  /** Field description */
  @Parameter(defaultValue = "${basedir}")
  private String workDirectory;
}
