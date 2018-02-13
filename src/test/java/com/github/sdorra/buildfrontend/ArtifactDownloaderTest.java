package com.github.sdorra.buildfrontend;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.repository.RepositorySystem;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactDownloaderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private ArtifactInstaller artifactInstaller;

    @Mock
    private RepositorySystem repositorySystem;

    @InjectMocks
    private ArtifactDownloader artifactDownloader;

    @Mock
    private ArtifactRepository artifactRepository;

    @Mock
    private Artifact artifact;

    @Before
    public void setUp() throws InvalidRepositoryException {
        when(artifact.getArtifactId()).thenReturn("unit-test");
        when(artifact.getType()).thenReturn("jar");
        when(repositorySystem.createDefaultLocalRepository()).thenReturn(artifactRepository);
    }

    @Test
    public void testDownloadIfNeededIsAlreadyDownloaded() throws Exception {
        when(artifact.getFile()).thenReturn(temporaryFolder.newFile());
        when(artifactRepository.find(artifact)).thenReturn(artifact);

        artifactDownloader.downloadIfNeeded(artifact, "https://hitchhiker.com");

        verify(artifactInstaller, never()).install(any(File.class), any(Artifact.class), any(ArtifactRepository.class));
    }

    @Test
    public void testDownloadIfNeededWhenItIsNeeded() throws ArtifactInstallationException, IOException {
        File file = temporaryFolder.newFile();
        Files.write("abc", file, Charsets.UTF_8);

        when(artifactRepository.find(artifact)).thenReturn(artifact);

        artifactDownloader.downloadIfNeeded(artifact, file.toURI().toURL().toExternalForm());

        verify(artifactInstaller).install(any(File.class), any(Artifact.class), any(ArtifactRepository.class));
    }

    @Test(expected = RuntimeException.class)
    public void testDownloadIfNeededWithError() throws IOException {
        File file = new File("/totally/invalid/path");

        when(artifactRepository.find(artifact)).thenReturn(artifact);

        artifactDownloader.downloadIfNeeded(artifact, file.toURI().toURL().toExternalForm());
    }

}