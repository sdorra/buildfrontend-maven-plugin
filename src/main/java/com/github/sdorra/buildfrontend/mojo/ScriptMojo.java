package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Node;
import com.github.sdorra.buildfrontend.PackageManager;
import com.github.sdorra.buildfrontend.ScriptRunner;
import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "run", defaultPhase = LifecyclePhase.COMPILE)
public class ScriptMojo extends AbstractPackageManagerMojo {

    @VisibleForTesting
    static final String THREAD_NAME = "NodeScript";

    @Parameter(required = true)
    private String script;

    @VisibleForTesting
    void setScript(String script) {
        this.script = script;
    }

    @Parameter
    private String[] args;

    @VisibleForTesting
    void setArgs(String[] args) {
        this.args = args;
    }

    @Parameter
    private boolean background = false;

    @VisibleForTesting
    void setBackground(boolean background) {
        this.background = background;
    }

    @Parameter
    private boolean ignoreFailure = false;

    @VisibleForTesting
    void setIgnoreFailure(boolean ignoreFailure) {
        this.ignoreFailure = ignoreFailure;
    }

    @Override
    protected void execute(Node node, final PackageManager packageManager) {
        if (background) {
            runInBackground(packageManager);
        } else {
            run(packageManager);
        }
    }

    private void run(PackageManager packageManager) {
        ScriptRunner runner = packageManager.script(this.script);
        if (ignoreFailure) {
            runner.ignoreFailure();
        }
        runner.execute(args);
    }

    private void runInBackground(final PackageManager packageManager) {
        Thread thread = new Thread(() -> run(packageManager), THREAD_NAME);
        thread.start();
    }
}
