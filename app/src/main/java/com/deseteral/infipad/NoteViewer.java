package com.deseteral.infipad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import us.feras.mdv.MarkdownView;

public class NoteViewer extends Fragment {
    private MarkdownView markNoteContent;
    private String initialContent;

    private static final String TAG = "NOTE_VIEWER_FRAGMENT";

    public static NoteViewer newInstance(String noteContent) {
        NoteViewer fragment = new NoteViewer();
        Bundle args = new Bundle();
        args.putString(NoteActivity.NOTE_CONTENT, noteContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            initialContent = getArguments().getString(NoteActivity.NOTE_CONTENT);
        }
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
}
