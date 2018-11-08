package com.github.sdorra.buildfrontend;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class PackageJsonReaderTest {

    private static final String PACKAGE_JSON_001 = "com/github/sdorra/buildfrontend/001-package.json";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testRead() throws IOException {
        PackageJsonReader reader = new PackageJsonReader();
        PackageJson packageJson = reader.read(createPackageJsonFile(PACKAGE_JSON_001));
        assertEquals("0.0.1", packageJson.getVersion());
    }

    private File createPackageJsonFile(String resource) throws IOException {
        File workingDirectory = temporaryFolder.newFolder();
        File packageJson = new File(workingDirectory, "package.json");

        byte[] data = Resources.toByteArray(Resources.getResource(resource));
        Files.write(data, packageJson);

        return packageJson;
    }

}