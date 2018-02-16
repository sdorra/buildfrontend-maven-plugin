package com.github.sdorra.buildfrontend;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;
import org.apache.maven.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

@Named
public class NodeFactory {

    private static final String PATH_NODE_BIN = String.format("bin%snode", File.separator);

    @VisibleForTesting
    static final String ARTIFACT_ID = "buildfrontend-node";

    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);

    private Directories directories;
    private ArtifactBuilder artifactBuilder;
    private ArtifactExtractor artifactExtractor;
    private ArtifactDownloader artifactDownloader;

    @Inject
    public NodeFactory(Directories directories, ArtifactBuilder artifactBuilder, ArtifactExtractor artifactExtractor, ArtifactDownloader artifactDownloader) {
        this.directories = directories;
        this.artifactBuilder = artifactBuilder;
        this.artifactExtractor = artifactExtractor;
        this.artifactDownloader = artifactDownloader;
    }

    public Node create(NodeConfiguration nodeConfiguration) throws IOException {
        return create(nodeConfiguration, NodePlatform.current());
    }

    @VisibleForTesting
    Node create(NodeConfiguration nodeConfiguration, NodePlatform nodePlatform) throws IOException {
        String version = nodeConfiguration.getVersion();
        Artifact artifact = createNodeArtifact(nodePlatform, version);
        artifactDownloader.downloadIfNeeded(artifact, nodePlatform.getNodeUrl(version));

        File executable = extractOrCopyNode(nodePlatform, artifact);
        return new Node(new File(directories.getWorkingDirectory()), executable);
    }

    private Artifact createNodeArtifact(NodePlatform nodePlatform, String version) {
        return artifactBuilder.builder(ARTIFACT_ID, version, nodePlatform.getNodePackageType())
                .withClassifier(nodePlatform.getClassifier())
                .build();
    }

    private File extractOrCopyNode(NodePlatform nodePlatform, Artifact artifact) throws IOException {
        File buildDirectory = new File(directories.getBuildDirectory());
        if (!buildDirectory.exists() && !buildDirectory.mkdirs()) {
            throw new IOException("failed to create build directory");
        }

        if (nodePlatform.isNodeUnpacked()) {
            File nodeFile = new File(directories.getBuildDirectory(), nodePlatform.getExecutableName());

            LOG.info("copy node to {}", nodeFile);
            Files.copy(artifact.getFile(), nodeFile);
            return nodeFile;
        } else {
            File directory = artifactExtractor.extractIfNeeded(artifact);
            return findNodeExecutable(directory, artifact.getVersion());
        }
    }

    private File findNodeExecutable(File directory, String nodeVersion) throws IOException {
        LOG.debug("searching node version {} at directory {}", nodeVersion, directory);

        String pattern = String.format("node-(v)?%s.*", Pattern.quote(nodeVersion));
        File nodeDirectory = null;
        for (File f : directory.listFiles()) {
            if (f.getName().matches(pattern)) {
                nodeDirectory = f;
                break;
            }
        }

        if (nodeDirectory == null) {
            throw new IOException("could not find node directory");
        }

        File node = new File(nodeDirectory, PATH_NODE_BIN);
        if  (!node.exists()) {
            throw new IOException("could not find node executable");
        }

        return node;
    }

}
