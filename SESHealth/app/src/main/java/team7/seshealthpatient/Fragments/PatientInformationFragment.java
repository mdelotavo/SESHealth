package team7.seshealthpatient.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import team7.seshealthpatient.Activities.EditInfoActivity;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.Activities.SendFileActivity;
import team7.seshealthpatient.R;
import team7.seshealthpatient.Util.MyUtilities;

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

    private TextView[] textViews;
    private String[] children;
    private FirebaseUser mUser;
    private String[] userValues;

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

    @BindView(R.id.weightTV)
    TextView weightTV;

    @BindView(R.id.heightTV)
    TextView heightTV;

    public PatientInformationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Patient Information");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_patient_information, container, false);

        ButterKnife.bind(this, v);

        setHasOptionsMenu(true);

        textViews = new TextView[]{nameTV, phoneTV, dobTV, genderTV, weightTV,
                heightTV, allergiesTV, medicationTV};

        children = new String[]{"name", "phoneNO", "DOB", "gender", "weight",
                "height", "allergies", "medication"};

        setValuesInit();

        // Note how we are telling butter knife to bind during the on create view method
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTVValues(textViews,children);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now that the view has been created, we can use butter knife functionality
    }

    public void setTVValues(TextView[] textViews, String[] children) {
        for (int i = 0; i < textViews.length; i++)
            if (i < 6)
                ((MainActivity) getActivity()).setTVValuesProfile(textViews[i], children[i]);
            else
                ((MainActivity) getActivity()).setTVValues(textViews[i], children[i]);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.send_file_info) {
            userValues = new String[8];
            for (int i = 0; i < textViews.length; i++)
                userValues[i] = textViews[i].getText().toString().trim();
            startActivity(new Intent(getContext(), SendFileActivity.class).putExtra("userValues", userValues));
        }
        return super.onOptionsItemSelected(item);
    }

    public void setValuesInit() {
        setTVValues(textViews, children);
    }
}
