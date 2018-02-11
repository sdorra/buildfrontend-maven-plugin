package com.github.sdorra.buildfrontend;

import java.text.MessageFormat;

public enum PackageManagerType {
    NPM("buildfrontend-npm", "zip", "https://github.com/npm/npm/archive/{0}.zip", "node_modules/npm/cli.js", "bin/npm-cli.js"),
    YARN("buildfrontend-yarn", "tar.gz", "https://github.com/yarnpkg/yarn/releases/download/{0}/yarn-{0}.tar.gz", "bin/yarn.js");

    private final String artifactId;
    private final String packaging;
    private final String urlTemplate;
    private final String[] cliPaths;

    PackageManagerType(String artifactId, String packaging, String urlTemplate, String... cliPaths) {
        this.artifactId = artifactId;
        this.packaging = packaging;
        this.urlTemplate = urlTemplate;
        this.cliPaths = cliPaths;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getPackaging() {
        return packaging;
    }

    public String[] getCliPaths() {
        return cliPaths;
    }

    public String createUrl(String version) {
        return MessageFormat.format(urlTemplate, version);
    }
}
