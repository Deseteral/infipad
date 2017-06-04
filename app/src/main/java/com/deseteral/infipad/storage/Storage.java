package com.deseteral.infipad.storage;

import com.deseteral.infipad.domain.Note;

import java.util.List;

/**
 * Storage virtual representation
 */
public interface Storage {
    /**
     * Saves the note
     * @param note note to save
     */
    void saveNote(Note note);

    /**
     * Load the content of the note into memory
     * @param note the note to load
     */
    void loadNoteContent(Note note);

    /**
     * Gets list of notes in the storage
     * @return list of notes
     */
    List<Note> getList();

    /**
     * Deletes the note
     * @param note note to remove
     */
    void deleteNote(Note note);
}
