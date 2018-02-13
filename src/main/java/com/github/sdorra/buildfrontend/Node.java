package com.github.sdorra.buildfrontend;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Node {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);

    private static final String ENV_PATH = "PATH";

    private final File workingDirectory;
    private final File executable;

    Node(File workingDirectory, File executable) {
        this.workingDirectory = workingDirectory;
        this.executable = executable;
    }

    public void execute(String command, String... args) {
        List<String> cmd = new ArrayList<String>();
        cmd.add(executable.getPath());
        cmd.add(command);
        cmd.addAll(Arrays.asList(args));
        executeAndCatch(cmd);
    }

    private void executeAndCatch(List<String> cmds) {
        LOG.info("execute {}", cmds);
        try {
            ProcessResult result = new ProcessExecutor(cmds)
                    .directory(workingDirectory)
                    .environment(createEnvironment(executable))
                    .redirectErrorStream(true)
                    .redirectOutput(System.out)
                    .execute();

            int exitCode = result.getExitValue();
            if (exitCode != 0) {
                throw new IOException("process ends with status code " + exitCode);
            }
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    private Map<String,String> createEnvironment(File node){
        String key = ENV_PATH;

        StringBuilder buffer = new StringBuilder();
        buffer.append(node.getParent());

        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            String k = e.getKey();
            if (ENV_PATH.equalsIgnoreCase(k)) {
                key = k;
                buffer.append(File.pathSeparator).append(Strings.nullToEmpty(e.getValue()));
                break;
            }
        }

        Map<String,String> env = new HashMap<String, String>(System.getenv());
        String p = buffer.toString();
        LOG.debug("set {} environment {} for execution", key, p);
        env.put(key, p);
        return env;
    }
}
