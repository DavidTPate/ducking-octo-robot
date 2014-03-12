package com.davidtpate.github.explore;


import com.davidtpate.github.explore.exception.HaltProcessingException;
import com.davidtpate.github.explore.model.Field;
import com.davidtpate.github.explore.model.Message;
import com.davidtpate.github.explore.model.Repository;
import com.davidtpate.github.explore.util.Strings;
import com.davidtpate.github.explore.util.Util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GithubExploreParser {
    /**
     * We want to restrict the messages we look at to only those that are claimed to be from Github.
     */
    private static final String GITHUB_FROM = "GitHub <noreply@github.com>";
    /**
     * We want to restrict processing to emails that have close enough subject, otherwise the processing is pointless.
     */
    private static final String GITHUB_SUBJECT = "GitHub explore";
    /**
     * Beginning part of the Plaintext section for "People you follow"
     */
    private static final String PEOPLE_YOU_FOLLOW_PREFIX = "Stars from people you follow";
    /**
     * Beginning part of the Plaintext section for "Trending"
     */
    private static final String POPULAR_PREFIX = "Trending Repositories";
    /**
     * Beginning part of the Plaintext section for "Stars from GitHub Staff"
     */
    private static final String GITHUB_STAFF_PREFIX = "Stars from GitHub Staff";
    /**
     * MIME Separator for parsing through the email.
     */
    private static final String MIME_PREFIX = "----==_mimepart";
    /**
     * The Plaintext Content Type
     */
    private static final String CONTENT_TYPE_PLAIN = "text/plain;";
    /**
     * The HTML Content Type
     */
    private static final String CONTENT_TYPE_HTML = "text/html;";
    Message.Builder mMessageBuilder = new Message.Builder();
    Repository.Builder mRepositoryBuilder = new Repository.Builder();
    /**
     * Pattern for matching the Plaintext repository list items. Matches strings like "1." and "12.", the period is required.
     */
    private Pattern mPlainTextListItemPattern = Pattern.compile("^[\\d]+\\..*");
    /**
     * Pattern for extracting the details of the first line of the repository. Matches strings like "1. https://github.com/person/example " and "2. https://github.com/person/example2 Java".
     * It separates the string into 3 pieces. The url, the repository name, and the repository type (if available).
     * For example, given the following: "2. https://github.com/person/example2 Java" it would resolve the parts as follows:
     * url: https://github.com/person/example2
     * name: person/example2
     * type: Java
     */
    private Pattern mPlainTextRepositoryItemPattern = Pattern.compile("^[\\d]+\\. (https://github.com/(.*/.*)) (.*)?$");
    /**
     * The header should always be first.
     */
    private ReaderLocation readerLocation = ReaderLocation.HEADER;

    public Message parse(String path) throws FileNotFoundException, IllegalArgumentException {
        // If we don't have a path to anything, no point in continuing.
        if (Strings.isEmpty(path)) {
            throw new IllegalArgumentException("Path is Null or Blank");
        }

        File file = new File(path);
        // If we don't have a file or it doesn't exist no point in continuing.
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("File Null or Not Found");
        }

        return parseMessage(new FileReader(path));
    }

    private Message parseMessage(FileReader fileReader) {
        // This can only be called internally so inputStream should never be null, but just in case.
        if (fileReader == null) {
            return null;
        }

        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        boolean haltProcessing = false;
        try {
            while ((line = reader.readLine()) != null && !haltProcessing) {
                // If we are in the header.
                switch (readerLocation) {
                    case HEADER:
                        // If we are leaving the header and we encounter a new mime part figure out what it is.
                        if (line.startsWith(MIME_PREFIX)) {
                            handleMimePart(reader);
                        } else {
                            try {
                                handleHeaderField(Field.parseField(line));
                            } catch (HaltProcessingException e) {
                                haltProcessing = true;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case PLAINTEXT_BODY:
                        // If we are leaving the header and we encounter a new mime part figure out what it is.
                        if (line.startsWith(MIME_PREFIX)) {
                            handleMimePart(reader);
                        } else {
                            // Find the repositories in the list and add them as "social" or "staff" repositories.
                            if (line.startsWith(PEOPLE_YOU_FOLLOW_PREFIX)) {
                                parseRepositoryList(reader, RepositoryListType.SOCIAL);
                            } else if (line.startsWith(GITHUB_STAFF_PREFIX)) {
                                // If the person has no friends that Star stuff, take a moment of silence and then parse the list.
                                parseRepositoryList(reader, RepositoryListType.STAFF);
                            } else if (line.startsWith(POPULAR_PREFIX)) {
                                parseRepositoryList(reader, RepositoryListType.POPULAR);
                            }
                            System.out.println(line);
                        }
                        break;
                    case HTML_BODY:
                        // If we are leaving the header and we encounter a new mime part figure out what it is.
                        if (line.startsWith(MIME_PREFIX)) {
                            handleMimePart(reader);
                        } else {
                            // For this example we're only processing PlainText, so let's stop here.
                            haltProcessing = true;
                        }
                        break;
                    default:
                        haltProcessing = true;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(reader);
        }

        return mMessageBuilder.build();
    }

    private void parseRepositoryList(BufferedReader reader, RepositoryListType repositoryListType) throws IOException {
        String line;
        boolean stopProcessing = false;
        // Bubble up the exceptions, no need to handle them down here.
        while ((line = reader.readLine()) != null && !stopProcessing) {
            // If we hit the next MIME part, stop processing.
            if (line.startsWith(MIME_PREFIX)) {
                handleMimePart(reader);
                stopProcessing = true;
            }
            else if (line.startsWith(PEOPLE_YOU_FOLLOW_PREFIX)) {
                // If we run into the People You Follow section, head off to process it. This could happen if they are out of order.
                parseRepositoryList(reader, RepositoryListType.SOCIAL);
                stopProcessing = true;
            } else if (line.startsWith(GITHUB_STAFF_PREFIX)) {
                // If we run into the Staff section, head off to process it.
                parseRepositoryList(reader, RepositoryListType.STAFF);
                stopProcessing = true;
            } else if (line.startsWith(POPULAR_PREFIX)) {
                parseRepositoryList(reader, RepositoryListType.POPULAR);
            }
            // If the line begins with a number followed directly by a period assume it is a repository.
            else if (mPlainTextListItemPattern.matcher(line).matches()) {
                switch (repositoryListType) {
                    case SOCIAL:
                        mMessageBuilder.socialRepository(parseRepository(line, reader));
                        break;
                    case STAFF:
                        mMessageBuilder.staffRepository(parseRepository(line, reader));
                        break;
                    case POPULAR:
                        mMessageBuilder.popularRepository(parseRepository(line, reader));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private Repository parseRepository(String lastLine, BufferedReader reader) throws IOException {
        String line = lastLine;
        Matcher matcher = mPlainTextRepositoryItemPattern.matcher(line);
        if (matcher.find()) {
            mRepositoryBuilder.url(matcher.group(1));
            mRepositoryBuilder.name(matcher.group(2));
            String type = matcher.group(3);
            if (Strings.notEmpty(type)) {
                mRepositoryBuilder.type(type);
            }
        }

        // Grab the description which is always on the next line. Also, bubble up the exceptions, no need to handle them this low.
        line = reader.readLine();
        mRepositoryBuilder.description(line);

        return mRepositoryBuilder.build();
    }

    private void handleMimePart(BufferedReader reader) throws IOException {
        // Since we just shifted to a new section we want to read in the headers to determine which section it is.
        String headerLine;
        boolean stopParsingHeaders = false;
        while ((headerLine = reader.readLine()) != null && !stopParsingHeaders) {
            // If we've reached whitespace, then we are done with the headers.
            if (Strings.isEmpty(headerLine)) {
                // If we have reached a new MIME part but have no headers then we are likely at the end of the file.
                stopParsingHeaders = true;
                break;
            }

            Field field = Field.parseField(headerLine);
            if (field == null) {
                break;
            }
            Header header = Header.findHeader(field.getName());
            if (header == null) {
                break;
            }

            // If we have the Content Type header, use it to figure out what's the MIME area contains.
            if (header == Header.CONTENT_TYPE) {
                if (field.getValue().equals(CONTENT_TYPE_PLAIN)) {
                    readerLocation = ReaderLocation.PLAINTEXT_BODY;
                } else if (field.getValue().equals(CONTENT_TYPE_HTML)) {
                    readerLocation = ReaderLocation.HTML_BODY;
                }
            }
        }
    }

    private void handleHeaderField(Field field) throws HaltProcessingException, ParseException {
        // If we don't have a field, no point continuing.
        if (field == null) {
            return;
        }

        //System.out.println("Name: " + field.getName() + " Value: " + field.getValue());
        Header header = Header.findHeader(field.getName());

        // If we didn't resolve the header, then we don't need to pay attention to it.
        if (header == null) {
            return;
        }

        String value = field.getValue();

        switch (header) {
            case TO:
                mMessageBuilder.to(value);
                break;
            case FROM:
                if (!GITHUB_FROM.equalsIgnoreCase(value)) {
                    throw new HaltProcessingException();
                }
                break;
            case SUBJECT:
                if (!value.startsWith(GITHUB_SUBJECT)) {
                    throw new HaltProcessingException();
                }
                mMessageBuilder.subject(value);
                break;
            case DATE:
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                mMessageBuilder.date(simpleDateFormat.parse(value));
        }
    }

    public enum RepositoryListType {
        SOCIAL, POPULAR, STAFF;
    }

    public enum ReaderLocation {
        HEADER, PLAINTEXT_BODY, HTML_BODY;
    }

    public enum Header {
        TO("Delivered-To"), FROM("From"), SUBJECT("Subject"), DATE("Date"), CONTENT_TYPE("Content-Type");

        protected String mHeader;

        Header(String mHeader) {
            this.mHeader = mHeader;
        }

        public static Header findHeader(String value) {
            // The list is short for now, so don't need anything fancy at this point.
            for (Header header : Header.values()) {
                if (header.getHeader().equalsIgnoreCase(value)) {
                    return header;
                }
            }
            return null;
        }

        public String getHeader() {
            return mHeader;
        }
    }


}
