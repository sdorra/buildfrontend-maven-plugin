package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.Node;
import com.github.sdorra.buildfrontend.PackageManager;
import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.DEPLOY)
public class DeployMojo extends AbstractPackageManagerMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @VisibleForTesting
    void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    @Override
    protected void execute(Node node, PackageManager packageManager) {
        String version = mavenProject.getVersion();
        String deployVersion = createDeploymentVersion(version);
        packageManager.publish(deployVersion);
    }

    private String createDeploymentVersion(String version) {
        if (isSnapshotVersion(version)) {
            return createSnapshotVersion(version);
        }
        return version;
    }

    private String createSnapshotVersion(String version) {
        String suffix = createSnapshotVersionSuffix();
        return version.replace("SNAPSHOT", suffix);
    }

    private String createSnapshotVersionSuffix() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(new Date());
    }

    private boolean isSnapshotVersion(String version) {
        return version.endsWith("-SNAPSHOT");
    }
}
