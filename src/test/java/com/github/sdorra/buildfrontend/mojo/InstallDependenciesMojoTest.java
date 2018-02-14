package com.github.sdorra.buildfrontend.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InstallDependenciesMojoTest extends AbstractPackageManagerMojoTestBase {

    private InstallDependenciesMojo mojo;

    @Before
    public void setUp() throws IOException {
        mojo = new InstallDependenciesMojo();
        preparePackaManagerMojo(mojo);
    }

    @Test
    public void testExecute() throws MojoFailureException, MojoExecutionException {
        mojo.execute();

        verify(packageManager).install();
    }
}