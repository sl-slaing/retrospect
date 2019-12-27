package com.example.retrospect.core.repositories;

import java.io.*;
import java.nio.file.Path;

public class FileStorage implements DataStorage {
    private final File file;

    public FileStorage(String filename) {
        var dataStoragePath = System.getProperty("DataStoragePath");
        file = Path.of(dataStoragePath).resolve(filename).toFile();
    }

    @Override
    public InputStream openRead() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public OutputStream openWrite() throws IOException {
        return new FileOutputStream(file);
    }

    @Override
    public boolean canRead() {
        return file.exists();
    }
}
