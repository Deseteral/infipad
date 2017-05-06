package com.deseteral.infipad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoteEditor.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoteEditor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteEditor extends Fragment {
    private OnFragmentInteractionListener mListener;
    private String initialNoteContent = "";

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

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
