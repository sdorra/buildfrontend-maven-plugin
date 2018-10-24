package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Node;
import com.github.sdorra.buildfrontend.PackageManager;
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
    private boolean background = false;

    @VisibleForTesting
    void setBackground(boolean background) {
        this.background = background;
    }

    @Override
    protected void execute(Node node, final PackageManager packageManager) {
        if (background) {
            runInBackground(packageManager);
        } else {
            packageManager.run(script);
        }
    }

    private void runInBackground(final PackageManager packageManager) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                packageManager.run(script);
            }
        }, THREAD_NAME);
        thread.start();
    }
}
