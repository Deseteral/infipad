package com.deseteral.infipad.domain;

import com.deseteral.infipad.service.ApplicationState;

import java.util.ArrayList;
import java.util.List;

public class Notepad {
    private List<Note> notes;

    public Notepad() {
        this.notes = new ArrayList<>();
    }

    public int findIndexByName(String name) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getName().equals(name)) {
                return i;
            }
        }

        return -1;
    }

    public Note findNoteByName(String name) {
        final int index = findIndexByName(name);
        return notes.get(index);
    }

    public void refresh() {
        notes = ApplicationState
                .getState()
                .getStorage()
                .getList();
    }

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
