package com.davidtpate.github.explore.model;

public class Repository {
    private String name;
    private String url;
    private String type;
    private String description;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description +'\'' +
                '}';
    }

    public static class Builder {
        private Repository repository = new Repository();

        public Builder name(String name) {
            repository.name = name;
            return this;
        }

        public Builder url(String url) {
            repository.url = url;
            return this;
        }

        public Builder type(String type) {
            repository.type = type;
            return this;
        }

        public Builder description(String description) {
            repository.description = description;
            return this;
        }

        public Repository build() {
            Repository builtRepository = repository;
            repository = new Repository();
            return builtRepository;
        }
    }
}
