/**
 * The MIT License
 * <p>
 * Copyright (c) 2014, Sebastian Sdorra
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.github.sdorra.buildfrontend;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * NodePlatform encapsulates the differences between the runtime environment, including operating system and
 * architecture.
 *
 * @author Sebastian Sdorra
 */
public enum NodePlatform {

    /** linux 32 bit */
    LINUX_X86("node-{0}-linux-x86.tar.gz", "tar.gz", false, "node"),
    /** linux 64 bit */
    LINUX_X64("node-{0}-linux-x64.tar.gz", "tar.gz", false, "node"),
    /** macos 32 bit */
    MACOS_X86("node-{0}-darwin-x86.tar.gz", "tar.gz", false, "node"),
    /** macos 64 bit */
    MACOS_X64("node-{0}-darwin-x64.tar.gz", "tar.gz", false, "node"),
    /** sunos/solaris 32 bit */
    SUNOS_X86("node-{0}-sunos-x86.tar.gz", "tar.gz", false, "node"),
    /** sunos/solaris 64 bit */
    SUNOS_X64("node-{0}-sunos-x64.tar.gz", "tar.gz", false, "node"),
    /** windows 32 bit */
    WINDOWS_X86("node.exe", "exe", true, "node.exe"),
    /** windows 64 bit */
    WINDOWS_X64("win-x64/node.exe", "exe", true, "node.exe");

    private static final String URL_TEMPLATE = "https://nodejs.org/dist/{0}/";

    private final String nodeFilePattern;
    private final String nodePackageType;
    private final boolean nodeUnpacked;
    private final String executableName;

    NodePlatform(String nodeFilePattern, String nodePackageType, boolean nodeUnpacked, String executableName) {
        this.nodeFilePattern = nodeFilePattern;
        this.nodePackageType = nodePackageType;
        this.nodeUnpacked = nodeUnpacked;
        this.executableName = executableName;
    }

    public static NodePlatform current() {
        return detect(System.getProperty("os.name"), System.getProperty("os.arch"));
    }

    static NodePlatform detect(String osName, String osArch) {
        String os = osName.toLowerCase(Locale.ENGLISH);
        String arch = osArch.toLowerCase(Locale.ENGLISH);

        boolean x64 = false;

        // TOOD: not the best way, find a better one
        if (arch.endsWith("64")) {
            x64 = true;
        }

        NodePlatform nodePlatform;

        if (os.startsWith("windows")) {
            nodePlatform = x64
                    ? NodePlatform.WINDOWS_X64
                    : NodePlatform.WINDOWS_X86;
        } else if (os.startsWith("linux")) {
            nodePlatform = x64
                    ? NodePlatform.LINUX_X64
                    : NodePlatform.LINUX_X86;
        } else if (os.startsWith("mac")) {
            nodePlatform = x64
                    ? NodePlatform.MACOS_X64
                    : NodePlatform.MACOS_X86;
        } else if (os.startsWith("sunos") || os.startsWith("solaris")) {
            nodePlatform = x64
                    ? NodePlatform.SUNOS_X64
                    : NodePlatform.SUNOS_X86;
        } else {
            throw new IllegalStateException("unknown os ".concat(os));
        }

        return nodePlatform;
    }

    public String getClassifier() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public String getExecutableName() {
        return executableName;
    }

    public String getNodePackageType() {
        return nodePackageType;
    }

    public String getNodeUrl(String version) {
        return MessageFormat.format(URL_TEMPLATE.concat(nodeFilePattern), version);
    }

    public boolean isNodeUnpacked() {
        return nodeUnpacked;
    }

}
