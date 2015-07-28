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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author Sebastian Sdorra
 */
@Mojo(name = "bower-install", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class BowerInstallMojo extends AbstractNodeMojo
{

  /** Field description */
  private static final String BOWER = "node_modules/bower/bin/bower";

  /** Field description */
  private static final String MODULE = "bower";

  /** Field description */
  private static final String VERSION = "1.3.12";

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param bowerVersion
   */
  public void setBowerVersion(String bowerVersion)
  {
    this.bowerVersion = bowerVersion;
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
    NodeExecutor executor = createNodeExecutor();

    executor.install(MODULE, bowerVersion);
    executor.cmd(BOWER, "install").execute();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Parameter
  private String bowerVersion = VERSION;
}
