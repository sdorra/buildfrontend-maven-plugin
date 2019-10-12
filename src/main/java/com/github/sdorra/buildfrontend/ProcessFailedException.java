package com.github.sdorra.buildfrontend;

public final class ProcessFailedException extends RuntimeException {

  private final int exitValue;

  ProcessFailedException(String message, int exitValue) {
    super(message);
    this.exitValue = exitValue;
  }

  public int getExitValue() {
    return exitValue;
  }

}
