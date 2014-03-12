package com.davidtpate.github.explore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message {
    private String subject;
    private String to;
    private Date date;
    private List<Repository> socialRepositoryList;
    private List<Repository> popularRepositoryList;
    private List<Repository> staffRepositoryList;

    public String getSubject() {
        return subject;
    }

    public List<Repository> getSocialRepositoryList() {
        return socialRepositoryList;
    }

    public List<Repository> getPopularRepositoryList() {
        return popularRepositoryList;
    }

    public List<Repository> getStaffRepositoryList() {
        return staffRepositoryList;
    }

    @Override
    public String toString() {
        return "Message{" +
                "subject='" + subject + '\'' +
                ", to='" + to + '\'' +
                ", date=" + date +
                ", socialRepositoryList=" + socialRepositoryList +
                ", popularRepositoryList=" + popularRepositoryList +
                ", staffRepositoryList=" + staffRepositoryList +
                '}';
    }

    public static class Builder {
        private Message message = new Message();

        public Builder to(String to) {
            message.to = to;
            return this;
        }

        public Builder subject(String subject) {
            message.subject = subject;
            return this;
        }

        public Builder date(Date date) {
            message.date = date;
            return this;
        }

        public Builder socialRepository(Repository respository) {
            // If we didn't get a repository, do nothing.
            if (respository == null) {
                return this;
            }

            if (message.socialRepositoryList == null) {
                message.socialRepositoryList = new ArrayList<Repository>();
            }

            message.socialRepositoryList.add(respository);
            return this;
        }

        public Builder popularRepository(Repository respository) {
            // If we didn't get a repository, do nothing.
            if (respository == null) {
                return this;
            }

            if (message.popularRepositoryList == null) {
                message.popularRepositoryList = new ArrayList<Repository>();
            }

            message.popularRepositoryList.add(respository);
            return this;
        }

        public Builder staffRepository(Repository respository) {
            // If we didn't get a repository, do nothing.
            if (respository == null) {
                return this;
            }

            if (message.staffRepositoryList == null) {
                message.staffRepositoryList = new ArrayList<Repository>();
            }

            message.staffRepositoryList.add(respository);
            return this;
        }

        public Message build() {
            Message builtMessage = message;
            message = new Message();
            return builtMessage;
        }
    }
}
