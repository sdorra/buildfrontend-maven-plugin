package com.github.sdorra.buildfrontend;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.maven.artifact.Artifact;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Directories directories;

    @Mock
    private ArtifactBuilder artifactBuilder;

    @Mock
    private ArtifactExtractor artifactExtractor;

    @Mock
    private ArtifactDownloader artifactDownloader;

    private NodeFactory nodeFactory;

    @Mock
    private ArtifactBuilder.ArtifactBuilderFinalizer finalizer;

    @Mock
    private Artifact artifact;

    @Before
    public void setUp() throws IOException {
        directories = new Directories();

        File workingDirectory = temporaryFolder.newFolder();
        directories.setWorkingDirectory(workingDirectory.getPath());

        File buildDirectory = temporaryFolder.newFolder();
        directories.setBuildDirectory(buildDirectory.getPath());

        nodeFactory = new NodeFactory(directories, artifactBuilder, artifactExtractor, artifactDownloader);
    }

    @Test
    public void testCreate() throws IOException {
        File extractedDirectory = temporaryFolder.newFolder();
        when(artifactExtractor.extractIfNeeded(artifact)).thenReturn(extractedDirectory);

        File nodeDirectory = new File(extractedDirectory, "node-v8.9.4");
        assertTrue(nodeDirectory.mkdir());

        File binDirectory = new File(nodeDirectory, "bin");
        assertTrue(binDirectory.mkdir());

        File nodeFile = new File(binDirectory, "node");
        assertTrue(nodeFile.createNewFile());

        when(artifact.getVersion()).thenReturn("v8.9.4");

        NodePlatform platform = NodePlatform.LINUX_X64;

        when(finalizer.build()).thenReturn(artifact);
        when(finalizer.withClassifier(platform.getClassifier())).thenReturn(finalizer);
        when(artifactBuilder.builder(NodeFactory.ARTIFACT_ID, "v8.9.4", platform.getNodePackageType())).thenReturn(finalizer);

        NodeConfiguration configuration = new NodeConfiguration();
        configuration.setVersion("v8.9.4");

        Node node = nodeFactory.create(configuration, platform);
        assertNotNull(node);

        verify(artifactDownloader).downloadIfNeeded(artifact, platform.getNodeUrl("v8.9.4"));
    }

    @Test
    public void testCreateOnMacOsX() throws IOException {
        File extractedDirectory = temporaryFolder.newFolder();
        when(artifactExtractor.extractIfNeeded(artifact)).thenReturn(extractedDirectory);

        File nodeDirectory = new File(extractedDirectory, "node-v8.9.4-darwin-x64");
        assertTrue(nodeDirectory.mkdir());

        File binDirectory = new File(nodeDirectory, "bin");
        assertTrue(binDirectory.mkdir());

        File nodeFile = new File(binDirectory, "node");
        assertTrue(nodeFile.createNewFile());

        when(artifact.getVersion()).thenReturn("v8.9.4");

        NodePlatform platform = NodePlatform.MACOS_X64;

        when(finalizer.build()).thenReturn(artifact);
        when(finalizer.withClassifier(platform.getClassifier())).thenReturn(finalizer);
        when(artifactBuilder.builder(NodeFactory.ARTIFACT_ID, "v8.9.4", platform.getNodePackageType())).thenReturn(finalizer);

        NodeConfiguration configuration = new NodeConfiguration();
        configuration.setVersion("v8.9.4");

        Node node = nodeFactory.create(configuration, platform);
        assertNotNull(node);

        verify(artifactDownloader).downloadIfNeeded(artifact, platform.getNodeUrl("v8.9.4"));
    }

    @Test
    public void createOnUnpackedPlatform() throws IOException {
        File nodeBinary = temporaryFolder.newFile();
        Files.write("xyz", nodeBinary, Charsets.UTF_8);

        when(artifact.getFile()).thenReturn(nodeBinary);

        NodePlatform platform = NodePlatform.WINDOWS_X64;

        when(finalizer.build()).thenReturn(artifact);
        when(finalizer.withClassifier(platform.getClassifier())).thenReturn(finalizer);
        when(artifactBuilder.builder(NodeFactory.ARTIFACT_ID, "v8.9.4", platform.getNodePackageType())).thenReturn(finalizer);

        NodeConfiguration configuration = new NodeConfiguration();
        configuration.setVersion("v8.9.4");

        Node node = nodeFactory.create(configuration, platform);
        assertNotNull(node);
        assertEquals("xyz", Files.toString(node.getExecutable(), Charsets.UTF_8));

        verify(artifactDownloader).downloadIfNeeded(artifact, platform.getNodeUrl("v8.9.4"));
    }

    @Test
    public void testCreateWithoutNodeDirectory() throws IOException {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("directory");

        File extractedDirectory = temporaryFolder.newFolder();
        when(artifactExtractor.extractIfNeeded(artifact)).thenReturn(extractedDirectory);

        when(artifact.getVersion()).thenReturn("v8.9.4");

        NodePlatform platform = NodePlatform.LINUX_X64;

        when(finalizer.build()).thenReturn(artifact);
        when(finalizer.withClassifier(platform.getClassifier())).thenReturn(finalizer);
        when(artifactBuilder.builder(NodeFactory.ARTIFACT_ID, "v8.9.4", platform.getNodePackageType())).thenReturn(finalizer);

        NodeConfiguration configuration = new NodeConfiguration();
        configuration.setVersion("v8.9.4");

        nodeFactory.create(configuration, platform);
    }

    @Test
    public void testCreateWithoutNodeExecutable() throws IOException {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("executable");

        File extractedDirectory = temporaryFolder.newFolder();
        when(artifactExtractor.extractIfNeeded(artifact)).thenReturn(extractedDirectory);

        File nodeDirectory = new File(extractedDirectory, "node-v8.9.4");
        assertTrue(nodeDirectory.mkdir());

        when(artifact.getVersion()).thenReturn("v8.9.4");

        NodePlatform platform = NodePlatform.LINUX_X64;

        when(finalizer.build()).thenReturn(artifact);
        when(finalizer.withClassifier(platform.getClassifier())).thenReturn(finalizer);
        when(artifactBuilder.builder(NodeFactory.ARTIFACT_ID, "v8.9.4", platform.getNodePackageType())).thenReturn(finalizer);

        NodeConfiguration configuration = new NodeConfiguration();
        configuration.setVersion("v8.9.4");

        nodeFactory.create(configuration, platform);
    }

}