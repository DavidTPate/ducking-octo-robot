package com.davidtpate.github.explore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GithubExploreMessage extends Message {
    private List<Repository> socialRepositoryList;
    private List<Repository> popularRepositoryList;
    private List<Repository> staffRepositoryList;

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
        return "GithubExploreMessage{" +
                "subject='" + subject + '\'' +
                ", to='" + to + '\'' +
                ", date=" + date +
                ", socialRepositoryList=" + socialRepositoryList +
                ", popularRepositoryList=" + popularRepositoryList +
                ", staffRepositoryList=" + staffRepositoryList +
                '}';
    }

    public static class Builder {
        private GithubExploreMessage message = new GithubExploreMessage();

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

        public GithubExploreMessage build() {
            GithubExploreMessage builtMessage = message;
            message = new GithubExploreMessage();
            return builtMessage;
        }
    }
}
