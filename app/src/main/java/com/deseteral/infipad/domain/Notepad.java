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

    public List<Note> searchForTag(String tag) {
        List<Note> found = new ArrayList<>();

        for (Note n : notes) {
            for (String t : n.getTags()) {
                if (t.equals(tag)) {
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
