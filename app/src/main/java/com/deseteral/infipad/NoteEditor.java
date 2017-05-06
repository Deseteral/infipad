package com.deseteral.infipad;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NoteEditor extends Fragment {
    private OnEditorContentChangedListener mListener;
    private String initialNoteContent = "";

    private static final String TAG = "NOTE_EDITOR_FRAGMENT";

    public NoteEditor() { }

    public static NoteEditor newInstance(String noteContent) {
        NoteEditor fragment = new NoteEditor();
        Bundle args = new Bundle();
        args.putString(NoteActivity.NOTE_CONTENT, noteContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            initialNoteContent = getArguments().getString(NoteActivity.NOTE_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_note_editor, container, false);

        EditText textNoteContent = (EditText) view.findViewById(R.id.text_note_content);
        textNoteContent.setText(initialNoteContent);
        textNoteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String newContent = s.toString();
                mListener.onEditorContentChanged(newContent);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditorContentChangedListener) {
            mListener = (OnEditorContentChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditorContentChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    interface OnEditorContentChangedListener {
        void onEditorContentChanged(String newContent);
    }
}
