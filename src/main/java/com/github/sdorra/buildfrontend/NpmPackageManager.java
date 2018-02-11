package com.github.sdorra.buildfrontend;

public class NpmPackageManager implements PackageManager {

    private final Node node;
    private final String executable;

    NpmPackageManager(Node node, String executable) {
        this.node = node;
        this.executable = executable;
    }

    @Override
    public void install() {
        node.execute(executable, "--color=false", "--parseable", "install");
    }

    @Override
    public void run(String script) {
        node.execute(executable, "--color=false", "--parseable", "run", script);
    }
}
