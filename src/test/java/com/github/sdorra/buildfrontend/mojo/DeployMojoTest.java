package com.github.sdorra.buildfrontend.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeployMojoTest extends AbstractPackageManagerMojoTestBase {

    @Mock
    private MavenProject project;

    @Captor
    private ArgumentCaptor<String> versionCaptor;

    private DeployMojo mojo;

    @Before
    public void setUp() throws IOException {
        mojo = new DeployMojo();
        mojo.setMavenProject(project);
        preparePackageManagerMojo(mojo);
    }


    @Test
    public void testDeployReleaseVersion() throws MojoFailureException, MojoExecutionException {
        when(project.getVersion()).thenReturn("0.0.1");

        mojo.execute();

        verify(packageManager).publish("0.0.1");
    }

    @Test
    public void testDeploySnapshotVersion() throws MojoFailureException, MojoExecutionException {
        when(project.getVersion()).thenReturn("1.0.0-SNAPSHOT");

        mojo.execute();

        verify(packageManager).publish(versionCaptor.capture());

        String version = versionCaptor.getValue();
        assertTrue("version is not a valid snapshot version", version.matches("1\\.0\\.0-[0-9]+\\.[0-9]+"));
    }

}