package com.github.sdorra.buildfrontend;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.stubbing.defaultanswers.ReturnsEmptyValues;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeExecutionBuilderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ProcessExecutor executor;

    @Mock
    private ProcessResult processResult;

    @Captor
    private ArgumentCaptor<Map<String, String>> envCaptor;

    private File workDirectory;
    private File executable;
    private Map<String, String> systemEnvironment;

    @Before
    public void setUpMocks() throws IOException, TimeoutException, InterruptedException {
        workDirectory = temporaryFolder.newFolder();
        executable = temporaryFolder.newFile("node");

        systemEnvironment = new HashMap<String, String>();

        executor = mock(ProcessExecutor.class, new AnswerWithSelf(ProcessExecutor.class));
        when(executor.execute()).thenReturn(processResult);
    }

    @Test
    public void testExecute() {
        CapturingNodeExecutionBuilder builder = new CapturingNodeExecutionBuilder();
        builder.execute("a", "b", "c");

        verify(executor).directory(workDirectory);
        verify(executor).redirectErrorStream(true);
        verify(executor).redirectOutput(System.out);

        int i=0;
        assertEquals(executable.getPath(), builder.cmds.get(i++));
        assertEquals("a", builder.cmds.get(i++));
        assertEquals("b", builder.cmds.get(i++));
        assertEquals("c", builder.cmds.get(i++));
    }

    @Test
    public void testBinaryPathCreation() {
        CapturingNodeExecutionBuilder builder = new CapturingNodeExecutionBuilder();
        builder.prependBinaryToPath("a");
        builder.prependBinaryToPath("b");
        builder.appendBinaryToPath("d");
        builder.appendBinaryToPath("e");
        builder.execute("a");

        verify(executor).environment(envCaptor.capture());

        Map<String, String> env = envCaptor.getValue();
        assertEquals(
            MessageFormat.format(executable.getParent() + "{0}b{0}a{0}d{0}e", File.pathSeparator),
            env.get("PATH")
        );
    }

    @Test
    public void testEnvironmentCreation() {
        systemEnvironment.put("b", "c");
        systemEnvironment.put("c", "0");

        CapturingNodeExecutionBuilder builder = new CapturingNodeExecutionBuilder();
        builder.addEnvironmentVariable("a", "b");
        builder.addEnvironmentVariable("c", "d");
        builder.execute("bin");

        verify(executor).environment(envCaptor.capture());

        Map<String, String> env = envCaptor.getValue();

        assertEquals("b", env.get("a"));
        assertEquals("c", env.get("b"));
        assertEquals("d", env.get("c"));
    }

    @Test
    public void testNonZeroStatusCode() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("exit value 42");

        when(processResult.getExitValue()).thenReturn(42);

        CapturingNodeExecutionBuilder builder = new CapturingNodeExecutionBuilder();
        builder.execute("bin");
    }


    public class CapturingNodeExecutionBuilder extends NodeExecutionBuilder {

        private List<String> cmds;

        CapturingNodeExecutionBuilder() {
            super(workDirectory, executable, systemEnvironment);
        }

        @Override
        ProcessExecutor newExecutor(List<String> cmds) {
            this.cmds = cmds;
            return executor;
        }
    }

    public class AnswerWithSelf implements Answer<Object> {
        private final Answer<Object> delegate = new ReturnsEmptyValues();
        private final Class<?> clazz;

        public AnswerWithSelf(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            Class<?> returnType = invocation.getMethod().getReturnType();
            if (returnType == clazz) {
                return invocation.getMock();
            } else {
                return delegate.answer(invocation);
            }
        }
    }


}