package com.cloud.storage.common;
import java.io.Serializable;

public class TransmitClass implements Serializable {
    String fileName;
    byte[] buffer;
    int part;

    public TransmitClass(String fileName, byte[] buffer, int part) {
        this.fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        this.buffer = buffer;
        this.part = part;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }
}
