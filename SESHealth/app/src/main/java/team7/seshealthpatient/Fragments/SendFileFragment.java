package team7.seshealthpatient.Fragments;


import android.Manifest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.CursorLoader;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.Format;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.HeartBeat.HeartRateMonitor;
import team7.seshealthpatient.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static org.apache.commons.lang3.time.DateUtils.round;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendFileFragment extends Fragment {
    private static String TAG = "SendFileFragment";
    private static final int CAMERA_REQUEST_CODE = 5;
    private static final int HEARTBEAT_REQUEST_CODE = 6;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location mLocation = null;

    private Uri videoUri = null;
    private int heartBeatAvg = 0;

    private static final String[] CAMERA_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.packetNameTV)
    TextView packetNameTV;

    @BindView(R.id.packetDateOfBirthTV)
    TextView packetDateOfBirthTV;

    @BindView(R.id.packetGenderTV)
    TextView packetGenderTV;

    @BindView(R.id.packetMobileTV)
    TextView packetMobileTV;

    @BindView(R.id.packetHeightTV)
    TextView packetHeightTV;

    @BindView(R.id.packetWeightTV)
    TextView packetWeightTV;

    @BindView(R.id.packetMedicalTV)
    TextView packetMedicalTV;

    @BindView(R.id.packetAllergiesTV)
    TextView packetAllergiesTV;

    @BindView(R.id.packetHeartBeatTV)
    TextView packetHeartBeatTV;

    @BindView(R.id.packetGPSTV)
    TextView packetGPSTV;

    @BindView(R.id.packetMessageET)
    EditText packetMessageET;

    // Checkboxes
    @BindView(R.id.packetGPSCheck)
    CheckBox packetGPSCheck;

    public SendFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = ((MainActivity) getActivity()).getFirebaseAuth();
        mUser = mAuth.getCurrentUser();
        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Send file");
        progressDialog = new ProgressDialog(getActivity());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(mUser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_file, container, false);
        ButterKnife.bind(this, v);

        TextView[] textViewsProfile = {packetNameTV, packetDateOfBirthTV, packetGenderTV, packetMobileTV, packetHeightTV, packetWeightTV};
        TextView[] textViews = {packetAllergiesTV, packetMedicalTV};

        String[] childrenProfile = {"name", "DOB", "gender", "phoneNO", "height", "weight"};
        String[] children = {"allergies", "medication"};

        setTVValuesProfile(textViewsProfile, childrenProfile);
        setTVValues(textViews, children);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now that the view has been created, we can use butter knife functionality
    }

    public void setTVValuesProfile(TextView[] textViews, String[] children) {
        for (int i = 0; i < textViews.length; i++)
            ((MainActivity)getActivity()).setTVValuesProfile(textViews[i], children[i]);
    }

    public void setTVValues(TextView[] textViews, String[] children) {
        for (int i = 0; i < textViews.length; i++)
            ((MainActivity)getActivity()).setTVValues(textViews[i], children[i]);
    }

    // During onClick event, the camera application will open up allowing users to record video
    @OnClick(R.id.packetCameraBtn)
    public void cameraOnClick() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 12);
        } else {
            Log.d(TAG, "onClick: starting camera");
            Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);// change the quality of the video
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    @OnClick(R.id.packetHeartBeatBtn)
    public void heartBeatClicked() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                    Manifest.permission.CAMERA
            }, 11);
        } else {
            Intent intent = new Intent(getActivity(), HeartRateMonitor.class);
            startActivityForResult(intent, HEARTBEAT_REQUEST_CODE);
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

    private void getCurrentLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
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
                Log.d(TAG, "Provider enabled: " + s);
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "Provider disabled: " + s);
            }

        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        progressDialog.setMessage("Getting current location...");
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
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
            String uriString = uriPath[uriPath.length-1];

            final StorageReference ref = storageRef.child("Users/" + userId + "/videos/" + uriString);
            UploadTask uploadTask = ref.putStream(stream);
            uploadToFirebase(ref, uploadTask);
        } else if(requestCode == HEARTBEAT_REQUEST_CODE) {
            Log.d(TAG, "Back from heartbeat");
            if (resultCode == RESULT_OK) {
                heartBeatAvg = Integer.parseInt(data.getStringExtra("heartBeatAvg"));
                packetHeartBeatTV.setText(heartBeatAvg + "BPM");
            } else {
                Log.d(TAG, "An error occurred when getting the heartbeat");
            }
        }
    }

    private void uploadToFirebase(final StorageReference ref, UploadTask uploadTask) {
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "An error occurred when uploading the video: " + task.getException());
                    Toast.makeText(getActivity(), "An error occurred when uploading the video", Toast.LENGTH_SHORT).show();
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
                } else {
                    Log.d(TAG, "An error occurred when uploading the video: " + task.getException());
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "An error occurred when uploading the video", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Gets the path from the URI so the video captured can be uploaded to Firebase Storage
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getActivity(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @OnClick(R.id.packetSubmitBtn)
    public void sendPacket() {
        String name = packetNameTV.getText().toString();
        String DOB = packetDateOfBirthTV.getText().toString();
        String gender = packetGenderTV.getText().toString();
        String mobile = packetMobileTV.getText().toString();
        String height = packetHeightTV.getText().toString();
        String weight = packetWeightTV.getText().toString();
        String medication = packetMedicalTV.getText().toString();
        String allergies = packetAllergiesTV.getText().toString();
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
        userProfile.put("heartBeat", heartBeatAvg);

        if(packetGPSCheck.isChecked())
            userProfile.put("coordinates", setCoordinates(true));
        else
            userProfile.put("coordinates", setCoordinates(false));

        userProfile.put("message", message);

        // Sets an empty string if videoURI has not been set
        try {
            userProfile.put("videoURI", videoUri.toString());
        } catch(Exception e) {
            userProfile.put("videoURI", "");
        }

        // Creates a database reference with a unique ID and provides it with the data packet
        DatabaseReference ref = reference.child("Packets").push();
        ref.setValue(userProfile);
    }

    private Map setCoordinates(boolean isChecked) {
        Map coordinates = new HashMap();
        if(isChecked) {
            try {
                coordinates.put("latitude", mLocation.getLatitude());
                coordinates.put("longitude", mLocation.getLongitude());
            } catch(Exception e) {
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
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(getActivity(), permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                    return;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.locationPermissionException), Toast.LENGTH_SHORT).show();
                }
                break;
            case 11:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    heartBeatClicked();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.cameraPermissionException), Toast.LENGTH_SHORT).show();
                }
                break;
            case 12:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    cameraOnClick();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.recordVideoPermissionException), Toast.LENGTH_SHORT).show();
                }
        }
    }
}
