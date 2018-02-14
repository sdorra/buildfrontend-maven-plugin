package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Directories;
import com.google.common.annotations.VisibleForTesting;
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
    private String workingDirectory;

    @VisibleForTesting
    void setDirectories(Directories directories) {
        this.directories = directories;
    }

    @VisibleForTesting
    void setBuildDirectory(String buildDirectory) {
        this.buildDirectory = buildDirectory;
    }

    @VisibleForTesting
    void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        directories.setBuildDirectory(buildDirectory);
        directories.setWorkingDirectory(workingDirectory);
        execute(directories);
    }

    protected abstract void execute(Directories directories) throws MojoExecutionException, MojoFailureException;
}
