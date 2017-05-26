package com.deseteral.infipad.storage;

import java.util.List;

public interface Storage {
    void saveNote(String name, String content);
    String loadNoteContent(String name);
    void getList(OnListFetchedCallback callback);
    void deleteNote(String name);

    interface OnListFetchedCallback {
        void onListFetched(List<String> list);
    }
}
