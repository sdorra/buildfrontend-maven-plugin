package com.github.sdorra.buildfrontend;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

final class ProcessFailure {

  private static final Logger LOG = LoggerFactory.getLogger(ProcessFailure.class);
  private static final String MESSAGE = "command '%s' ends with exit code %d";

  private final List<String> cmds;
  private final int exitValue;

  ProcessFailure(List<String> cmds, int exitValue) {
    this.cmds = cmds;
    this.exitValue = exitValue;
  }

  void log() {
    if (LOG.isWarnEnabled()) {
      LOG.warn(message());
    }
  }

  void raise() {
    throw new ProcessFailedException(message(), exitValue);
  }

  private String message() {
    return String.format(MESSAGE, Joiner.on(" ").join(cmds), exitValue);
  }
}
