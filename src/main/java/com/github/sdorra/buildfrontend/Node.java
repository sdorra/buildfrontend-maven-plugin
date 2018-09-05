package com.github.sdorra.buildfrontend;

import com.google.common.annotations.VisibleForTesting;

import java.io.File;

public class Node {

    private final File workingDirectory;
    private final File executable;

    Node(File workingDirectory, File executable) {
        this.workingDirectory = workingDirectory;
        this.executable = executable;
    }

    @VisibleForTesting
    File getExecutable() {
        return executable;
    }

    public void execute(String command, String... args) {
        builder().execute(command, args);
    }

    public NodeExecutionBuilder builder() {
        return new NodeExecutionBuilder(workingDirectory, executable);
    }
}
