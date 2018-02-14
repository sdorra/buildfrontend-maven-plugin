package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.*;
import org.mockito.Mock;

import java.io.IOException;

import static org.mockito.Mockito.when;

public abstract class AbstractPackageManagerMojoTestBase {

    @Mock
    private NodeFactory nodeFactory;

    @Mock
    private Node node;

    private NodeConfiguration nodeConfiguration = new NodeConfiguration();

    @Mock
    private PackageManagerFactory packageManagerFactory;

    @Mock
    protected PackageManager packageManager;

    private PackageManagerConfiguration packageManagerConfiguration = new PackageManagerConfiguration();

    protected void preparePackaManagerMojo(AbstractPackageManagerMojo pkgMojo) throws IOException {
        prepareNodeMojo(pkgMojo);

        when(packageManagerFactory.create(packageManagerConfiguration, node)).thenReturn(packageManager);
        pkgMojo.setPackageManagerFactory(packageManagerFactory);
        pkgMojo.setPackageManagerConfiguration(packageManagerConfiguration);
    }

    private void prepareNodeMojo(AbstractNodeMojo nodeMojo) throws IOException {
        prepareDirectoryMojo(nodeMojo);

        when(nodeFactory.create(nodeConfiguration)).thenReturn(node);
        nodeMojo.setNodeFactory(nodeFactory);
        nodeMojo.setNodeConfiguration(nodeConfiguration);
    }

    private void prepareDirectoryMojo(AbstractDirectoryMojo directoryMojo) {
        directoryMojo.setDirectories(new Directories());
        directoryMojo.setBuildDirectory("build");
        directoryMojo.setWorkingDirectory("work");
    }

}
