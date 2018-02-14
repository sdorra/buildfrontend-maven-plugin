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
public class ScriptMojoTest extends AbstractPackageManagerMojoTestBase {

    private ScriptMojo mojo;

    @Before
    public void setUp() throws IOException {
        mojo = new ScriptMojo();
        mojo.setScript("awesome");
        preparePackaManagerMojo(mojo);
    }

    @Test
    public void testExecute() throws MojoFailureException, MojoExecutionException {
        mojo.execute();

        verify(packageManager).run("awesome");
    }
}