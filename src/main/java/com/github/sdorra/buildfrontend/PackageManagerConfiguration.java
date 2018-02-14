package com.github.sdorra.buildfrontend;

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugins.annotations.Parameter;

public class PackageManagerConfiguration {

    @Parameter
    private PackageManagerType type;

    @Parameter
    private String version;

    public PackageManagerType getType() {
        return type;
    }

    @VisibleForTesting
    void setType(PackageManagerType type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    @VisibleForTesting
    void setVersion(String version) {
        this.version = version;
    }
}
