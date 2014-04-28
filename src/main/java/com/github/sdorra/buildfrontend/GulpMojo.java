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
@Mojo(name = "gulp", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GulpMojo extends AbstractNodeMojo
{

  /** Field description */
  private static final String GULP = "node_modules/gulp/bin/gulp.js";

  /** Field description */
  private static final String MODULE = "gulp";

  /** Field description */
  private static final String VERSION = "3.6.2";

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param tasks
   */
  public void setGulpTasks(String[] tasks)
  {
    this.gulpTasks = tasks;
  }

  /**
   * Method description
   *
   *
   * @param gulpVersion
   */
  public void setGulpVersion(String gulpVersion)
  {
    this.gulpVersion = gulpVersion;
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

    executor.install(MODULE, gulpVersion);

    CommandExecutor ce = executor.cmd(GULP);

    if (gulpTasks != null)
    {
      ce.args(gulpTasks);
    }

    ce.execute();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Parameter(required = false)
  private String[] gulpTasks;

  /** Field description */
  @Parameter
  private String gulpVersion = VERSION;
}
