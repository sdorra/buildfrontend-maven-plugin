package com.github.sdorra.buildfrontend;

import com.google.common.annotations.VisibleForTesting;

import java.io.File;
import java.util.function.Consumer;

abstract class AbstractPackageManager {

    private PackageJsonReader packageJsonReader = new PackageJsonReader();

    @VisibleForTesting
    void setPackageJsonReader(PackageJsonReader packageJsonReader) {
        this.packageJsonReader = packageJsonReader;
    }

    private File getPackageJsonFile(Node node) {
        return new File(node.builder().getWorkingDirectory(), "package.json");
    }

    private PackageJson readPackageJson(File file) {
        return packageJsonReader.read(file);
    }

    void publish(Node node, String version, Consumer<String> consumer) {
        File file = getPackageJsonFile(node);
        PackageJson packageJson = readPackageJson(file);

        String oldVersion = packageJson.getVersion();

        packageJson.setVersion(version).write(file);
        consumer.accept(oldVersion);
        packageJson.setVersion(oldVersion).write(file);
    }
}
