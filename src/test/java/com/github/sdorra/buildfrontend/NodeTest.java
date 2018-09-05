package com.github.sdorra.buildfrontend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NodeTest {

    private static final String EXECUTABLE = String.format("parent%snode", File.separator);

    @Mock
    private NodeExecutionBuilder builder;

    @Test
    public void testExecute() {
        NodeForTesting node = new NodeForTesting("work", EXECUTABLE);
        node.execute("b", "c", "d");

        verify(builder).execute("b", "c", "d");
    }

    public class NodeForTesting extends Node {

        NodeForTesting(String workingDirectory, String executable) {
            super(new File(workingDirectory), new File(executable));
        }

        @Override
        public NodeExecutionBuilder builder() {
            return builder;
        }
    }


}