package com.github.sdorra.buildfrontend;

import com.google.common.io.Files;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;

@Named
public class NodeFactory {

    private static final String PATH_NODE_BIN = String.format("bin%snode", File.separator);
    private static final String ARTIFACT_ID = "buildfrontend-node";

    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);

    @Inject
    private Directories directories;

    @Inject
    private ArtifactBuilder artifactBuilder;

    @Inject
    private ArtifactExtractor artifactExtractor;

    @Inject
    private ArtifactDownloader artifactDownloader;

    public Node create(NodeConfiguration nodeConfiguration) throws IOException {
        Platform platform = Platform.current();

        String version = nodeConfiguration.getVersion();
        Artifact artifact = createNodeArtifact(platform, version);
        artifactDownloader.downloadIfNeeded(artifact, platform.getNodeUrl(version));

        File executable = extractNode(platform, artifact);
        return new Node(new File(directories.getWorkingDirectory()), executable);
    }

    private Artifact createNodeArtifact(Platform platform, String version) {
        return artifactBuilder.builder(ARTIFACT_ID, version, platform.getNodePackageType())
                .withClassifier(platform.getClassifier())
                .build();
    }

    private File extractNode(Platform platform, Artifact artifact) throws IOException {
        if (platform.isNodeUnpacked()) {
            File nodeFile = new File(directories.getBuildDirectory(), platform.getExecutableName());

            LOG.info("copy node to {}", nodeFile);
            Files.copy(artifact.getFile(), nodeFile);
            return nodeFile;
        } else {
            File directory = artifactExtractor.extractIfNeeded(artifact);
            return findNodeExecutable(platform, directory, artifact.getVersion());
        }
    }

    private File findNodeExecutable(Platform platform, File directory, String nodeVersion) throws IOException {
        File node = null;

        if (platform.isNodeUnpacked()) {
            for (File f : directory.listFiles()) {
                if (f.getName().startsWith("node")) {
                    node = f;
                    break;
                }
            }
        } else {
            File nodeDirectory = null;
            for (File f : directory.listFiles()) {
                if (f.getName().startsWith("node-".concat(nodeVersion))) {
                    nodeDirectory = f;
                    break;
                }
            }

            if (nodeDirectory == null) {
                throw new IOException("could not find node directory");
            }

            node = new File(nodeDirectory, PATH_NODE_BIN);
        }

        if ((node == null) ||!node.exists()) {
            throw new IOException("could not find node executable");
        }

        return node;
    }

}
