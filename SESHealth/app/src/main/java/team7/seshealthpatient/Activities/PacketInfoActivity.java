package team7.seshealthpatient.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.MapModels.LocationDefaults;
import team7.seshealthpatient.PacketInfo;
import team7.seshealthpatient.R;

public class PacketInfoActivity extends AppCompatActivity {

    private final String TAG = "PacketInfoActivity";
    private TextView[] textViews;
    private String[] childrenKeys;
    private String uid;
    private String packetId;
    private String videoURI;
    private String fileURI;
    private Toolbar toolbar;
    private String coordinates;
    private String locationReply;
    private boolean fileBtnDisabled;
    private boolean videoBtnDisabled;
    private DatabaseReference packetReference;
    private ProgressDialog progressDialog;
    private FirebaseUser mUser;
    private File videoFile = new File(
            Environment.getExternalStorageDirectory().getPath() + "/healthapp/video.mp4");


    // Request Codes
    private static final int LOCATION_REQUEST_CODE = 6;

    // Reply Fields
    EditText messageET;
    EditText weightET;
    EditText heightET;
    EditText allergiesET;
    EditText medicationET;
    EditText heartbeatET;
    TextView locationTV;

    @BindView(R.id.packetInfoToolbar)
    Toolbar packetInfoToolbar;

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

    @BindView(R.id.packetPlayVideoBtn)
    Button packetPlayVideoBtn;

    @BindView(R.id.packetDownloadFileBtn)
    Button packetDownloadFileBtn;

    @BindView(R.id.packetReplyBtn)
    Button packetReplyBtn;


    // Linear Layouts
    @BindView(R.id.packetMessageLL)
    LinearLayout packetMessageLL;

    @BindView(R.id.packetWeightLL)
    LinearLayout packetWeightLL;

    @BindView(R.id.packetHeightLL)
    LinearLayout packetHeightLL;

    @BindView(R.id.packetAllergiesLL)
    LinearLayout packetAllergiesLL;

    @BindView(R.id.packetMedicationLL)
    LinearLayout packetMedicationLL;

    @BindView(R.id.packetLocationLL)
    LinearLayout packetLocationLL;

    @BindView(R.id.packetHeartbeatLL)
    LinearLayout packetHeartbeatLL;

    @BindView(R.id.packetReplyLocationIV)
    ImageView packetReplyLocationIV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_info);

        ButterKnife.bind(this);

        fileBtnDisabled = false;
        videoBtnDisabled = false;
        videoURI = "";
        fileURI = "";
        coordinates = "";

        toolbar = findViewById(R.id.packetInfoToolbar);
        toolbar.setTitle("Packet Information");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);

        textViews = new TextView[]{packetNameInfoTV, packetMobileInfoTV, packetDOBInfoTV,
                packetGenderInfoTV, packetWeightInfoTV, packetHeightInfoTV, packetAllergiesInfoTV,
                packetMedicationInfoTV, packetLocationInfoTV, packetHeartbeatInfoTV, packetMessageInfoTV};

        childrenKeys = new String[]{"name", "mobile", "DOB", "gender", "weight", "height",
                "allergies", "medication", "location", "heartBeat", "message",
                "videoDownloadUri", "fileDownloadUri"};

        Intent receivedIntent = getIntent();
        uid = receivedIntent.getStringExtra("uid");
        packetId = receivedIntent.getStringExtra("packetId");

        packetReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Packets").child(packetId);

        getCoordinates();
        new SetView().execute();
    }

    class DownloadVideo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Retrieving the video ...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadVideo(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            playVideo();
        }
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
            setButtons();
        }
    }

    public void downloadVideo(String requestedVideoURL) {
        try {
            URL videoURL = new URL(requestedVideoURL);
            URLConnection connection = videoURL.openConnection();
            int contentLength = connection.getContentLength();

            videoFile.getParentFile().mkdir();
            videoFile.createNewFile();

            DataInputStream stream = new DataInputStream(videoURL.openStream());
            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(videoFile));
            dos.write(buffer);
            dos.flush();
            dos.close();
        } catch (Exception e) {
            System.out.println("------------------------------55---------------------");
            e.printStackTrace();
        }
    }

    public void playVideo() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(videoFile.getPath()), "video/mp4");
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoFile.delete();
        videoFile.getParentFile().delete();
    }

    @OnClick(R.id.packetReplyLocationIV)
    public void locationInMap() {
        if (!packetLocationInfoTV.getText().toString().equals("Not included") && !packetLocationInfoTV.getText().toString().isEmpty()) {
            try {
                coordinates = coordinates.replaceAll("[^0-9.,-]", "");
                String[] latLong = coordinates.split(",");
                String latitude = latLong[0];
                String longitude = latLong[1];
                Intent intent = new Intent(PacketInfoActivity.this, PatientLocationActivity.class);
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("uid", uid);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open location in Map", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(PacketInfoActivity.this, "The patient did not include their location", Toast.LENGTH_SHORT).show();
    }

    public void setPacketInfoValues(final String childKey, final TextView textView) {
        packetReference.child(childKey).addValueEventListener(new ValueEventListener() {
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

    public void setButtons() {
        packetReference.child("videoDownloadUri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    videoURI = dataSnapshot.getValue().toString().trim();
                    setVideoBtn();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        packetReference.child("fileDownloadUri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    fileURI = dataSnapshot.getValue().toString().trim();
                    setFileBtn();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setVideoBtn() {
        if (videoURI.trim().equals("Not included") || videoURI.trim().isEmpty()) {
            videoBtnDisabled = true;
            packetPlayVideoBtn.setBackgroundResource(R.drawable.disabled_bg_btn);
            packetPlayVideoBtn.setTextColor(Color.parseColor("#cc46aef7"));
            packetPlayVideoBtn.setText("No video sent");
        } else {
            packetPlayVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PacketInfoActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                        ActivityCompat.requestPermissions(PacketInfoActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                2);
                    } else {
                        new DownloadVideo().execute(videoURI);
                    }
                }
            });
        }
    }

    public void setFileBtn() {
        if (fileURI.trim().equals("Not included") || fileURI.trim().isEmpty()) {
            fileBtnDisabled = true;
            packetDownloadFileBtn.setBackgroundResource(R.drawable.disabled_bg_btn);
            packetDownloadFileBtn.setTextColor(Color.parseColor("#cc46aef7"));
            packetDownloadFileBtn.setText("No file sent");
        } else {
            packetDownloadFileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fileURI)));
                }
            });
        }
    }

    public void getCoordinates() {
        packetReference.child("coordinates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    coordinates = dataSnapshot.getValue().toString().trim();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.packetReplyMessageIV)
    public void setReplyMessageField() {
        messageET = displayEditText(messageET, packetMessageLL);
    }

    @OnClick(R.id.packetReplyWeightIV)
    public void setReplyWeightField() {
        weightET = displayEditText(weightET, packetWeightLL);
    }

    @OnClick(R.id.packetReplyHeightIV)
    public void setReplyHeightField() {
        heightET = displayEditText(heightET, packetHeightLL);
    }

    @OnClick(R.id.packetReplyAllergiesIV)
    public void setReplyAllergiesField() {
        allergiesET = displayEditText(allergiesET, packetAllergiesLL);
    }

    @OnClick(R.id.packetReplyMedicationIV)
    public void setReplyMedicationField() {
        medicationET = displayEditText(medicationET, packetMedicationLL);
    }

    @OnClick(R.id.packetReplyHeartbeatIV)
    public void setReplyHeartbeatField() {
        heartbeatET = displayEditText(heartbeatET, packetHeartbeatLL);
    }

    private EditText displayEditText(EditText et, LinearLayout ll) {
        if (et == null) {
            et = new EditText(PacketInfoActivity.this);
            ll.addView(et);
        } else {
            ll.removeView(et);
            et = null;
        }
        return et;
    }

    // Sends a packet back to the patient with a reply about all the info
    @OnClick(R.id.packetReplyBtn)
    public void replyToPacket() {
        Map<String, String> replyPacket = new HashMap<>();
        packetReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Diagnosis").child(mUser.getUid()).child(packetId);
        Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        String message =  editTextNotNull(messageET);
        String weight = editTextNotNull(weightET);
        String height = editTextNotNull(heightET);
        String allergies = editTextNotNull(allergiesET);
        String medication = editTextNotNull(medicationET);
        String heartbeat = editTextNotNull(heartbeatET);
        replyPacket.put("Timestamp", currentTimestamp.toString());
        replyPacket.put("message", message);
        replyPacket.put("weight", weight);
        replyPacket.put("height", height);

        if(locationReply != null)
            replyPacket.put("location", locationReply);
        else
            replyPacket.put("location", "No reply");

        replyPacket.put("allergies", allergies);
        replyPacket.put("medication", medication);
        replyPacket.put("heartBeat", heartbeat);

        packetReference.setValue(replyPacket).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(PacketInfoActivity.this, "Your reply was successfully submitted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(PacketInfoActivity.this, "An error occurred, please try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String editTextNotNull(EditText et) {
        return et != null ? et.getText().toString().trim() : "No reply";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    new DownloadVideo().execute(videoURI);
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.downloadVideoPermissionException), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE && resultCode == RESULT_OK) {
            if(locationTV == null) {
                locationTV = new TextView(this);
                locationReply = data.getStringExtra("location");
                locationTV.setText(locationReply);
                packetLocationLL.addView(locationTV);

            } else {
                locationReply = data.getStringExtra("location");
                locationTV.setText(locationReply);
            }
        }
    }
}
