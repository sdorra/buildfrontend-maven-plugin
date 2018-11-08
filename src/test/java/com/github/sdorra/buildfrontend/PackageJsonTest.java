package com.github.sdorra.buildfrontend;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PackageJsonTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGetVersion() throws IOException {
        PackageJson json = createPackageJson("0.0.1");

        assertEquals("0.0.1", json.getVersion());
    }

    @Test
    public void testSetVersion() throws IOException {
        PackageJson json = createPackageJson("0.0.1");
        json = json.setVersion("0.0.2");

        assertEquals("0.0.2", json.getVersion());
    }

    @Test
    public void testWrite() throws IOException {
        File file = temporaryFolder.newFile();
        createPackageJson("0.0.2").write(file);

        try (JsonReader reader = Json.createReader(new FileReader(file))) {
            String version = reader.readObject().getString("version");
            assertEquals("0.0.2", version);
        }
    }

    private PackageJson createPackageJson(String version) throws IOException {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("version", version)
                .build();
        return new PackageJson(jsonObject);
    }


}