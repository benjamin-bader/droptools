package com.bendb.example.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.JsonSnakeCase;
import org.joda.time.DateTime;

import java.util.List;

@AutoValue
@JsonSnakeCase
public abstract class BlogPost {
    @JsonProperty
    public abstract int postId();

    @JsonProperty
    public abstract String text();

    @JsonProperty
    public abstract DateTime createdAt();

    @JsonProperty
    public abstract ImmutableList<String> tags();

    BlogPost() {}

    public static BlogPost create(
            int postId,
            String text,
            DateTime createdAt,
            List<String> tags) {
        return new AutoValue_BlogPost(postId, text, createdAt, immutableCopy(tags));
    }

    /**
     * Creates an immutable list of tags, taking into account jOOQ's peculiar
     * mapping of an empty postgres array to a one-element list whose only
     * element is {@code null}.
     *
     * @param tags the list of tags
     * @return an immutable copy of tags.
     */
    private static ImmutableList<String> immutableCopy(List<String> tags) {
        if (tags == null
                || tags.size() == 0
                || (tags.size() == 1 && tags.get(0) == null)) {
            return ImmutableList.of();
        }

        return ImmutableList.copyOf(tags);
    }
}
