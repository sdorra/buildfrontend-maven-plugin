package com.github.sdorra.buildfrontend;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class NpmPackageManager implements PackageManager {

    private final Node node;
    private final String executable;

    NpmPackageManager(Node node, String executable) {
        this.node = node;
        this.executable = executable;
    }

    @Override
    public void install() {
        npm("install");
    }

    @Override
    public void run(String script) {
        npm("run", script);
    }

    @Override
    public void link() {
        npm( "link");
    }

    @Override
    public void link(String pkg) {
        npm("link", pkg);
    }

    private void npm(String... args) {
        List<String> lists = Lists.newArrayList("--color=false", "--parseable");
        for ( String arg : args ) {
            lists.add(arg);
        }
        node.execute(executable, lists.toArray(new String[0]));
    }
}
