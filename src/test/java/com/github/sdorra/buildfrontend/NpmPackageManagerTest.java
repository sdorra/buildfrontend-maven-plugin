package com.github.sdorra.buildfrontend;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NpmPackageManagerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private Node node;

    @Mock
    private NodeExecutionBuilder builder;

    @Mock
    private PackageJsonReader packageJsonReader;

    private NpmPackageManager packageManager;

    private File executable;

    @Before
    public void before() throws IOException {
        executable = temporaryFolder.newFile("npm");
        packageManager = new NpmPackageManager(node, executable);

        when(node.builder()).thenReturn(builder);
        when(builder.prependBinaryToPath(anyString())).thenReturn(builder);

        File workingDirectory = temporaryFolder.newFolder();
        when(builder.getWorkingDirectory()).thenReturn(workingDirectory);
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
    public void testPublish() {
        PackageJson packageJson = mock(PackageJson.class);
        when(packageJson.getVersion()).thenReturn("0.0.1");
        when(packageJson.setVersion("0.0.2")).thenReturn(packageJson);
        when(packageJson.setVersion("0.0.1")).thenReturn(packageJson);

        when(packageJsonReader.read(Mockito.any(File.class))).thenReturn( packageJson );
        packageManager.setPackageJsonReader(packageJsonReader);

        packageManager.publish("0.0.2");
        verifyExecution("publish");
    }

    private void verifyExecution(String ...args) {
        List<String> list = Lists.newArrayList("--color=false", "--parseable");
        list.addAll(Arrays.asList(args));
        verify(builder).execute(executable.getPath(), list.toArray(new String[0]));

        // verify path
        verify(builder).prependBinaryToPath(executable.getParent());
    }
}
