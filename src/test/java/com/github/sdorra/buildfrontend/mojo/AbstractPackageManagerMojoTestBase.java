package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.when;

public abstract class AbstractPackageManagerMojoTestBase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    NodeFactory nodeFactory;

    @Mock
    private Node node;

    NodeConfiguration nodeConfiguration = new NodeConfiguration();

    @Mock
    private PackageManagerFactory packageManagerFactory;

    @Mock
    PackageManager packageManager;

    private PackageManagerConfiguration packageManagerConfiguration = new PackageManagerConfiguration();

    void preparePackageManagerMojo(AbstractPackageManagerMojo pkgMojo) throws IOException {
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

    void prepareDirectoryMojo(AbstractDirectoryMojo directoryMojo) throws IOException {
        directoryMojo.setDirectories(new Directories());

        File work = temporaryFolder.newFolder("work");
        File packageJson = new File(work, AbstractNodeMojo.PACKAGE_JSON);
        if (!packageJson.createNewFile()) {
            throw new IOException("could not create " + AbstractNodeMojo.PACKAGE_JSON);
        }
        directoryMojo.setWorkingDirectory(work.getPath());

        File build = temporaryFolder.newFolder("build");
        directoryMojo.setBuildDirectory(build.getPath());
    }

    void deletePackageJson(AbstractDirectoryMojo mojo) throws IOException {
        if (!new File(mojo.getWorkingDirectory(), AbstractNodeMojo.PACKAGE_JSON).delete()) {
            throw new IOException("failed to delete " + AbstractNodeMojo.PACKAGE_JSON);
        }
    }

}
