package com.deseteral.infipad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class NoteViewer extends Fragment {
    private View view;
    private WebView webNoteContent;

    private static final String TAG = "NOTE_VIEWER_FRAGMENT";

    public NoteViewer() { }

    public static NoteViewer newInstance() {
        NoteViewer fragment = new NoteViewer();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note_viewer, container, false);

        webNoteContent = (WebView) view.findViewById(R.id.web_note_content);
        return view;
    }

    public void updateContentView(String html) {
        webNoteContent.loadData(html, "text/html", null);
    }
}
