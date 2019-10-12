package com.github.sdorra.buildfrontend;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class NpmPackageManager extends AbstractPackageManager implements PackageManager {

    private final Node node;
    private final File executable;

    NpmPackageManager(Node node, File executable) {
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
    public ScriptRunner script(String script) {
        return new NpmScriptRunner(script);
    }

    @Override
    public void link() {
        npm( "link");
    }

    @Override
    public void link(String pkg) {
        npm("link", pkg);
    }

    @Override
    public void publish(String version) {
        publish(node, version, oldVersion -> npm("publish"));
    }

    private String[] args(String... args) {
        List<String> lists = Lists.newArrayList("--color=false", "--parseable");
        Collections.addAll(lists, args);
        return lists.toArray(new String[0]);
    }

    private void npm(String... args) {
        node.builder()
                .prependBinaryToPath(executable.getParent())
                .execute(executable.getPath(), args(args));
    }

    private class NpmScriptRunner implements ScriptRunner {

        private final String script;
        private NodeExecutionBuilder nodeExecutionBuilder;

        private NpmScriptRunner(String script) {
            this.script = script;
            nodeExecutionBuilder = node.builder()
                    .prependBinaryToPath(executable.getParent());
        }

        @Override
        public ScriptRunner ignoreFailure() {
            nodeExecutionBuilder.ignoreFailure();
            return this;
        }

        @Override
        public void execute() {
            nodeExecutionBuilder.execute(executable.getPath(), args("run", script));
        }
    }
}
