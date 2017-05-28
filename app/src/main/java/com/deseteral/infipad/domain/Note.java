package com.deseteral.infipad.domain;

import java.util.ArrayList;
import java.util.List;

public class Note {
    private String name;
    private String content;
    private List<String> tags;

    public Note(
            String name,
            String content,
            List<String> tags
    ) {
        this.name = name;
        this.content = content;
        this.tags = tags;
    }

    public Note(String name) {
        this(name, "", new ArrayList<String>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
