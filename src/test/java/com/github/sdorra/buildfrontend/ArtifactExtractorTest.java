package com.github.sdorra.buildfrontend;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.junit.Before;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactExtractorTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private Directories directories;

    @Mock
    private ArchiverManager archiverManager;

    @InjectMocks
    private ArtifactExtractor artifactExtractor;

    @Mock
    private UnArchiver unArchiver;

    @Mock
    private Artifact artifact;


    @Before
    public void setUp() throws IOException {
        when(artifact.getArtifactId()).thenReturn("special");
    }

    @Test
    public void testExtractIfNeeded() throws IOException {
        File directory = artifactExtractor.extractIfNeeded(artifact);
        assertEquals("special", directory.getName());
    }

    @Test
    public void testExtractIfNeededAlreadyExtracted() throws IOException {
        File directory = new File("special");

        File returned = artifactExtractor.extractIfNeeded(artifact);
        assertEquals(directory, returned);
    }
}