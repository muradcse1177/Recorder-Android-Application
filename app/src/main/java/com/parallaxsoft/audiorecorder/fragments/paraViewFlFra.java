package com.parallaxsoft.audiorecorder.fragments;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.parallaxsoft.audiorecorder.R;
import com.parallaxsoft.audiorecorder.adapters.paraViewFFileViewerAdapter;


public class paraViewFlFra extends Fragment{
    private static final String paraPositionAg = "position";
    private static final String paraLog = "paraViewFlFra";

    private int position;
    private paraViewFFileViewerAdapter paraViewFlFraparaViewFFileViewerAdapter;

    public static paraViewFlFra newInstance(int position) {
        paraViewFlFra f = new paraViewFlFra();
        Bundle b = new Bundle();
        b.putInt(paraPositionAg, position);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(paraPositionAg);
        observer.startWatching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);
        RecyclerView paraViewFlFraFileRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        paraViewFlFraFileRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        paraViewFlFraFileRecyclerView.setLayoutManager(llm);
        paraViewFlFraFileRecyclerView.setItemAnimator(new DefaultItemAnimator());
        paraViewFlFraparaViewFFileViewerAdapter = new paraViewFFileViewerAdapter(getActivity(), llm);
        paraViewFlFraFileRecyclerView.setAdapter(paraViewFlFraparaViewFFileViewerAdapter);

        return v;
    }

    FileObserver observer =
            new FileObserver(android.os.Environment.getExternalStorageDirectory().toString()
                    + "/SoundRecorder") {
                @Override
                public void onEvent(int event, String file) {
                    if(event == FileObserver.DELETE){
                        String filePath = android.os.Environment.getExternalStorageDirectory().toString()
                                + "/SoundRecorder" + file + "]";
                        Log.d(paraLog, "File deleted ["
                                + android.os.Environment.getExternalStorageDirectory().toString()
                                + "/SoundRecorder" + file + "]");
                        paraViewFlFraparaViewFFileViewerAdapter.removeOutOfApp(filePath);
                    }
                }
            };
}




