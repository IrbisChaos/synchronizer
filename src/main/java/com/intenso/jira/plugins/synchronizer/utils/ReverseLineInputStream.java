// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.InputStream;

public class ReverseLineInputStream extends InputStream
{
    RandomAccessFile in;
    long currentLineStart;
    long currentLineEnd;
    long currentPos;
    long lastPosInFile;
    
    public ReverseLineInputStream(final File file) throws FileNotFoundException {
        this.currentLineStart = -1L;
        this.currentLineEnd = -1L;
        this.currentPos = -1L;
        this.lastPosInFile = -1L;
        this.in = new RandomAccessFile(file, "r");
        this.currentLineStart = file.length();
        this.currentLineEnd = file.length();
        this.lastPosInFile = file.length() - 1L;
        this.currentPos = this.currentLineEnd;
    }
    
    public void findPrevLine() throws IOException {
        this.currentLineEnd = this.currentLineStart;
        if (this.currentLineEnd == 0L) {
            this.currentLineEnd = -1L;
            this.currentLineStart = -1L;
            this.currentPos = -1L;
            return;
        }
        long filePointer = this.currentLineStart - 1L;
        int readByte;
        do {
            --filePointer;
            if (filePointer < 0L) {
                break;
            }
            this.in.seek(filePointer);
            readByte = this.in.readByte();
        } while (readByte != 10 || filePointer == this.lastPosInFile);
        this.currentLineStart = filePointer + 1L;
        this.currentPos = this.currentLineStart;
    }
    
    @Override
    public int read() throws IOException {
        if (this.currentPos < this.currentLineEnd) {
            this.in.seek(this.currentPos++);
            final int readByte = this.in.readByte();
            return readByte;
        }
        if (this.currentPos < 0L) {
            return -1;
        }
        this.findPrevLine();
        return this.read();
    }
}
