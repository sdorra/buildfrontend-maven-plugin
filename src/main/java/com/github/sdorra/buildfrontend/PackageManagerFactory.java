package com.github.sdorra.buildfrontend;

import org.apache.maven.artifact.Artifact;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;

@Named
public class PackageManagerFactory {

    @Inject
    private ArtifactBuilder artifactBuilder;

    @Inject
    private ArtifactDownloader artifactDownloader;

    @Inject
    private ArtifactExtractor artifactExtractor;

    public PackageManager create(PackageManagerConfiguration configuration, Node node) throws IOException {
        String executable = installPackageManager(configuration, node);
        switch (configuration.getType()) {
            case NPM:
                return new NpmPackageManager(node, executable);
            case YARN:
                return new YarnPackageManager(node, executable);
        }
        throw new IllegalArgumentException("unknown package manager type");
    }

    private String installPackageManager(PackageManagerConfiguration configuration, Node node) throws IOException {
        PackageManagerType type = configuration.getType();
        String version = configuration.getVersion();

        Artifact artifact = createArtifact(type, version);
        artifactDownloader.downloadIfNeeded(artifact, type.createUrl(version));
        File directory = artifactExtractor.extractIfNeeded(artifact);

        File executable = findFile(type.getCliPaths(), directory);
        if (executable == null) {
            throw new IOException("could not find npm");
        }

        return executable.getPath();
    }

    private PackageManager createYarn(PackageManagerConfiguration configuration, Node node) throws IOException {
        PackageManagerType type = configuration.getType();
        String version = configuration.getVersion();

        Artifact artifact = createArtifact(type, version);
        artifactDownloader.downloadIfNeeded(artifact, type.createUrl(version));
        File directory = artifactExtractor.extractIfNeeded(artifact);

        File npm = findFile(type.getCliPaths(), directory);
        if (npm == null) {
            throw new IOException("could not find npm");
        }
        return new YarnPackageManager(node, npm.getPath());
    }

    private PackageManager createNpm(PackageManagerConfiguration configuration, Node node) throws IOException {
        PackageManagerType type = configuration.getType();
        String version = configuration.getVersion();

        Artifact artifact = createArtifact(type, version);
        artifactDownloader.downloadIfNeeded(artifact, type.createUrl(version));
        File directory = artifactExtractor.extractIfNeeded(artifact);

        File npm = findFile(type.getCliPaths(), directory);
        if (npm == null) {
            throw new IOException("could not find npm");
        }
        return new NpmPackageManager(node, npm.getPath());
    }

    private Artifact createArtifact(PackageManagerType type, String version) {
        return artifactBuilder.builder(type.getArtifactId(), version, type.getPackaging()).build();
    }

    private File findFile(String[] paths, File directory) {
        File cli = null;
        for (String bin : paths) {
            File f = new File(directory, bin);
            if (f.exists()) {
                cli = f;
                break;
            }
        }
        return cli;
    }
}
