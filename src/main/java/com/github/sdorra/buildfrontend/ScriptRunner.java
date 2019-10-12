package com.github.sdorra.buildfrontend;

/**
 * Runner interface for scripts defined in package.json.
 */
public interface ScriptRunner {

  /**
   * Do not throw an exception, it the process ends with a status
   * code which not zero.
   *
   * @return {@code this}.
   */
  ScriptRunner ignoreFailure();

  /**
   * Executes the defined script.
   */
  void execute();
}
