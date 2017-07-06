/*
 * The MIT License
 *
 * Copyright 2017 Sebastian Sdorra.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.sdorra.buildfrontend;

import com.google.common.base.Strings;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Executes a script from the scripts section of a package.json.
 *
 * @author Sebastian Sdorra
 * @since 1.2.0
 */
@Mojo(name = "npm-run", defaultPhase = LifecyclePhase.COMPILE)
public class NpmRunMojo extends AbstractNodeMojo {

  @Parameter
  private String script;

  /**
   * Sets the script for execution.
   *
   * @param script script for execution
   */
  public void setScript(String script) {
    this.script = script;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (Strings.isNullOrEmpty(script)) {
      throw new MojoExecutionException("script parameter is required");
    }
    
    NodeExecutor executor = createNodeExecutor();
    executor.npmCmd("run", script).execute();
  }

}
