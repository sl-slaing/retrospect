package com.example.retrospect.core.repositories;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataStorage {
    InputStream openRead() throws IOException;

    OutputStream openWrite() throws IOException;

    boolean canRead();
}
