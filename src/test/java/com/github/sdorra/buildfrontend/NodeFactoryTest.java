package com.github.sdorra.buildfrontend;

import org.apache.maven.artifact.Artifact;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private Directories directories;

    @Mock
    private ArtifactBuilder artifactBuilder;

    @Mock
    private ArtifactExtractor artifactExtractor;

    @Mock
    private ArtifactDownloader artifactDownloader;

    @InjectMocks
    private NodeFactory nodeFactory;

    @Mock
    private ArtifactBuilder.ArtifactBuilderFinalizer finalizer;

    @Mock
    private Artifact artifact;

    @Test
    public void testCreate() throws IOException {
        File workingDirectory = temporaryFolder.newFolder();
        when(directories.getWorkingDirectory()).thenReturn(workingDirectory.getPath());

        File extractedDirectory = temporaryFolder.newFolder();
        when(artifactExtractor.extractIfNeeded(artifact)).thenReturn(extractedDirectory);

        File nodeDirectory = new File(extractedDirectory, "node-v8.9.4");
        assertTrue(nodeDirectory.mkdir());

        File binDirectory = new File(nodeDirectory, "bin");
        assertTrue(binDirectory.mkdir());

        File nodeFile = new File(binDirectory, "node");
        assertTrue(nodeFile.mkdir());

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

}