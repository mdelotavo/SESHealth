package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.seshealthpatient.R;
import team7.seshealthpatient.Util.MyUtilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends AppCompatActivity {
    private static String TAG = "ProfileActivity";
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Toolbar toolbar;
    private String[] userValues;

    @BindView(R.id.profileLinearLayout)
    LinearLayout profileLinearLayout;

    @BindView(R.id.profileNameTV)
    TextView profileNameTV;

    @BindView(R.id.profileEmailTV)
    TextView profileEmailTV;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Bundle extra = getIntent().getExtras();
        userValues = extra.getStringArray("userValues");

        Intent intent = getIntent();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(intent.getStringExtra("uid"));

        TextView[] textViewsProfile = {profileNameTV, profileEmailTV};
        String[] childrenProfile = {"name", "email"};

        setTVValuesProfile(textViewsProfile, childrenProfile);

        reference.child("accountType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    Toast.makeText(ProfileActivity.this, "An account type does not exist for this user", Toast.LENGTH_SHORT).show();
                } else {
                    if(dataSnapshot.getValue().toString().equals("patient")) {
                        Log.d(TAG, dataSnapshot.getValue().toString());
                        setPatientView();
                    } else if(dataSnapshot.getValue().toString().equals("doctor")) {
                        Log.d(TAG, dataSnapshot.getValue().toString());
                        setDoctorView();
                    } else {
                        Log.d(TAG, dataSnapshot.getValue().toString());
                        Toast.makeText(ProfileActivity.this, "Could not find a valid account type", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
                Toast.makeText(ProfileActivity.this, "An error occurred when connecting with the database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Put this in a utility class
    public void setTVValuesProfile(TextView[] textViews, String[] children) {
        for (int i = 0; i < textViews.length; i++)
            MyUtilities.setTVValuesProfile(reference, textViews[i], children[i]);
    }

    public void setTVValues(TextView[] textViews, String[] children) {
        for (int i = 0; i < textViews.length; i++)
            MyUtilities.setTVValues(reference, textViews[i], children[i]);
    }

    private void setPatientView() {
        TextView mobileTV = new TextView(ProfileActivity.this);
        TextView dobTV = new TextView(ProfileActivity.this);
        TextView genderTV = new TextView(ProfileActivity.this);
        TextView heightTV = new TextView(ProfileActivity.this);
        TextView weightTV = new TextView(ProfileActivity.this);
        TextView allergiesTV = new TextView(ProfileActivity.this);
        TextView medicalTV = new TextView(ProfileActivity.this);

        TextView[] textViewsProfile = { mobileTV, dobTV, genderTV, heightTV, weightTV};
        String[] childrenProfile = {"phoneNO", "DOB", "gender", "height", "weight"};

        TextView[] textViews = { allergiesTV, medicalTV};
        String[] childrenViews = { "allergies", "medication"};
        setTVValuesProfile(textViewsProfile, childrenProfile);
        setTVValues(textViews, childrenViews);

        for(int i = 0; i < textViewsProfile.length; i++) {
            profileLinearLayout.addView(textViewsProfile[i]);
        }
        for(int i = 0; i < textViews.length; i++) {
            profileLinearLayout.addView(textViews[i]);
        }
    }

    private void setDoctorView() {
        TextView occupation = new TextView(ProfileActivity.this);
        TextView[] textViewsProfile = {occupation};
        String[] childrenProfile = {"occupation"};
        setTVValuesProfile(textViewsProfile, childrenProfile);

        for(int i = 0; i < textViewsProfile.length; i++) {
            profileLinearLayout.addView(textViewsProfile[i]);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
