package com.github.sdorra.buildfrontend;

import com.google.common.io.Files;
import org.apache.maven.artifact.Artifact;
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
        NodePlatform nodePlatform = NodePlatform.current();

        String version = nodeConfiguration.getVersion();
        Artifact artifact = createNodeArtifact(nodePlatform, version);
        artifactDownloader.downloadIfNeeded(artifact, nodePlatform.getNodeUrl(version));

        File executable = extractNode(nodePlatform, artifact);
        return new Node(new File(directories.getWorkingDirectory()), executable);
    }

    private Artifact createNodeArtifact(NodePlatform nodePlatform, String version) {
        return artifactBuilder.builder(ARTIFACT_ID, version, nodePlatform.getNodePackageType())
                .withClassifier(nodePlatform.getClassifier())
                .build();
    }

    private File extractNode(NodePlatform nodePlatform, Artifact artifact) throws IOException {
        if (nodePlatform.isNodeUnpacked()) {
            File nodeFile = new File(directories.getBuildDirectory(), nodePlatform.getExecutableName());

            LOG.info("copy node to {}", nodeFile);
            Files.copy(artifact.getFile(), nodeFile);
            return nodeFile;
        } else {
            File directory = artifactExtractor.extractIfNeeded(artifact);
            return findNodeExecutable(nodePlatform, directory, artifact.getVersion());
        }
    }

    private File findNodeExecutable(NodePlatform nodePlatform, File directory, String nodeVersion) throws IOException {
        File node = null;

        if (nodePlatform.isNodeUnpacked()) {
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
