# buildfrontend-maven-plugin

Installs and run [node](http://nodejs.org/), [npm](https://www.npmjs.org) or [yarn](https://yarnpkg.com) as part of your 
[maven](http://maven.apache.org/) build. The buildfrontend-maven-plugin will do the following steps for you:

* download the specified tools
* installs them to your local maven repository
* extract the archives from the local maven repository
* invoke the tools with the configured parameters

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
          <version>v8.9.4</version>
        </node>
        <pkgManager>
          <type>YARN</type>
          <version>v1.3.2</version>
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
