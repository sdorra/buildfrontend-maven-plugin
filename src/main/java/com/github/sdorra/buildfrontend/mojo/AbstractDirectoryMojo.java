package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Directories;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractDirectoryMojo extends AbstractMojo {

    @Component
    private Directories directories;

    @Parameter(defaultValue = "${project.build.directory}/frontend")
    private String buildDirectory;

    @Parameter(defaultValue = "${basedir}")
    private String workDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        directories.setBuildDirectory(buildDirectory);
        directories.setWorkingDirectory(workDirectory);
        execute(directories);
    }

    protected abstract void execute(Directories directories) throws MojoExecutionException, MojoFailureException;
}
