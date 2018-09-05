package com.github.sdorra.buildfrontend;

import java.io.File;

public class YarnPackageManager implements PackageManager {

    private final Node node;
    private final File executable;

    YarnPackageManager(Node node, File executable) {
        this.node = node;
        this.executable = executable;
    }

    @Override
    public void install() {
        yarn("install");
    }

    @Override
    public void run(String script) {
        yarn("run", script);
    }

    @Override
    public void link() {
        yarn("link");
    }

    @Override
    public void link(String pkg) {
        yarn("link", pkg);
    }

    private void yarn(String ...args) {
        node.builder()
                .prependBinaryToPath(executable.getParent())
                .execute(executable.getPath(), args);
    }
}
