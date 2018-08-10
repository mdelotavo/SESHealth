package team7.seshealthpatient.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team7.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendFileFragment extends Fragment {


    public SendFileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_file, container, false);
    }

}
