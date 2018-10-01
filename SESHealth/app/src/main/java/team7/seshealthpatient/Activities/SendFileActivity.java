package team7.seshealthpatient.Activities;


import android.Manifest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.HeartBeat.HeartRateMonitor;
import team7.seshealthpatient.R;

import static org.apache.commons.lang3.time.DateUtils.round;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendFileActivity extends AppCompatActivity {
    private static String TAG = "SendFileActivity";
    private static final int CAMERA_REQUEST_CODE = 5;
    private static final int HEARTBEAT_REQUEST_CODE = 6;
    private static final int RECORD_VIDEO_REQUEST_PERMISSIONS = 10;
    private static final int HEART_BEAT_REQUEST_PERMISSIONS = 11;
    private static final int LOCATION_REQUEST_PERMISSIONS = 12;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Toolbar toolbar;
    private String[] userValues;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location mLocation = null;

    private Uri videoUri = null;
    private int heartBeatAvg = 0;

    Button selectFile, upload;
    TextView notification;
    Uri pdfUri;

    private static final String[] CAMERA_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.packetHeartBeatTV)
    TextView packetHeartBeatTV;

    @BindView(R.id.packetGPSTV)
    TextView packetGPSTV;

    @BindView(R.id.packetMessageET)
    EditText packetMessageET;

    // Checkboxes
    @BindView(R.id.packetGPSCheck)
    CheckBox packetGPSCheck;

    @BindView(R.id.packetHeartBeatCheck)
    CheckBox packetHeartBeatCheck;

    @BindView(R.id.packetCameraCheck)
    CheckBox packetCameraCheck;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        ButterKnife.bind(this);

        Bundle extra = getIntent().getExtras();
        userValues = extra.getStringArray("userValues");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // Note the use of getActivity() to reference the Activity holding this fragment

        toolbar = findViewById(R.id.sendFileToolbar);
        toolbar.setTitle("Send File");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressDialog = new ProgressDialog(this);
        ///////////////////////
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(mUser.getUid());
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        selectFile = findViewById(R.id.selectFile);
        upload = findViewById(R.id.upload);
        notification = findViewById(R.id.notification);
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SendFileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectPdf();
                }
                if (ContextCompat.checkSelfPermission(SendFileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectPic();
                }
                else
                    ActivityCompat.requestPermissions(SendFileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
            }
        });///////////////
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pdfUri!=null)
                    uploadFile(pdfUri);
                else
                    Toast.makeText(SendFileActivity.this,"Select a File",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadFile(Uri pdfUri){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file...");
        progressDialog.setProgress(0);
        progressDialog.show();
        final String fileName=System.currentTimeMillis()+"";
        StorageReference storageReference=storage.getReference();
        storageReference.child("Users/"+userId+"/Uploads"+pdfUri).child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        DatabaseReference reference=database.getReference();
                        reference.child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(SendFileActivity.this,"File succesfully uploaded",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(SendFileActivity.this,"File NOT succesfully uploaded",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SendFileActivity.this,"File NOT succesfully uploaded",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        }); //////////////
    }

    private void selectPdf(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }
    private void selectPic(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }


    //////
    // During onClick event, the camera application will open up allowing users to record video
    public void recordVideo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, RECORD_VIDEO_REQUEST_PERMISSIONS);
        } else {
            Log.d(TAG, "onClick: starting camera");
            Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);// change the quality of the video
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    public void getHeartRate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA
            }, HEART_BEAT_REQUEST_PERMISSIONS);
        } else {
            AlertDialog.Builder heartRateAlertBuilder = new AlertDialog.Builder(this);
            heartRateAlertBuilder.setMessage("Launching the heart rate scanner will turn your flash on. Would you like to continue?");
            heartRateAlertBuilder.setCancelable(true);

            heartRateAlertBuilder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), HeartRateMonitor.class);
                            startActivityForResult(intent, HEARTBEAT_REQUEST_CODE);
                        }
                    });

            heartRateAlertBuilder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            heartRateAlertBuilder.show();
        }
    }

    @OnClick(R.id.packetGPSCheck)
    public void coordinatesClicked() {
        boolean checked = packetGPSCheck.isChecked();
        packetGPSCheck.setChecked(false);
        if (checked)
            getCurrentLocation();
        else
            locationManager.removeUpdates(locationListener);
    }

    @OnClick(R.id.packetHeartBeatCheck)
    public void heartBeatOnClick() {
        boolean checked = packetHeartBeatCheck.isChecked();
        packetHeartBeatCheck.setChecked(false);
        if (checked)
            getHeartRate();
    }

    @OnClick(R.id.packetCameraCheck)
    public void cameraOnClick() {
        boolean checked = packetCameraCheck.isChecked();
        packetCameraCheck.setChecked(false);
        if (checked)
            recordVideo();
    }

    private void getCurrentLocation() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        final boolean locationCaptured = false;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Latitude: " + location.getLatitude() + "\tLongitude: " + location.getLongitude());
                mLocation = location;
                String latitude = location.getLatitude() + "";
                String longitude = location.getLongitude() + "";
                String coordinates = latitude + " " + longitude;
                packetGPSTV.setText(coordinates);
                packetGPSCheck.setChecked(true);
                progressDialog.dismiss();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "Provider enabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getApplicationContext(), "Please turn on your location", Toast.LENGTH_SHORT).show();
                packetGPSCheck.setChecked(false);
                progressDialog.dismiss();
            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, LOCATION_REQUEST_PERMISSIONS);
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        progressDialog.setMessage("Getting current location...");
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading video, please wait...");
            progressDialog.show();
            Uri videoUri = data.getData();
            Log.d(TAG, "onActivityResult: done taking a video");

            String uri = getRealPathFromURI(videoUri);
            InputStream stream = null;
            try {
                stream = new FileInputStream(new File(uri));
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.toString());
                progressDialog.dismiss();
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Gets the filename which is added to Firebase
            String[] uriPath = uri.split("/[a-zA-z0-9]");
            String uriString = uriPath[uriPath.length - 1];

            final StorageReference ref = storageRef.child("Users/" + userId + "/videos/" + uriString);
            UploadTask uploadTask = ref.putStream(stream);
            uploadToFirebase(ref, uploadTask);
        } else if (requestCode == HEARTBEAT_REQUEST_CODE) {
            Log.d(TAG, "Back from heartbeat");
            if (resultCode == RESULT_OK) {
                heartBeatAvg = Integer.parseInt(data.getStringExtra("heartBeatAvg"));
                packetHeartBeatTV.setText(heartBeatAvg + "BPM");
                packetHeartBeatCheck.setChecked(true);
            } else {
                Log.d(TAG, "An error occurred when getting the heartbeat");
            }
        }

        if(requestCode==86 && resultCode==RESULT_OK && data!=null){
            pdfUri=data.getData();
            notification.setText("A file is selected: "+ data.getData().getLastPathSegment());
        }
        else{
            Toast.makeText(SendFileActivity.this,"Please select a file",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase(final StorageReference ref, UploadTask uploadTask) {
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "An error occurred when uploading the video: " + task.getException());
                    Toast.makeText(getApplicationContext(), "An error occurred when uploading the video", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    throw task.getException();
                }
                Log.d(TAG, "Video uri: " + ref.getDownloadUrl());
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    videoUri = downloadUri;
                    progressDialog.dismiss();
                    packetCameraCheck.setChecked(true);
                } else {
                    Log.d(TAG, "An error occurred when uploading the video: " + task.getException());
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "An error occurred when uploading the video", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Gets the path from the URI so the video captured can be uploaded to Firebase Storage
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @OnClick(R.id.packetSubmitBtn)
    public void sendPacket() {
        String name = userValues[0];
        String mobile = userValues[1];
        String DOB = userValues[2];
        String gender = userValues[3];
        double weight = Double.parseDouble(userValues[4]);
        double height = Double.parseDouble(userValues[5]);
        String allergies = userValues[6];
        String medication = userValues[7];
        String message = packetMessageET.getText().toString();

        Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        Map userProfile = new HashMap();
        userProfile.put("Timestamp", currentTimestamp.toString());
        userProfile.put("name", name);
        userProfile.put("DOB", DOB);
        userProfile.put("gender", gender);
        userProfile.put("mobile", mobile);
        userProfile.put("height", height);
        userProfile.put("weight", weight);
        userProfile.put("allergies", allergies);
        userProfile.put("medication", medication);

        if (packetGPSCheck.isChecked())
            userProfile.put("coordinates", setCoordinates(true));
        else
            userProfile.put("coordinates", setCoordinates(false));

        if (packetHeartBeatCheck.isChecked())
            userProfile.put("heartBeat", heartBeatAvg);
        else
            userProfile.put("heartBeat", "Not included");

        userProfile.put("message", message);

        // Sets an empty string if videoURI has not been set
        if (packetCameraCheck.isChecked())
            try {
                userProfile.put("videoURI", videoUri.toString());
            } catch (Exception e) {
                userProfile.put("videoURI", "");
            }
        else
            userProfile.put("videoURI", "");

        // Creates a database reference with a unique ID and provides it with the data packet
        DatabaseReference ref = reference.child("Packets").push();
        ref.setValue(userProfile);

        Toast.makeText(this, "Your information has uploaded successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private Map setCoordinates(boolean isChecked) {
        Map coordinates = new HashMap();
        if (isChecked) {
            try {
                coordinates.put("latitude", mLocation.getLatitude());
                coordinates.put("longitude", mLocation.getLongitude());
            } catch (Exception e) {
                coordinates.put("latitude", 0);
                coordinates.put("longitude", 0);
            }
        } else {
            coordinates.put("latitude", 0);
            coordinates.put("longitude", 0);
        }
        return coordinates;
    }

    // Checks camera permissions when using the camera to ensure the application doesn't crash if they do not allow access.
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(this, permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RECORD_VIDEO_REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    cameraOnClick();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.recordVideoPermissionException), Toast.LENGTH_SHORT).show();
                }
                break;
            case HEART_BEAT_REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    heartBeatOnClick();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.cameraPermissionException), Toast.LENGTH_SHORT).show();
                }
                break;
            case LOCATION_REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.locationPermissionException), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPdf();
            selectPic();
        }
        else
            Toast.makeText(SendFileActivity.this,"Please provide permission...",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}