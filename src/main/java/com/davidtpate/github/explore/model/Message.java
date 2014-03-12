package com.davidtpate.github.explore.model;

import java.util.Date;

public abstract class Message {
    protected String subject;
    protected String to;
    protected Date date;

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }

    public Date getDate() {
        return date;
    }
}
