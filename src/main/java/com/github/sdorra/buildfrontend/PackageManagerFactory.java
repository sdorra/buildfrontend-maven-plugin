package com.github.sdorra.buildfrontend;

import org.apache.maven.artifact.Artifact;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

@Named
public class PackageManagerFactory {

    private ArtifactBuilder artifactBuilder;
    private ArtifactDownloader artifactDownloader;
    private ArtifactExtractor artifactExtractor;

    @Inject
    public PackageManagerFactory(ArtifactBuilder artifactBuilder, ArtifactDownloader artifactDownloader, ArtifactExtractor artifactExtractor) {
        this.artifactBuilder = artifactBuilder;
        this.artifactDownloader = artifactDownloader;
        this.artifactExtractor = artifactExtractor;
    }

    public PackageManager create(PackageManagerConfiguration configuration, Node node) throws IOException {
        if (configuration.getType() == PackageManagerType.NPM) {
            return createNpmPackageManager(configuration, node);
        } else if (configuration.getType() == PackageManagerType.YARN) {
            return createYarnPackageManager(configuration, node);
        } else {
            throw new IllegalArgumentException("unknown package manager type");
        }
    }

    private PackageManager createNpmPackageManager(PackageManagerConfiguration configuration, Node node) throws IOException {
        String executable = installPackageManager(configuration);
        return new NpmPackageManager(node, executable);
    }

    private PackageManager createYarnPackageManager(PackageManagerConfiguration configuration, Node node) throws IOException {
        String executable = installPackageManager(configuration);
        return new YarnPackageManager(node, executable);
    }

    private String installPackageManager(PackageManagerConfiguration configuration) throws IOException {
        PackageManagerType type = configuration.getType();
        String version = configuration.getVersion();

        Artifact artifact = createArtifact(type, version);
        artifactDownloader.downloadIfNeeded(artifact, type.createUrl(version));
        File directory = artifactExtractor.extractIfNeeded(artifact);

        File packageManagerDirectory = findDirectory(configuration, directory);
        File executable = findFile(type.getCliPaths(), packageManagerDirectory);
        if (executable == null) {
            throw new IOException("could not find package manager executable");
        }

        return executable.getPath();
    }

    private Artifact createArtifact(PackageManagerType type, String version) {
        return artifactBuilder.builder(type.getArtifactId(), version, type.getPackaging()).build();
    }

    private File findDirectory(PackageManagerConfiguration configuration, File directory) {
        String typeString = configuration.getType().name().toLowerCase(Locale.ENGLISH);
        String pattern = String.format("%s-[v]?%s", typeString, Pattern.quote(configuration.getVersion()));

        for (File child : directory.listFiles()) {
            String fileName = child.getName();
            if (child.isDirectory() && fileName.matches(pattern)) {
                return child;
            }
        }
        return directory;
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
