package team7.seshealthpatient.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.R;

/**
 * Class: PatientInformationFragment
 * Extends: {@link Fragment}
 * Author: Carlos Tirado < Carlos.TiradoCorts@uts.edu.au> and YOU!
 * Description:
 * <p>
 * This fragment's job will be that to display patients information, and be able to edit that
 * information (either edit it in this fragment or a new fragment, up to you!)
 * <p>

 */
public class PatientInformationFragment extends Fragment {

    @BindView(R.id.nameTV)
    TextView nameTV;

    @BindView(R.id.phoneTV)
    TextView phoneTV;

    @BindView(R.id.dobTV)
    TextView dobTV;

    @BindView(R.id.allergiesTV)
    TextView allergiesTV;

    @BindView(R.id.medicationTV)
    TextView medicationTV;

    @BindView(R.id.genderTV)
    TextView genderTV;

    public PatientInformationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note the use of getActivity() to reference the Activity holding this fragment

        getActivity().setTitle("Welcome User");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_patient_information, container, false);

        ButterKnife.bind(this, v);

        TextView[] textViews =
                {nameTV, phoneTV, dobTV, allergiesTV, medicationTV, genderTV};

        String[] children = {"fullName", "phoneNo", "birthDate",
                "allergies", "medication", "gender"};

        setTVValues(textViews, children);

        nameTV.setText("b");

        // Note how we are telling butter knife to bind during the on create view method
        ButterKnife.bind(this, v);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now that the view has been created, we can use butter knife functionality
    }

    public void setTVValues(TextView[] textViews, String[] children) {
        for (int i = 0; i < textViews.length; i++)
        ((MainActivity)getActivity()).setTVValues(textViews[i], children[i]);
    }
}
