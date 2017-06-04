package com.deseteral.infipad.domain;

import com.deseteral.infipad.service.ApplicationState;

import java.util.ArrayList;
import java.util.List;

public class Notepad {
    /**
     * List of notes
     */
    private List<Note> notes;

    /**
     * Creates empty notepad
     */
    public Notepad() {
        this.notes = new ArrayList<>();
    }

    /**
     * Find note by name
     * @param name the name to look for
     * @return index of the note in the notebook, -1 if not found
     */
    public int findIndexByName(String name) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getName().equals(name)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Find note by name
     * @param name the name to look for
     * @return note reference
     */
    public Note findNoteByName(String name) {
        final int index = findIndexByName(name);
        return notes.get(index);
    }

    /**
     * Loads current info for all notes in notepad
     */
    public void refresh() {
        notes = ApplicationState
                .getState()
                .getStorage()
                .getList();
    }

    /**
     * Search for the notes
     * @param phrase phrase to look for (names and tags)
     * @return list of notes
     */
    public List<Note> search(String phrase) {
        final List<Note> names = searchForName(phrase);
        final List<Note> tags = searchForTag(phrase);

        for (Note n : tags) {
            if (!names.contains(n)) {
                names.add(n);
            }
        }

        return names;
    }

    /**
     * Search for the notes with specified name
     * @param phrase the name to look for
     * @return list of notes
     */
    private List<Note> searchForName(String phrase) {
        List<Note> found = new ArrayList<>();

        for (Note n : notes) {
            if (n.getName().toLowerCase().contains(phrase.toLowerCase())) {
                found.add(n);
                break;
            }
        }

        return found;
    }

    /**
     * Search for the notes with specified tag
     * @param phrase the tag to look for
     * @return list of notes
     */
    private List<Note> searchForTag(String phrase) {
        List<Note> found = new ArrayList<>();

        for (Note n : notes) {
            for (String noteTag : n.getTags()) {
                if (noteTag.toLowerCase().contains(phrase.toLowerCase())) {
                    found.add(n);
                    break;
                }
            }
        }

        return found;
    }

    public List<Note> getNotes() {
        return notes;
    }
}
