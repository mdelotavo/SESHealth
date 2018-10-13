package team7.seshealthpatient.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.seshealthpatient.R;
import team7.seshealthpatient.Util.MyUtilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends AppCompatActivity {
    private static String TAG = "ProfileActivity";

    @BindView(R.id.nameProfileTV)
    TextView nameProfileTV;

    @BindView(R.id.genderProfileTV)
    TextView genderProfileTV;

    @BindView(R.id.dobProfileTV)
    TextView dobProfileTV;

    @BindView(R.id.phoneProfileTV)
    TextView phoneProfileTV;

    @BindView(R.id.weightProfileTV)
    TextView weightProfileTV;

    @BindView(R.id.heightProfileTV)
    TextView heightProfileTV;

    @BindView(R.id.allergiesProfileTV)
    TextView allergiesProfileTV;

    @BindView(R.id.medicationProfileTV)
    TextView medicationProfileTV;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Toolbar toolbar;
    private TextView[] textViews;
    private String[] children;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.profileToolbar);
        toolbar.setTitle("Patient Profile");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(intent.getStringExtra("uid"));

        textViews = new TextView[]{nameProfileTV, genderProfileTV, dobProfileTV, phoneProfileTV,
                weightProfileTV, heightProfileTV, allergiesProfileTV, medicationProfileTV};

        children = new String[]{"name", "gender", "DOB", "phoneNO", "weight",
                "height", "allergies", "medication"};

        setPatientView();
    }

    // Put this in a utility class
    public void setTVValuesProfile(TextView textViews, String children) {
        MyUtilities.setTVValuesProfile(reference, textViews, children);
    }

    public void setTVValues(TextView textViews, String children) {
        MyUtilities.setTVValues(reference, textViews, children);
    }

    private void setPatientView() {
        for (int i = 0; i < textViews.length; i++)
            if (i < 6)
                setTVValuesProfile(textViews[i], children[i]);
            else
                setTVValues(textViews[i], children[i]);
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
