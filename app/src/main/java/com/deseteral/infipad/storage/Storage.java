package com.deseteral.infipad.storage;

import java.util.List;

public interface Storage {
    void saveNote(String name, String content);
    String loadNoteContent(String name);
    List<String> getList();
}
