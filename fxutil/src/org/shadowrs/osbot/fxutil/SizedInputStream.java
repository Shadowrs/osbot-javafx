package org.shadowrs.osbot.fxutil;

import java.io.IOException;
import java.io.InputStream;

public class SizedInputStream extends InputStream {
    public  int              bytesRead;
    private ProgressListener l;
    private InputStream      in;
    private long             startTime;
    private double           size;

    public SizedInputStream(InputStream in, int size, ProgressListener l) {
        this.in = in;
        this.size = (double)size;
        this.l = l;
        this.startTime = System.currentTimeMillis();
    }

    public int available() {
        return (int)this.size - this.bytesRead;
    }

    public int read() throws IOException {
        int b = this.in.read();
        if (b != -1) {
            ++this.bytesRead;
        }

        this.updateListener();
        return b;
    }

    public int read(byte[] b) throws IOException {
        int read = this.in.read(b);
        this.bytesRead += read;
        this.updateListener();
        return read;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int read = this.in.read(b, off, len);
        this.bytesRead += read;
        this.updateListener();
        return read;
    }

    private void updateListener() {
        if (this.l != null) {
            double percent = (double)this.bytesRead / this.size * 100.0D;
            this.l.onProgressUpdate(percent);
            long curTime = System.currentTimeMillis();
            double timeSeconds = (double)(curTime - this.startTime) / 1000.0D;
            double speed = (double)this.bytesRead / 1048576.0D / timeSeconds;
            this.l.updateDownloadSpeed(speed);
        }

    }
}
