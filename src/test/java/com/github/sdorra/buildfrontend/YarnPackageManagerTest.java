package com.github.sdorra.buildfrontend;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class YarnPackageManagerTest {

    @Mock
    private Node node;

    private YarnPackageManager packageManager;

    @Before
    public void before() {
        packageManager = new YarnPackageManager(node, "yarn");
    }

    @Test
    public void testInstall() {
        packageManager.install();
        verify(node).execute("yarn", "install");
    }

    @Test
    public void testRun() {
        packageManager.run("hello");
        verify(node).execute("yarn", "run", "hello");
    }

    @Test
    public void testLink() {
        packageManager.link();
        verify(node).execute("yarn", "link");
    }

    @Test
    public void testLinkWithPackage() {
        packageManager.link("react");
        verify(node).execute("yarn", "link", "react");
    }


}
