# buildfrontend-maven-plugin

[![Quality Gates](https://sonarcloud.io/api/badges/gate?key=com.github.sdorra%3Abuildfrontend-maven-plugin)](https://sonarcloud.io/dashboard?id=com.github.sdorra%3Abuildfrontend-maven-plugin)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.sdorra%3Abuildfrontend-maven-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=com.github.sdorra%3Abuildfrontend-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.sdorra/buildfrontend-maven-plugin.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22buildfrontend-maven-plugin%22)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.md)

Installs and run [node](http://nodejs.org/), [npm](https://www.npmjs.org) or [yarn](https://yarnpkg.com) as part of your 
[maven](http://maven.apache.org/) build. The buildfrontend-maven-plugin will do the following steps for you:

* download the specified tools
* installs them to your local maven repository
* extract the archives from the local maven repository
* invoke the tools with the configured parameters

## Continuous integration

Travis (Linux & Mac OS X): [![Build Status](https://travis-ci.org/sdorra/buildfrontend-maven-plugin.svg?branch=master)](https://travis-ci.org/sdorra/buildfrontend-maven-plugin)

Appveyor (Windows): [![Build status](https://ci.appveyor.com/api/projects/status/lqpabvst2vwybtg8?svg=true)](https://ci.appveyor.com/project/sdorra/buildfrontend-maven-plugin)

## Demo

A demo project can be found [here](https://github.com/sdorra/buildfrontend-maven-plugin/tree/master/src/it).

## Usage

```xml
<build>
  <plugins>
    
    <plugin>
      <groupId>com.github.sdorra</groupId>
      <artifactId>buildfrontend-maven-plugin</artifactId>
      <version>2.0.0-SNAPSHOT</version>
      <configuration>
        <node>
          <version>8.9.4</version>
        </node>
        <pkgManager>
          <type>YARN</type>
          <version>1.3.2</version>
        </pkgManager>
        <script>run</script>
      </configuration>
      <executions>
        <execution>
          <id>install</id>
          <phase>process-resources</phase>
          <goals>
            <goal>install</goal>
          </goals>
        </execution>
        <execution>
          <id>run</id>
          <phase>process-resources</phase>
          <goals>
            <goal>run</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
    
  </plugins>
</build>
```

## Differences to version 1.x

The project does not longer support steps for each tool (e.g.: bower, npm, karma, ...), version 2.x focuses on the usage
of scripts inside the package.json. If you want to run bower or karma just add a script to your package.json and use
the run goal.

Version 2.x uses an abstraction for node package manager, so you can decide if you want to use npm or yarn.

## Why not [eirslett/frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin)?

The buildfrontend-maven-plugin is heavily inspired from the great work of [eirslett](https://github.com/eirslett). The
main differences between both projects is that buildfrontend-maven-plugin installs the downloaded components into the
local maven repository before extraction. This gives a better performance during a clean build, because we don't have to
download the artifacts again. 

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details.