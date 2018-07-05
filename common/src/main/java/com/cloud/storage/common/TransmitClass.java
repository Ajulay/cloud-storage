package com.cloud.storage.common;
import java.io.Serializable;

public class TransmitClass implements Serializable {
    String fileName;
    char[] buffer;
    int part;

    public TransmitClass(String fileName, char[] buffer, int part) {
        this.fileName = fileName;
        this.buffer = buffer;
        this.part = part;
    }

}
