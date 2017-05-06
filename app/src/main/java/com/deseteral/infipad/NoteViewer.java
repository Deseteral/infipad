package com.deseteral.infipad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import us.feras.mdv.MarkdownView;

public class NoteViewer extends Fragment {
    private MarkdownView markNoteContent;
    private final String initialContent;

    private static final String TAG = "NOTE_VIEWER_FRAGMENT";

    public NoteViewer(String initialContent) {
        this.initialContent = initialContent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_viewer, container, false);

        markNoteContent = (MarkdownView) view.findViewById(R.id.mark_note_content);
        markNoteContent.loadMarkdown(initialContent);
        return view;
    }

    public void updateContentView(String markdown) {
        markNoteContent.loadMarkdown(markdown);
    }

    public static NoteViewer newInstance(String initialContent) {
        return new NoteViewer(initialContent);
    }
}
