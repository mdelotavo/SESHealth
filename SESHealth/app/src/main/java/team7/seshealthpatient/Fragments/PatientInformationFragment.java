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
import team7.seshealthpatient.Activities.LoginActivity;
import team7.seshealthpatient.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fireBaseUser;

    @BindView(R.id.logout_btn)
    Button logout_btn;

    // Note how Butter Knife also works on Fragments, but here it is a little different
    @BindView(R.id.blank_frag_msg)
    TextView blankFragmentTV;


    public PatientInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        fireBaseUser = firebaseAuth.getCurrentUser();

        if(firebaseAuth.getCurrentUser() == null) {
            //Check if correct context is being passed...
            startActivity(new Intent(getContext(), LoginActivity.class));
        }

        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Welcome " + fireBaseUser.getEmail());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_patient_information, container, false);

        // Note how we are telling butter knife to bind during the on create view method
        ButterKnife.bind(this, v);

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now that the view has been created, we can use butter knife functionality
        blankFragmentTV.setText("Welcome to this fragment");
    }

    @OnClick(R.id.logout_btn)
    public void logout() {
        firebaseAuth.signOut();
        if(firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }
}
