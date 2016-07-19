package org.lakunu.labs.ant;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;

import java.io.IOException;
import java.io.OutputStream;

public final class TaskOutputRecorder implements BuildListener {

    private final StringBufferOutputStream output = new StringBufferOutputStream(64 * 1024);
    private final StringBufferOutputStream error = new StringBufferOutputStream(64 * 1024);

    public String getOutput() {
        return output.buffer.toString();
    }

    public String getError() {
        return error.buffer.toString();
    }

    @Override
    public void buildStarted(BuildEvent buildEvent) {
    }

    @Override
    public void buildFinished(BuildEvent buildEvent) {
    }

    @Override
    public void targetStarted(BuildEvent buildEvent) {
    }

    @Override
    public void targetFinished(BuildEvent buildEvent) {
    }

    @Override
    public void taskStarted(BuildEvent buildEvent) {
    }

    @Override
    public void taskFinished(BuildEvent buildEvent) {
    }

    @Override
    public void messageLogged(BuildEvent buildEvent) {
        OutputStream out = null;
        switch (buildEvent.getPriority()) {
            case Project.MSG_INFO:
                out = output;
                break;
            case Project.MSG_ERR:
                out = error;
                break;
        }
        try {
            if (out != null) {
                IOUtils.write(buildEvent.getMessage() + "\n", out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class StringBufferOutputStream extends ThresholdingOutputStream {

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(256);
        private OutputStream current;

        private StringBufferOutputStream(int threshold) {
            super(threshold);
            this.current = this.buffer;
        }

        @Override
        protected OutputStream getStream() throws IOException {
            return current;
        }

        @Override
        protected void thresholdReached() throws IOException {
            this.current = NullOutputStream.NULL_OUTPUT_STREAM;
        }

    }
}
