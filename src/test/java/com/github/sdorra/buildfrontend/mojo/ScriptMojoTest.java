package com.github.sdorra.buildfrontend.mojo;

import com.github.sdorra.buildfrontend.ScriptRunner;
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
        ScriptRunner runner = mock(ScriptRunner.class);
        when(packageManager.script("awesome")).thenReturn(runner);

        mojo.execute();

        verify(runner).execute();
    }

    @Test
    public void testExecuteAndIgnoreFailure() throws MojoFailureException, MojoExecutionException {
        ScriptRunner runner = mock(ScriptRunner.class);
        when(packageManager.script("awesome")).thenReturn(runner);

        mojo.setIgnoreFailure(true);
        mojo.execute();

        verify(runner).ignoreFailure();
        verify(runner).execute();
    }

    @Test
    public void testSkip() throws IOException {
        mojo.setSkip(true);

        verify(nodeFactory, never()).create(nodeConfiguration);
    }

    @Test
    public void testIgnoreFailure() {
        mojo.setIgnoreFailure(true);
    }

    @Test
    public void testExecuteInBackground() throws MojoFailureException, MojoExecutionException, InterruptedException {
        final ThreadNameCapturingAnswer answer = new ThreadNameCapturingAnswer();

        ScriptRunner runner = mock(ScriptRunner.class);
        when(packageManager.script("awesome")).thenReturn(runner);

        doAnswer(answer).when(runner).execute();

        mojo.setBackground(true);
        mojo.execute();

        synchronized (answer) {
            answer.wait(500L);
        }

        assertEquals(ScriptMojo.THREAD_NAME, answer.threadName);
        verify(runner).execute();
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
