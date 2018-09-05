package com.github.sdorra.buildfrontend;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NodeExecutionBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(NodeExecutionBuilder.class);

    private static final String ENV_PATH = "PATH";

    private final File workingDirectory;
    private final File node;

    private final Map<String,String> systemEnvironment;
    private final Map<String,String> environment;
    private final List<String> binPath;


    NodeExecutionBuilder(File workingDirectory, File node) {
        this(workingDirectory, node, System.getenv());
    }

    @VisibleForTesting
    NodeExecutionBuilder(File workingDirectory, File node, Map<String, String> systemEnvironment) {
        this.workingDirectory = workingDirectory;
        this.node = node;

        this.systemEnvironment = systemEnvironment;
        this.environment = new HashMap<String, String>();
        this.binPath = parseBinPath(systemEnvironment.get(ENV_PATH));
    }

    private List<String> parseBinPath(String path) {
        // splitter returns an unmodifiable list, so we have to create a new list in order to modify it
        return new ArrayList<String>(
            Splitter.on(File.pathSeparator).omitEmptyStrings().trimResults().splitToList(Strings.nullToEmpty(path))
        );
    }

    public NodeExecutionBuilder appendBinaryToPath(String path) {
        this.binPath.add(path);
        return this;
    }

    public NodeExecutionBuilder prependBinaryToPath(String path) {
        this.binPath.add(0, path);
        return this;
    }

    public NodeExecutionBuilder addEnvironmentVariable(String key, String value) {
        this.environment.put(key, value);
        return this;
    }

    public void execute(String command, String... args) {
        List<String> cmds = createCommand(command, args);
        LOG.info("execute {}", cmds);

        Map<String, String> env = createEnvironment();
        try {
            ProcessExecutor executor = create(env, cmds);
            ProcessResult result = executor.execute();
            int exitValue = result.getExitValue();
            if (exitValue != 0) {
                throw new IOException("process ends with exit value " + exitValue);
            }
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    private ProcessExecutor create(Map<String,String> environment, List<String> cmds) {
        return newExecutor(cmds)
                .directory(workingDirectory)
                .environment(environment)
                .redirectErrorStream(true)
                .redirectOutput(System.out);
    }

    @VisibleForTesting
    ProcessExecutor newExecutor(List<String> cmds) {
        return new ProcessExecutor(cmds);
    }

    private List<String> createCommand(String command, String... args) {
        List<String> cmd = new ArrayList<String>();
        cmd.add(node.getPath());
        cmd.add(command);
        cmd.addAll(Arrays.asList(args));
        return cmd;
    }

    private Map<String,String> createEnvironment() {
        Map<String,String> env = new HashMap<String, String>(systemEnvironment);
        env.putAll(environment);
        env.put(ENV_PATH, createBinPath());

        if (LOG.isDebugEnabled()) {
            LOG.debug(envToString(env));
        }
        return env;
    }

    private String envToString(Map<String, String> env) {
        String separator = System.getProperty("line.separator", "\n");
        StringBuilder builder = new StringBuilder("execution environment:");
        for (Map.Entry<String,String> e : env.entrySet()) {
            builder.append(separator).append(" - ").append(e.getKey()).append(": ").append(e.getValue());
        }
        return builder.toString();
    }

    private String createBinPath() {
        StringBuilder builder = new StringBuilder(node.getParent());
        for ( String directory : binPath ) {
            builder.append(File.pathSeparator).append(directory);
        }
        return builder.toString();
    }
}
