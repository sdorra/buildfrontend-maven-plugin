/**
 * The MIT License
 *
 * Copyright 2016 Sebastian Sdorra.
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
 * Executes an command with the node executor.
 * 
 * @author Sebastian Sdorra
 * @since 1.1.0
 */
@Mojo(name = "exec", defaultPhase = LifecyclePhase.COMPILE)
public class ExecMojo extends AbstractNodeMojo {

    @Parameter
    private String command;
    
    @Parameter
    private String[] args;

    /**
     * Sets the command for execution.
     * 
     * @param command command for execution 
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Sets the arguments for the command.
     * 
     * @param args command arguments 
     */
    public void setArgs(String[] args) {
        this.args = args;
    }
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Strings.isNullOrEmpty(command)) {
            throw new MojoExecutionException("command parameter is required");
        }
        
        if (args == null) {
            args = new String[0];
        }
        
        NodeExecutor executor = createNodeExecutor();
        executor.cmd(command).args(args).execute();
    }

}
