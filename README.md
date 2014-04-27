buildfrontend-maven-plugin
==========================

Installs and run [node](http://nodejs.org/), [npm](https://www.npmjs.org), [bower](http://bower.io/), [grunt](http://gruntjs.com/), [gulp](http://gulpjs.com/) and [karma](http://karma-runner.github.io/) as part of your [maven](http://maven.apache.org/) build.


### Usage

```xml
  <build>
    <plugins>
      
      <plugin>
        <groupId>com.github.sdorra</groupId>
        <artifactId>buildfrontend-maven-plugin</artifactId>
        <version>1.0.0-SNAPSHOT</version>
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

  <pluginRepositories>

    <pluginRepository>
      <id>maven.scm-manager.org</id>
      <name>scm-manager release repository</name>
      <url>http://maven.scm-manager.org/nexus/content/groups/public</url>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
      <releases>
        <updatePolicy>never</updatePolicy>
      </releases>
    </pluginRepository>

  </pluginRepositories>
```
