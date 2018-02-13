package com.github.sdorra.buildfrontend;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.archiver.Archiver;
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
import static org.mockito.Mockito.verify;
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

    private File buildDirectory;
    private File artifactFile;

    @Before
    public void setUp() throws IOException {
        buildDirectory = temporaryFolder.newFolder();
        artifactFile = temporaryFolder.newFile();

        when(artifact.getFile()).thenReturn(artifactFile);
        when(directories.getBuildDirectory()).thenReturn(buildDirectory.getPath());
        when(artifact.getArtifactId()).thenReturn("special");
    }

    @Test
    public void testExtractIfNeeded() throws IOException, NoSuchArchiverException {
        when(archiverManager.getUnArchiver(artifactFile)).thenReturn(unArchiver);

        File directory = artifactExtractor.extractIfNeeded(artifact);
        assertEquals(buildDirectory, directory.getParentFile());
        assertEquals("special", directory.getName());

        verify(unArchiver).extract();
    }

    @Test
    public void testExtractIfNeededAlreadyExtracted() throws IOException {
        File directory = new File(buildDirectory, "special");
        assertTrue(directory.mkdir());

        File returned = artifactExtractor.extractIfNeeded(artifact);
        assertEquals(directory, returned);
    }

    @Test
    public void testExtractIfNeededAlreadyExtractedWithSingleSubDirectory() throws IOException {
        File directory = new File(buildDirectory, "special");
        assertTrue(directory.mkdir());

        File ssd = new File(directory, "dir");
        assertTrue(ssd.mkdir());

        File returned = artifactExtractor.extractIfNeeded(artifact);
        assertEquals(ssd, returned);
    }
}