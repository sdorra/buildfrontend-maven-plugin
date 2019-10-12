package com.github.sdorra.buildfrontend;

/**
 * Interface for a node package manager (yarn or npm).
 */
public interface PackageManager {

    /**
     * Install everything which is defined in the package.json file of the package manager.
     */
    void install();

    /**
     * Runs a script which is defined in the package.json.
     *
     * @param script name of the script
     *
     * @deprecated use {@link #script(String)} instead
     */
    @Deprecated
    void run(String script);

    /**
     * Returns a {@link ScriptRunner} which is able to run a script,
     * which is defined in the package.json.
     *
     * @param script name of the script
     *
     * @return {@link ScriptRunner} for given script
     */
    ScriptRunner script(String script);

    /**
     * Symlink a package folder during development.
     */
    void link();

    /**
     * Link another package to the current package.
     *
     * @param pkg of package to link
     */
    void link(String pkg);

    /**
     * Publishes the package to an npm registry.
     *
     * @param version version of the package
     */
    void publish(String version);

    /**
     * Runner interface for scripts defined in package.json.
     */
    interface ScriptRunner {

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
}
