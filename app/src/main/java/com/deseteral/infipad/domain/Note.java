package com.deseteral.infipad.domain;

import java.util.ArrayList;
import java.util.List;

public class Note {
    /**
     * The name of the note
     */
    private String name;

    /**
     * The content of the note
     */
    private String content;

    /**
     * Note's tags
     */
    private List<String> tags;

    /**
     * Creates new note
     * @param name the name of the note
     * @param content the content of the note
     * @param tags list of lags for the note
     */
    public Note(
            String name,
            String content,
            List<String> tags
    ) {
        this.name = name;
        this.content = content;
        this.tags = tags;
    }

    /**
     * Creates empty note
     * @param name the name of the note
     */
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
