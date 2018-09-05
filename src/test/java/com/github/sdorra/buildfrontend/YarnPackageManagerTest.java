package com.github.sdorra.buildfrontend;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class YarnPackageManagerTest {

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
    public void testLink() {
        packageManager.link();
        verifyExecution("link");
    }

    @Test
    public void testLinkWithPackage() {
        packageManager.link("react");
        verifyExecution("link", "react");
    }

    private void verifyExecution(String ...args) {
        verify(builder).execute(executable.getPath(), args);

        // verify path
        verify(builder).prependBinaryToPath(executable.getParent());
    }

}
