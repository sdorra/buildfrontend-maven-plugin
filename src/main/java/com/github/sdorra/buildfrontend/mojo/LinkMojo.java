package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Node;
import com.github.sdorra.buildfrontend.PackageManager;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "link", defaultPhase = LifecyclePhase.INSTALL)
public class LinkMojo extends AbstractPackageManagerMojo {
    @Override
    protected void execute(Node node, PackageManager packageManager) {
        packageManager.link();
    }
}
