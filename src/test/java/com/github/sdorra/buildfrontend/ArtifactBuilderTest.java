package com.github.sdorra.buildfrontend;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.repository.RepositorySystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactBuilderTest {

    @Mock
    private RepositorySystem repositorySystem;

    @InjectMocks
    private ArtifactBuilder artifactBuilder;

    @Mock
    private Artifact artifact;

    @Test
    public void testBuildWithClassifier() {
        when(repositorySystem.createArtifactWithClassifier("com.github.sdorra", "test", "1.0.0", "jar", "arm")).thenReturn(artifact);
        Artifact a = artifactBuilder.builder("test", "1.0.0", "jar").withClassifier("arm").build();
        assertSame(artifact, a);
    }

    @Test
    public void testBuildWithoutClassifier() {
        when(repositorySystem.createArtifact("com.github.sdorra", "test", "1.0.0", "jar")).thenReturn(artifact);
        Artifact a = artifactBuilder.builder("test", "1.0.0", "jar").build();
        assertSame(artifact, a);
    }
}