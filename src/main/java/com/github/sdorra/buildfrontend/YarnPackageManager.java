package com.github.sdorra.buildfrontend;

import java.io.File;

public class YarnPackageManager extends AbstractPackageManager implements PackageManager {

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
    public ScriptRunner script(String script) {
        return new YarnScriptRunner(script);
    }

    @Override
    public void link() {
        yarn("link");
    }

    @Override
    public void link(String pkg) {
        yarn("link", pkg);
    }

    @Override
    public void publish(String version) {
        publish(node, version, oldVersion -> yarn("publish", "--new-version", version));
    }

    private void yarn(String... args) {
        node.builder()
                .prependBinaryToPath(executable.getParent())
                .execute(executable.getPath(), args);
    }

    private class YarnScriptRunner implements ScriptRunner {

        private final String script;
        private NodeExecutionBuilder nodeExecutionBuilder;

        private YarnScriptRunner(String script) {
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
            nodeExecutionBuilder.execute(executable.getPath(), "run", script);
        }
    }
}
