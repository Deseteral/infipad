package com.deseteral.infipad.storage;

import com.deseteral.infipad.domain.Note;

import java.util.List;

public interface Storage {
    void saveNote(Note note);
    void loadNoteContent(Note note);
    List<Note> getList();
    void deleteNote(Note note);
}
