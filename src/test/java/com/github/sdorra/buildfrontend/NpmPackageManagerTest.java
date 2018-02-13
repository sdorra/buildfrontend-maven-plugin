package com.github.sdorra.buildfrontend;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NpmPackageManagerTest {

    @Mock
    private Node node;

    private NpmPackageManager packageManager;

    @Before
    public void before() {
        packageManager = new NpmPackageManager(node, "npm");
    }

    @Test
    public void testInstall() {
        packageManager.install();
        verify(node).execute("npm", "--color=false", "--parseable", "install");
    }

    @Test
    public void testRun() {
        packageManager.run("hello");
        verify(node).execute("npm", "--color=false", "--parseable", "run", "hello");
    }

}