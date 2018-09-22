package team7.seshealthpatient.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import team7.seshealthpatient.Activities.EditInfoActivity;
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
public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
//    private FirebaseDatabase database;
//    private DatabaseReference reference;

    private final static String TAG = "ProfileFragment";

    @BindView(R.id.profileLinearLayout)
    LinearLayout profileLinearLayout;

    @BindView(R.id.profileNameTV)
    TextView profileNameTV;

    @BindView(R.id.profileEmailTV)
    TextView profileEmailTV;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = ((MainActivity) getActivity()).getFirebaseAuth();
        mUser = mAuth.getCurrentUser();
        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Note how we are telling butter knife to bind during the on create view method
        ButterKnife.bind(this, v);
        TextView[] textViewsProfile = {profileNameTV, profileEmailTV};
        String[] childrenProfile = {"name", "phoneNO"};
        setTVValues(textViewsProfile, childrenProfile);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now that the view has been created, we can use butter knife functionality
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Put this in a utility class
    public void setTVValuesProfile(TextView[] textViews, String[] children) {
        for (int i = 0; i < textViews.length; i++)
            ((MainActivity)getActivity()).setTVValuesProfile(textViews[i], children[i]);
    }

    public void setTVValues(TextView[] textViews, String[] children) {
        for (int i = 0; i < textViews.length; i++)
            ((MainActivity)getActivity()).setTVValues(textViews[i], children[i]);
    }

    @OnClick(R.id.profileSubmitBtn)
    public void onClick() {
        setPatientView();
    }

    private void setPatientView() {
        TextView mobileTV = new TextView(getActivity());
        TextView dobTV = new TextView(getActivity());
        TextView genderTV = new TextView(getActivity());
        TextView heightTV = new TextView(getActivity());
        TextView weightTV = new TextView(getActivity());
        TextView allergiesTV = new TextView(getActivity());
        TextView medicalTV = new TextView(getActivity());

        TextView[] textViewsProfile = { mobileTV, dobTV, genderTV, heightTV, weightTV};
        String[] childrenProfile = {"phoneNO", "DOB", "gender", "height", "weight"};

        TextView[] textViews = { allergiesTV, medicalTV};
        String[] childrenViews = { "allergies", "medication"};
        setTVValues(textViewsProfile, childrenProfile);
        for(int i = 0; i < textViewsProfile.length; i++) {
            profileLinearLayout.addView(textViewsProfile[i]);
        }

    }

    private void setDoctorView() {

    }



}
