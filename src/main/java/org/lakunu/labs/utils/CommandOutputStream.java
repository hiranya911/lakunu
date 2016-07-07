package org.lakunu.labs.utils;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;
import org.lakunu.labs.LabOutputHandler;

import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

final class CommandOutputStream extends TeeOutputStream {

    private static final int LEVEL_STDOUT = 0;
    private static final int LEVEL_STDERR = 1;

    private CommandOutputStream(OutputStream out, OutputStream branch) {
        super(out, branch);
    }

    public String getContent() {
        checkState(branch instanceof StringBufferOutputStream, "Buffering not enabled");
        return ((StringBufferOutputStream) branch).buffer.toString();
    }

    public static CommandOutputStream withoutBuffering(boolean stdout, LabOutputHandler outputHandler) {
        int level = stdout ? LEVEL_STDOUT : LEVEL_STDERR;
        return new CommandOutputStream(new LabOutputHandlerStream(level, outputHandler),
                NullOutputStream.NULL_OUTPUT_STREAM);
    }

    public static CommandOutputStream withBuffering(boolean stdout, LabOutputHandler outputHandler,
                                                    int threshold) {
        int level = stdout ? LEVEL_STDOUT : LEVEL_STDERR;
        return new CommandOutputStream(new LabOutputHandlerStream(level, outputHandler),
                new StringBufferOutputStream(threshold));
    }

    private static final class LabOutputHandlerStream extends LogOutputStream {

        private final LabOutputHandler outputHandler;

        private LabOutputHandlerStream(int level, LabOutputHandler outputHandler) {
            super(level);
            checkNotNull(outputHandler, "Output handler is required");
            this.outputHandler = outputHandler;
        }

        @Override
        protected void processLine(String line, int level) {
            if (level == LEVEL_STDOUT) {
                outputHandler.info(line);
            } else {
                outputHandler.error(line);
            }
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
