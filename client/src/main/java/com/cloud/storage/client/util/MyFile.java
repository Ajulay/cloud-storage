package com.cloud.storage.client.util;

import javafx.beans.property.*;

import java.io.File;
import java.net.URI;

public class MyFile extends File {
    private StringProperty fileName;
    private LongProperty fileSize;

    public MyFile(String pathname) {
       super(pathname);
       fileName = new SimpleStringProperty(pathname.substring(pathname.lastIndexOf("/")+1));
       fileSize = new SimpleLongProperty(this.length());

    }

    public MyFile(String parent, String child) {
        super(parent, child);
    }

    public MyFile(File parent, String child) {
        super(parent, child);
    }

    public MyFile(URI uri) {
        super(uri);
    }

    public String getFileNameProperty() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileNameProperty(String fileNameProperty) {
        this.fileName.set(fileNameProperty);
    }

    public long getFileSizeProperty() {
        return fileSize.get();
    }

    public LongProperty fileSizeProperty() {
        return fileSize;
    }

    public void setFileSizeProperty(long fileSizeProperty) {
        this.fileSize.set(fileSizeProperty);
    }

    public Long fileSize() {
        return fileSize.get();
    }

    @Override
    public String toString() {
        return this.getFileNameProperty().substring(this.getFileNameProperty().lastIndexOf("/")+1);
    }
}
