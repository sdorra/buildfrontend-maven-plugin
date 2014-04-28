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

import com.github.sdorra.buildfrontend.NodeExecutor.CommandExecutor;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Sebastian Sdorra
 */
@Mojo(name = "grunt", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GruntMojo extends AbstractNodeMojo
{

  /** Field description */
  private static final String GRUNT = "node_modules/grunt-cli/bin/grunt";

  /** Field description */
  private static final String MODULE_GRUNT = "grunt";

  /** Field description */
  private static final String MODULE_GRUNT_CLI = "grunt-cli";

  /** Field description */
  private static final String VERSION_GRUNT = "0.4.4";

  /** Field description */
  private static final String VERSION_GRUNT_CLI = "0.1.13";

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param gruntCliVersion
   */
  public void setGruntCliVersion(String gruntCliVersion)
  {
    this.gruntCliVersion = gruntCliVersion;
  }

  /**
   * Method description
   *
   *
   * @param gruntTasks
   */
  public void setGruntTasks(String[] gruntTasks)
  {
    this.gruntTasks = gruntTasks;
  }

  /**
   * Method description
   *
   *
   * @param gruntVersion
   */
  public void setGruntVersion(String gruntVersion)
  {
    this.gruntVersion = gruntVersion;
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
  protected void doExecute() throws MojoExecutionException, MojoFailureException
  {
    NodeExecutor executor = createNodeExecutor();

    executor.install(MODULE_GRUNT, gruntVersion);
    executor.install(MODULE_GRUNT_CLI, gruntCliVersion);

    CommandExecutor ce = executor.cmd(GRUNT);

    if (gruntTasks != null)
    {
      ce.args(gruntTasks);
    }

    ce.execute();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Parameter(required = false)
  private String[] gruntTasks;

  /** Field description */
  @Parameter
  private String gruntVersion = VERSION_GRUNT;

  /** Field description */
  @Parameter
  private String gruntCliVersion = VERSION_GRUNT_CLI;
}
