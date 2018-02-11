package com.github.sdorra.buildfrontend;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class Directories {

    private String workingDirectory;
    private String buildDirectory;

    public void setBuildDirectory(String buildDirectory) {
        this.buildDirectory = buildDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getBuildDirectory() {
        return buildDirectory;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }
}
