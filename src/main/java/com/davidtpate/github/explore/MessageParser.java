package com.davidtpate.github.explore;

import com.davidtpate.github.explore.model.Message;

import java.io.FileNotFoundException;

public abstract class MessageParser<T extends Message> {
    /**
     * MIME Separator for parsing through the email.
     */
    protected static final String MIME_PREFIX = "----==_mimepart";
    /**
     * The Plaintext Content Type
     */
    protected static final String CONTENT_TYPE_PLAIN = "text/plain;";
    /**
     * The HTML Content Type
     */
    protected static final String CONTENT_TYPE_HTML = "text/html;";

    public abstract T parse(String path) throws FileNotFoundException, IllegalArgumentException;

    public enum ReaderLocation {
        HEADER, PLAINTEXT_BODY, HTML_BODY;
    }

    /**
     * State variable of the current parsing location, the header should always be first.
     */
    protected ReaderLocation readerLocation = ReaderLocation.HEADER;

}
