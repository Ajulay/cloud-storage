package com.cloud.storage.common;
import java.io.Serializable;


import static com.cloud.storage.common.UtilConstants.SERVICE_FILE;
import static com.cloud.storage.common.UtilConstants.STRING;

public class TransmitClass implements Serializable {
    String fileName;
    byte[] buffer;
    int part;
    boolean lastPart;
    int mark;
    public TransmitClass(String fileName, byte[] buffer, int part, int mark) {
        this(fileName, buffer, part, false, mark);


    }
    public TransmitClass(String msg) {
        this(msg, STRING);

    }

    public TransmitClass(String fileName, byte[] buffer, int part, boolean lastPart, int mark) {
        this.fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        this.buffer = buffer;
        this.part = part;
        this.mark = mark;
        this.lastPart = lastPart;
    }

    public TransmitClass(String pass, int mark) {
        this(SERVICE_FILE, pass.getBytes(), 0, mark);
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

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public boolean isLastPart() {
        return lastPart;
    }
    public void setLastPart(boolean lastPart) {
        this.lastPart = lastPart;
    }


}
