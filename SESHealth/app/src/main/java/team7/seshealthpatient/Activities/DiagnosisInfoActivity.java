package team7.seshealthpatient.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.seshealthpatient.R;

public class DiagnosisInfoActivity extends AppCompatActivity {
    private final String TAG = "DiagnosisInfo";

    private TextView[] textViews;
    private String[] childrenKeys;

    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String patientId;
    private String packetId;

    @BindView(R.id.diagnosisInfoToolbar)
    Toolbar toolbar;

    @BindView(R.id.packetNameInfoTV)
    TextView packetNameInfoTV;

    @BindView(R.id.packetMessageInfoTV)
    TextView packetMessageInfoTV;

    @BindView(R.id.packetMobileInfoTV)
    TextView packetMobileInfoTV;

    @BindView(R.id.packetDOBInfoTV)
    TextView packetDOBInfoTV;

    @BindView(R.id.packetGenderInfoTV)
    TextView packetGenderInfoTV;

    @BindView(R.id.packetWeightInfoTV)
    TextView packetWeightInfoTV;

    @BindView(R.id.packetHeightInfoTV)
    TextView packetHeightInfoTV;

    @BindView(R.id.packetAllergiesInfoTV)
    TextView packetAllergiesInfoTV;

    @BindView(R.id.packetMedicationInfoTV)
    TextView packetMedicationInfoTV;

    @BindView(R.id.packetLocationInfoTV)
    TextView packetLocationInfoTV;

    @BindView(R.id.packetHeartbeatInfoTV)
    TextView packetHeartbeatInfoTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_info);

        ButterKnife.bind(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        Bundle extras = getIntent().getExtras();

        toolbar.setTitle("Packet Information");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        textViews = new TextView[]{packetNameInfoTV, packetMobileInfoTV, packetDOBInfoTV,
                packetGenderInfoTV, packetWeightInfoTV, packetHeightInfoTV, packetAllergiesInfoTV,
                packetMedicationInfoTV, packetLocationInfoTV, packetHeartbeatInfoTV, packetMessageInfoTV};

        childrenKeys = new String[]{"name", "mobile", "DOB", "gender", "weight", "height",
                "allergies", "medication", "location", "heartBeat", "message",
                "videoDownloadUri", "fileDownloadUri"};

        if(extras != null) {
            patientId = extras.get("patientId").toString();
            packetId = extras.get("packetId").toString();
        } else {
            patientId = mUser.getUid();
        }
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(patientId);

        new SetView().execute();
    }

    class SetView extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            for (int i = 0; i < textViews.length; i++)
                setPacketInfoValues(childrenKeys[i], textViews[i]);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    public void setPacketInfoValues(final String childKey, final TextView textView) {
        reference.child("Packets").child(packetId).child(childKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (childKey.equals("location") && !dataSnapshot.getValue().toString().trim().equals("Not included"))
                    textView.setText(dataSnapshot.getValue().toString().trim());
                else
                    textView.setText(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}


