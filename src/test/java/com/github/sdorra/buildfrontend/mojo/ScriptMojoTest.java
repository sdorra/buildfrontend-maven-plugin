package com.github.sdorra.buildfrontend.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScriptMojoTest extends AbstractPackageManagerMojoTestBase {

    private ScriptMojo mojo;

    @Before
    public void setUp() throws IOException {
        mojo = new ScriptMojo();
        mojo.setScript("awesome");
        preparePackageManagerMojo(mojo);
    }

    @Test
    public void testExecute() throws MojoFailureException, MojoExecutionException {
        mojo.execute();

        verify(packageManager).run("awesome");
    }

    @Test
    public void testSkip() throws IOException {
        mojo.setSkip(true);

        verify(nodeFactory, never()).create(nodeConfiguration);
    }

    @Test
    public void testExecuteInBackground() throws MojoFailureException, MojoExecutionException, InterruptedException {
        final ThreadNameCapturingAnswer answer = new ThreadNameCapturingAnswer();
        doAnswer(answer).when(packageManager).run("awesome");

        mojo.setBackground(true);
        mojo.execute();

        synchronized (answer) {
            answer.wait(500L);
        }

        assertEquals(ScriptMojo.THREAD_NAME, answer.threadName);
        verify(packageManager).run("awesome");
    }

    private static class ThreadNameCapturingAnswer implements Answer<Void> {

        private String threadName;

        @Override
        public Void answer(InvocationOnMock invocation) {
            synchronized (this) {
                threadName = Thread.currentThread().getName();
                this.notifyAll();
            }
            return null;
        }
    }
}
