package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Directories;
import com.github.sdorra.buildfrontend.Node;
import com.github.sdorra.buildfrontend.NodeConfiguration;
import com.github.sdorra.buildfrontend.NodeFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

public abstract class AbstractNodeMojo extends AbstractDirectoryMojo {

    @Parameter(alias = "node")
    private NodeConfiguration nodeConfiguration;

    @Component
    private NodeFactory nodeFactory;

    @Override
    public void execute(Directories directories) throws MojoExecutionException, MojoFailureException {
        try {
            Node node = nodeFactory.create(nodeConfiguration);
            execute(node);
        } catch (IOException e) {
            throw new MojoExecutionException("failed to create node", e);
        }
    }

    protected abstract void execute(Node node) throws MojoExecutionException, MojoFailureException;
}
