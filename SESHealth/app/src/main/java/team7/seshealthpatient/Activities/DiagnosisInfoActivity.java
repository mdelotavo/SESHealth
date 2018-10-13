package team7.seshealthpatient.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import butterknife.OnClick;
import team7.seshealthpatient.R;

public class DiagnosisInfoActivity extends AppCompatActivity {
    private final String TAG = "DiagnosisInfo";

    private TextView[] textViews;
    private String[] childrenKeys;
    private TextView[] replyTextViews;
    private String[] replyKeys;

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

    // Replies

    @BindView(R.id.packetMessageReplyTV)
    TextView packetMessageReplyTV;

    @BindView(R.id.packetWeightReplyTV)
    TextView packetWeightReplyTV;

    @BindView(R.id.packetHeightReplyTV)
    TextView packetHeightReplyTV;

    @BindView(R.id.packetAllergiesReplyTV)
    TextView packetAllergiesReplyTV;

    @BindView(R.id.packetMedicationReplyTV)
    TextView packetMedicationReplyTV;

    @BindView(R.id.packetLocationReplyTV)
    TextView packetLocationReplyTV;

    @BindView(R.id.packetHeartbeatReplyTV)
    TextView packetHeartbeatReplyTV;

    @BindView(R.id.recommendedLocationIV)
    ImageView recommendedLocationIV;


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

        replyTextViews = new TextView[] {packetMessageReplyTV, packetWeightReplyTV, packetHeightReplyTV, packetAllergiesReplyTV, packetMedicationReplyTV, packetLocationReplyTV, packetHeartbeatReplyTV};
        replyKeys = new String[] {"message", "weight", "height", "allergies", "medication", "location", "heartBeat"};

        if(extras != null) {
            patientId = extras.get("patientId").toString();
            packetId = extras.get("packetId").toString();
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
            for (int i = 0; i < replyTextViews.length; i++)
                setPacketReplyValues(replyKeys[i], replyTextViews[i]);
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

    public void setPacketReplyValues(final String replyKey, final TextView replyTextView) {
        Log.d(TAG, "packet id: " + packetId);
        reference.child("Diagnosis").child(packetId).child(replyKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (replyKey.equals("location") && !dataSnapshot.getValue().toString().trim().equals("No reply")) {
                    replyTextView.setText(dataSnapshot.getValue().toString().trim());
                    recommendedLocationIV.setVisibility(View.VISIBLE);
                } else {
                    replyTextView.setText("No reply");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.recommendedLocationIV)
    public void showRecommendedLocation() {
        String Coordinates = packetLocationReplyTV.getText().toString().trim();

        if (Coordinates.contains("lat")){
            String latLon = Coordinates.replaceAll("[^0-9.,-]","");
            //Toast.makeText(ChatActivity.this, "This is doctor recommended location: " + latLon, Toast.LENGTH_SHORT).show();

            String[] latLong = latLon.split(",");
            String latitude = latLong[0];
            String longitude = latLong[1];

            Intent intent = new Intent(DiagnosisInfoActivity.this, PatientViewDoctorRecommendationActivity.class);
            intent.putExtra("Latitude", latitude);
            intent.putExtra("Longitude", longitude);
            startActivity(intent);

        }
    }

}


