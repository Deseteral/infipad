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
    EditText textNoteContent;
    private OnEditorContentChangedListener mListener;
    private String initialNoteContent = "";

    private static final String TAG = "NOTE_EDITOR_FRAGMENT";

    /**
     * Creates new instance of the note editor fragment
     * @param noteContent the content of the note
     * @return note editor fragment
     */
    public static NoteEditor newInstance(String noteContent) {
        NoteEditor fragment = new NoteEditor();
        Bundle args = new Bundle();
        args.putString(NoteActivity.NOTE_CONTENT, noteContent);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inserts text at current cursor position
     * @param text text to insert
     */
    public void insertText(String text) {
        textNoteContent.getText().insert(textNoteContent.getSelectionStart(), text);
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

        textNoteContent = (EditText) view.findViewById(R.id.text_note_content);
        textNoteContent.setText(initialNoteContent);
        textNoteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String newContent = s.toString();
                mListener.onEditorContentChanged(newContent);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
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

    /**
     * Callback for editor content changed event
     */
    interface OnEditorContentChangedListener {

        /**
         * Callback for editor content changed event
         * @param newContent the new content of the editor
         */
        void onEditorContentChanged(String newContent);
    }
}
