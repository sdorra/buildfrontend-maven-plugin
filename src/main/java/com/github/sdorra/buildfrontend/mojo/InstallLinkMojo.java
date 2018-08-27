package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Node;
import com.github.sdorra.buildfrontend.PackageManager;
import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "install-link", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class InstallLinkMojo extends AbstractPackageManagerMojo {

    @Parameter(required = true)
    private String pkg;

    @VisibleForTesting
    void setPkg(String pkg) {
        this.pkg = pkg;
    }

    @Override
    protected void execute(Node node, PackageManager packageManager) {
        packageManager.link(pkg);
    }
}
