/**
 * The MIT License
 *
 * Copyright (c) 2014, Sebastian Sdorra
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */



package com.github.sdorra.buildfrontend;

import java.io.IOException;
import java.text.MessageFormat;

import java.util.Locale;

/**
 *
 * @author Sebastian Sdorra
 */
public enum Platform
{

  //J-
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
  //J+

  /** Field description */
  private static final String URL_TEMPLATE = "https://nodejs.org/dist/{0}/";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param nodeFilePattern
   * @param nodePackageType
   * @param nodeUnpacked
   * @param executableName
   */
  Platform(String nodeFilePattern, String nodePackageType,
    boolean nodeUnpacked, String executableName)
  {
    this.nodeFilePattern = nodeFilePattern;
    this.nodePackageType = nodePackageType;
    this.nodeUnpacked = nodeUnpacked;
    this.executableName = executableName;
  }

  //~--- methods --------------------------------------------------------------

  public static Platform current() throws IOException {
    Platform platform;
    String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

    // TOOD: not the best way, find a better oen
    String arch = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
    boolean x64 = false;

    if (arch.endsWith("64"))
    {
      x64 = true;
    }

    if (os.startsWith("windows"))
    {
      platform = x64
        ? Platform.WINDOWS_X64
        : Platform.WINDOWS_X86;
    }
    else if (os.startsWith("linux"))
    {
      platform = x64
        ? Platform.LINUX_X64
        : Platform.LINUX_X86;
    }
    else if (os.startsWith("mac"))
    {
      platform = x64
        ? Platform.MACOS_X64
        : Platform.MACOS_X86;
    }
    else if (os.startsWith("sunos") || os.startsWith("solaris"))
    {
      platform = x64
        ? Platform.SUNOS_X64
        : Platform.SUNOS_X86;
    }
    else
    {
      throw new IOException("Unsupported os.name: " + os);
    }

    return platform;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String getClassifier()
  {
    return name().toLowerCase(Locale.ENGLISH);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getExecutableName()
  {
    return executableName;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getNodePackageType()
  {
    return nodePackageType;
  }

  /**
   * Method description
   *
   *
   * @param version
   *
   * @return
   */
  public String getNodeUrl(String version)
  {
    return MessageFormat.format(URL_TEMPLATE.concat(nodeFilePattern), version);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isNodeUnpacked()
  {
    return nodeUnpacked;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final String executableName;

  /** Field description */
  private final String nodeFilePattern;

  /** Field description */
  private final String nodePackageType;

  /** Field description */
  private final boolean nodeUnpacked;
}
