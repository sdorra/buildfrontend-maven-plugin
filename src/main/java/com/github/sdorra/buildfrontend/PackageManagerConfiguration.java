package com.github.sdorra.buildfrontend;

import org.apache.maven.plugins.annotations.Parameter;

public class PackageManagerConfiguration {

    @Parameter
    private PackageManagerType type;

    @Parameter
    private String version;

    public PackageManagerType getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

}
