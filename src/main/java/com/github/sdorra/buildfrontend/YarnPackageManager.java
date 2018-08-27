package com.github.sdorra.buildfrontend;

public class YarnPackageManager implements PackageManager {

    private final Node node;
    private final String executable;

    YarnPackageManager(Node node, String executable) {
        this.node = node;
        this.executable = executable;
    }

    @Override
    public void install() {
        node.execute(executable, "install");
    }

    @Override
    public void run(String script) {
        node.execute(executable, "run", script);
    }

    @Override
    public void link() {
        node.execute(executable, "link");
    }

    @Override
    public void link(String pkg) {
        node.execute(executable, "link", pkg);
    }
}
