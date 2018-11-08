package com.github.sdorra.buildfrontend;

import com.google.common.base.Throwables;

import javax.json.Json;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class PackageJsonReader {

    public PackageJson read(File packageJsonFile) {
        try (JsonReader reader = Json.createReader(new FileReader(packageJsonFile))) {
            return new PackageJson(reader.readObject());
        } catch (FileNotFoundException e) {
            throw Throwables.propagate(e);
        }
    }

}
