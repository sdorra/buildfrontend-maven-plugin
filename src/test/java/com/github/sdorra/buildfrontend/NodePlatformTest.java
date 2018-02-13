package com.github.sdorra.buildfrontend;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NodePlatformTest {

    @Test
    public void testDetect() {
        assertEquals(NodePlatform.LINUX_X64, NodePlatform.detect("Linux", "amd64"));
        assertEquals(NodePlatform.LINUX_X86, NodePlatform.detect("Linux", "i686"));
        assertEquals(NodePlatform.MACOS_X64, NodePlatform.detect("Mac OS X", "x86_64"));
        assertEquals(NodePlatform.MACOS_X86, NodePlatform.detect("Mac OS X", "i686"));
        assertEquals(NodePlatform.SUNOS_X64, NodePlatform.detect("SunOS", "x86_64"));
        assertEquals(NodePlatform.SUNOS_X86, NodePlatform.detect("SunOS", "i686"));
        assertEquals(NodePlatform.SUNOS_X64, NodePlatform.detect("Solaris", "x86_64"));
        assertEquals(NodePlatform.SUNOS_X86, NodePlatform.detect("Solaris", "i686"));
        assertEquals(NodePlatform.WINDOWS_X64, NodePlatform.detect("Windows", "amd64"));
        assertEquals(NodePlatform.WINDOWS_X86, NodePlatform.detect("Windows", "i686"));
    }

    @Test(expected = IllegalStateException.class)
    public void testDetectInvalid() {
        NodePlatform.detect("Invalid", "32Bit");
    }

    @Test
    public void testGetClassifier() {
        assertEquals("linux_x64", NodePlatform.LINUX_X64.getClassifier());
    }

    @Test
    public void testGetNodeUrl() {
        assertEquals("https://nodejs.org/dist/v8.9.4/node-v8.9.4-darwin-x64.tar.gz", NodePlatform.MACOS_X64.getNodeUrl("v8.9.4"));
        assertEquals("https://nodejs.org/dist/v8.9.4/win-x64/node.exe", NodePlatform.WINDOWS_X64.getNodeUrl("v8.9.4"));
        assertEquals("https://nodejs.org/dist/v8.9.4/node.exe", NodePlatform.WINDOWS_X86.getNodeUrl("v8.9.4"));
    }

}