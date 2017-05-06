package com.deseteral.infipad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import us.feras.mdv.MarkdownView;

public class NoteViewer extends Fragment {
    private MarkdownView markNoteContent;

    private static final String TAG = "NOTE_VIEWER_FRAGMENT";

    public NoteViewer() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_viewer, container, false);

        markNoteContent = (MarkdownView) view.findViewById(R.id.mark_note_content);
        return view;
    }

    public void updateContentView(String markdown) {
        markNoteContent.loadMarkdown(markdown);
    }

    public static NoteViewer newInstance() {
        return new NoteViewer();
    }
}
