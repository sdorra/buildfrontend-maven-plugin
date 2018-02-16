package com.github.sdorra.buildfrontend;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;

@Named
public class ArtifactExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactExtractor.class);

    private final Directories directories;
    private final ArchiverManager archiverManager;

    @Inject
    public ArtifactExtractor(Directories directories, ArchiverManager archiverManager) {
        this.directories = directories;
        this.archiverManager = archiverManager;
    }

    public File extractIfNeeded(Artifact artifact) throws IOException {
        File directory = new File(directories.getBuildDirectory(), artifact.getArtifactId());
        if (directory.exists()){
            LOG.info("skip extraction of {}", artifact);
        } else {
            extract(artifact, directory);
        }
        return directory;
    }

    private void extract(Artifact artifact, File directory) throws IOException {
        if (!directory.mkdirs()) {
            throw new IOException("failed to create directory for extraction");
        }
        extract(artifact.getFile(), directory);
    }

    private void extract(File archive, File target) throws IOException {
        LOG.info("extract {} to {}", archive, target);

        try {
            UnArchiver unarchiver = archiverManager.getUnArchiver(archive);

            unarchiver.setSourceFile(archive);
            unarchiver.setDestDirectory(target);
            unarchiver.extract();
        } catch (NoSuchArchiverException ex) {
            throw new IOException("could not find unarchiver", ex);
        }
    }

}
