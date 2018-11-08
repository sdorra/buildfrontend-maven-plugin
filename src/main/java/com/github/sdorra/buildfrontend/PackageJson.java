package com.github.sdorra.buildfrontend;

import com.google.common.base.Throwables;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PackageJson {

    private final JsonObject jsonObject;

    PackageJson(JsonObject packageJson) {
        this.jsonObject = packageJson;
    }

    public String getVersion() {
        return jsonObject.getString("version");
    }

    public PackageJson setVersion(String newVersion) {
        JsonObject packageJsonWithNewVersion = Json.createObjectBuilder(jsonObject)
                .add("version", newVersion)
                .build();
        return new PackageJson(packageJsonWithNewVersion);
    }

    public void write(File file) {
        try (JsonWriter writer = Json.createWriter(new FileWriter(file, false))) {
            writer.writeObject(jsonObject);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
