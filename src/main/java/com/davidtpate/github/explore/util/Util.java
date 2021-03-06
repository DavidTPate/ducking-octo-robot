package com.davidtpate.github.explore.util;

import java.io.Closeable;
import java.io.IOException;

public class Util {
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}
