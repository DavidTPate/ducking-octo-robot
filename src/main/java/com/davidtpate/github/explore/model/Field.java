package com.davidtpate.github.explore.model;

import com.davidtpate.github.explore.util.Strings;

public class Field {
    private String name;
    private String value;

    public static Field parseField(String raw) {
        if (Strings.isEmpty(raw) || !raw.contains(": ")) {
            return null;
        }

        Builder fieldBuilder = new Builder();

        String[] partArray = raw.split(":");
        // I originally used a For Each, but that requires hacky access to the internal field in the Builder which is a bit hacky.
        for (int i = 0; i <= partArray.length - 1; i++) {
            if (i == 0) {
                fieldBuilder.name(partArray[i].trim());
            } else {
                // If we are dealing with a value that had colons in it, replace them.
                fieldBuilder.value((i > 1 ? ":" : "") + partArray[i].trim());
            }
        }

        return fieldBuilder.build();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {
        private Field field = new Field();

        public Builder name(String name) {
            field.name = name;
            return this;
        }

        public Builder value(String value) {
            // Since there could be a value with colons in it, we just append the value for any subsequent calls.
            if (field.value == null) {
                field.value = "";
            }

            field.value += value;
            return this;
        }

        public Field build() {
            Field builtField = field;
            field = new Field();
            return builtField;
        }
    }
}
