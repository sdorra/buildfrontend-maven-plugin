package com.github.sdorra.buildfrontend;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeTest {

    private static final String EXECUTABLE = String.format("parent%snode", File.separator);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ProcessResult result;

    @Test
    public void testExecute() {
        CapturingNode node = new CapturingNode("work", EXECUTABLE);
        node.execute("b", "c");

        int i = 0;
        assertEquals("parent/node", node.cmds.get(i++));
        assertEquals("b", node.cmds.get(i++));
        assertEquals("c", node.cmds.get(i++));

        assertTrue(node.env.get("PATH").startsWith("parent"));
    }

    @Test
    public void testWithNonZeroExitValue() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("exit value 2");

        when(result.getExitValue()).thenReturn(2);

        CapturingNode node = new CapturingNode("work", EXECUTABLE);
        node.execute("b", "c");
    }

    public class CapturingNode extends Node {

        private Map<String,String> env;
        private List<String> cmds;

        CapturingNode(String workingDirectory, String executable) {
            super(new File(workingDirectory), new File(executable));
        }

        @Override
        protected ProcessResult execute(Map<String,String> env, List<String> cmds) {
            this.env = env;
            this.cmds = cmds;
            return result;
        }
    }
}