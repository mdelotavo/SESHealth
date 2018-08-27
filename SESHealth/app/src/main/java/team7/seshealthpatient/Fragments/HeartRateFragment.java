package team7.seshealthpatient.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team7.seshealthpatient.Activities.HeartRateMonitor;
import team7.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRateFragment extends Fragment {

    public HeartRateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent intent = new Intent(getActivity(), HeartRateMonitor.class);
        startActivity(intent);
        return inflater.inflate(R.layout.fragment_heart_rate, container, false);
    }

}
