package com.github.sdorra.buildfrontend;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.repository.RepositorySystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Named
public class ArtifactDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactDownloader.class);

    @Inject
    private ArtifactInstaller installer;

    @Inject
    private RepositorySystem repositorySystem;

    public void downloadIfNeeded(Artifact artifact, String url) {
        try  {
            ArtifactRepository repository =  repositorySystem.createDefaultLocalRepository();

            if (!isInstalled(repository, artifact)) {
                downloadAndInstall(artifact, url);
            }  else  {
                LOG.info("{} is already installed", artifact.getId());
            }
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    private void downloadAndInstall(Artifact artifact, String url) throws IOException, ArtifactInstallationException, InvalidRepositoryException {
        File temporaryFile = File.createTempFile(artifact.getArtifactId(), artifact.getType());
        try {
            download(url, temporaryFile);
            installer.install(temporaryFile, artifact, repositorySystem.createDefaultLocalRepository());
        } finally {
            if (!temporaryFile.delete()) {
                LOG.warn("failed to remove temporary file {}", temporaryFile);
            }
        }
    }

    private boolean isInstalled(ArtifactRepository repository, Artifact artifact) {
        boolean result = false;
        Artifact installed = repository.find(artifact);
        if (installed != null)  {
            File file = installed.getFile();
            result = (file != null) && file.exists();
        }
        return result;
    }

    private void download(String urlString, File target) throws IOException {
        LOG.info("download {} from {}", urlString, target);

        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        Closer closer = Closer.create();

        try {
            InputStream input = closer.register(connection.getInputStream());
            OutputStream output = closer.register(new FileOutputStream(target));

            long bytes = ByteStreams.copy(input, output);
            LOG.info("downloaded {} bytes from {}", bytes, urlString);
        } catch (IOException ex)  {
            throw closer.rethrow(ex);
        } finally {
            closer.close();
        }
    }

}
