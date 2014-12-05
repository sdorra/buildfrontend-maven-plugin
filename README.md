buildfrontend-maven-plugin
==========================

Installs and run [node](http://nodejs.org/), [npm](https://www.npmjs.org), [bower](http://bower.io/), [grunt](http://gruntjs.com/), [gulp](http://gulpjs.com/) and [karma](http://karma-runner.github.io/) as part of your [maven](http://maven.apache.org/) build. The buildfrontend-maven-plugin will do the following steps for you:

* download the specified tools
* installs them to your local maven repository
* extract the archives from the local maven repository
* invoke the tools with the configured parameters

### Demo

A demo project can be found [here](https://github.com/sdorra/buildfrontend-maven-plugin-demo).

### Usage

```xml
<build>
  <plugins>
    
    <plugin>
      <groupId>com.github.sdorra</groupId>
      <artifactId>buildfrontend-maven-plugin</artifactId>
      <version>1.0.5</version>
      <configuration>
        <karmaConfig>test/conf/karma.conf</karmaConfig>
      </configuration>
      <executions>
        <execution>
          <id>gulp</id>
          <phase>process-resources</phase>
          <goals>
            <goal>npm-install</goal>
            <goal>bower-install</goal>
            <goal>gulp</goal>
          </goals>
        </execution>
        <execution>
          <id>karma</id>
          <phase>test</phase>
          <goals>
            <goal>karma</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
    
  </plugins>
</build>
```
