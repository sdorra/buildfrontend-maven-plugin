package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Node;
import com.github.sdorra.buildfrontend.PackageManager;
import com.github.sdorra.buildfrontend.PackageManagerConfiguration;
import com.github.sdorra.buildfrontend.PackageManagerFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

public abstract class AbstractPackageManagerMojo extends AbstractNodeMojo {

    @Parameter(alias = "pkgManager")
    private PackageManagerConfiguration packageManagerConfiguration;

    @Component
    private PackageManagerFactory packageManagerFactory;

    @VisibleForTesting
    void setPackageManagerConfiguration(PackageManagerConfiguration packageManagerConfiguration) {
        this.packageManagerConfiguration = packageManagerConfiguration;
    }

    @VisibleForTesting
    void setPackageManagerFactory(PackageManagerFactory packageManagerFactory) {
        this.packageManagerFactory = packageManagerFactory;
    }

    @Override
    protected void execute(Node node) throws MojoExecutionException, MojoFailureException {
        try {
            PackageManager packageManager = packageManagerFactory.create(packageManagerConfiguration, node);
            execute(node, packageManager);
        } catch (IOException e) {
            throw new MojoExecutionException("failed to create package manager", e);
        }
    }

    protected abstract void execute(Node node, PackageManager packageManager) throws MojoExecutionException, MojoFailureException;
}
