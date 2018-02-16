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
public class PackageManagerFactoryTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private ArtifactBuilder artifactBuilder;

    @Mock
    private ArtifactDownloader artifactDownloader;

    @Mock
    private ArtifactExtractor artifactExtractor;

    @InjectMocks
    private PackageManagerFactory packageManagerFactory;

    @Mock
    private ArtifactBuilder.ArtifactBuilderFinalizer finalizer;

    @Mock
    private Artifact artifact;

    @Mock
    private Node node;

    @Test
    public void testCreateNpm() throws IOException {
        PackageManagerType type = PackageManagerType.NPM;
        String version = "5.6.0";

        PackageManagerConfiguration configuration = new PackageManagerConfiguration();
        configuration.setType(type);
        configuration.setVersion(version);

        when(artifactBuilder.builder(type.getArtifactId(), version, type.getPackaging())).thenReturn(finalizer);
        when(finalizer.build()).thenReturn(artifact);

        File extractedFolder = temporaryFolder.newFolder();

        File npmFolder = new File(extractedFolder,"npm-5.6.0");
        assertTrue(npmFolder.mkdir());

        File binFolder = new File(npmFolder, "bin");
        assertTrue(binFolder.mkdir());

        File cliFile = new File(binFolder, "npm-cli.js");
        assertTrue(cliFile.createNewFile());

        when(artifactExtractor.extractIfNeeded(artifact)).thenReturn(extractedFolder);

        PackageManager packageManager = packageManagerFactory.create(configuration, node);
        assertNotNull(packageManager);
        assertTrue(packageManager instanceof NpmPackageManager);

        verify(artifactDownloader).downloadIfNeeded(artifact, type.createUrl(version));
    }

    @Test
    public void testCreateYarn() throws IOException {
        PackageManagerType type = PackageManagerType.YARN;
        String version = "1.3.2";

        PackageManagerConfiguration configuration = new PackageManagerConfiguration();
        configuration.setType(type);
        configuration.setVersion(version);

        when(artifactBuilder.builder(type.getArtifactId(), version, type.getPackaging())).thenReturn(finalizer);
        when(finalizer.build()).thenReturn(artifact);

        File extractedFolder = temporaryFolder.newFolder();

        File yarnFolder = new File(extractedFolder,"yarn-v1.3.2");
        assertTrue(yarnFolder.mkdir());

        File binFolder = new File(yarnFolder, "bin");
        assertTrue(binFolder.mkdir());

        File cliFile = new File(binFolder, "yarn.js");
        assertTrue(cliFile.createNewFile());

        when(artifactExtractor.extractIfNeeded(artifact)).thenReturn(extractedFolder);

        PackageManager packageManager = packageManagerFactory.create(configuration, node);
        assertNotNull(packageManager);
        assertTrue(packageManager instanceof YarnPackageManager);

        verify(artifactDownloader).downloadIfNeeded(artifact, type.createUrl(version));
    }

    @Test(expected = IOException.class)
    public void testCreateMissingBinary() throws IOException {
        PackageManagerType type = PackageManagerType.YARN;
        String version = "1.3.2";

        PackageManagerConfiguration configuration = new PackageManagerConfiguration();
        configuration.setType(type);
        configuration.setVersion(version);

        when(artifactBuilder.builder(type.getArtifactId(), version, type.getPackaging())).thenReturn(finalizer);
        when(finalizer.build()).thenReturn(artifact);

        File extractedFolder = temporaryFolder.newFolder();
        when(artifactExtractor.extractIfNeeded(artifact)).thenReturn(extractedFolder);

        packageManagerFactory.create(configuration, node);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUnknownType() throws IOException {
        packageManagerFactory.create(new PackageManagerConfiguration(), node);
    }

}