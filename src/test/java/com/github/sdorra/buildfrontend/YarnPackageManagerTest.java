package com.github.sdorra.buildfrontend;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.json.Json;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class YarnPackageManagerTest {

    private static final String PACKAGE_JSON_001 = "com/github/sdorra/buildfrontend/001-package.json";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private Node node;

    @Mock
    private NodeExecutionBuilder builder;

    private YarnPackageManager packageManager;

    private File executable;

    @Before
    public void before() throws IOException {
        executable = temporaryFolder.newFile("yarn");
        packageManager = new YarnPackageManager(node, executable);

        when(node.builder()).thenReturn(builder);
        when(builder.prependBinaryToPath(anyString())).thenReturn(builder);
    }

    @Test
    public void testInstall() {
        packageManager.install();
        verifyExecution("install");
    }

    @Test
    public void testRun() {
        packageManager.run("hello");
        verifyExecution("run", "hello");
    }

    @Test
    public void testScript() {
        packageManager.script("hello").ignoreFailure().execute();

        verify(builder).ignoreFailure();

        verifyExecution("run", "hello");
    }

    @Test
    public void testScriptWithArgs() {
        packageManager.script("hello").execute("a", "b");
        verifyExecution("run", "hello", "a", "b");
    }

    @Test
    public void testLink() {
        packageManager.link();
        verifyExecution("link");
    }

    @Test
    public void testLinkWithPackage() {
        packageManager.link("react");
        verifyExecution("link", "react");
    }

    @Test
    public void testPublish() throws IOException {
        File workingDirectory = temporaryFolder.newFolder();
        when(builder.getWorkingDirectory()).thenReturn(workingDirectory);

        File packageJson = new File(workingDirectory, "package.json");
        byte[] data = Resources.toByteArray(Resources.getResource(PACKAGE_JSON_001));
        Files.write(data, packageJson);

        doAnswer(invocation -> {
            assertEquals("0.0.2", getVersionFrom(packageJson));
            return null;
        }).when(builder).execute(executable.getPath(), "publish", "--new-version", "0.0.2");

        packageManager.publish("0.0.2");

        assertEquals("old version is not restored", "0.0.1", getVersionFrom(packageJson));
    }

    private String getVersionFrom(File packageJson) throws FileNotFoundException {
        try(JsonReader reader = Json.createReader(new FileReader(packageJson))) {
            return reader.readObject().getString("version");
        }
    }

    private void verifyExecution(String ...args) {
        verify(builder).execute(executable.getPath(), args);

        // verify path
        verify(builder).prependBinaryToPath(executable.getParent());
    }

}
