package team7.seshealthpatient.Fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendFileFragment extends Fragment {
    private static String TAG = "SendFileFragment";
    private static final int  CAMERA_REQUEST_CODE = 5;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog progressDialog;
    private Uri videoUri;
    private FirebaseDatabase database;
    private DatabaseReference reference;


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

    @BindView(R.id.packetHeightTV)
    TextView packetHeightTV;

    @BindView(R.id.packetWeightTV)
    TextView packetWeightTV;

    @BindView(R.id.packetMedicalTV)
    TextView packetMedicalTV;

    @BindView(R.id.packetAllergiesTV)
    TextView packetAllergiesTV;

    @BindView(R.id.packetMessageET)
    EditText packetMessageET;

    public SendFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = ((MainActivity)getActivity()).getFirebaseAuth();
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

        TextView[] textViewsProfile = {packetNameTV, packetDateOfBirthTV, packetGenderTV};
        TextView[] textViews = {packetAllergiesTV, packetMedicalTV};

        String[] childrenProfile = {"name", "DOB", "gender"};
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
        if(checkPermissions(CAMERA_PERMISSION[0]) && checkPermissions(CAMERA_PERMISSION[1])){
            Log.d(TAG, "onClick: starting camera");
            Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);// change the quality of the video
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(getActivity(), getString(R.string.cameraPermissionException), Toast.LENGTH_LONG).show();
        }
    }

    // This is triggered when the user presses 'ok' after recording video
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading video, please wait...");
            progressDialog.show();
            Uri videoUri = data.getData();
            Log.d(TAG, "onActivityResult: done taking a video");
//            mVideoView.setVideoURI(videoUri);
//            mVideoView.start();

            String uri = getRealPathFromURI(videoUri);
            InputStream stream = null;
            try {
                stream = new FileInputStream(new File(uri));
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.toString());
                progressDialog.dismiss();
            }
            // FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Profile");
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String[] uriPath = uri.split("/[a-zA-z0-9]");
            String uriString = uriPath[uriPath.length-1];

            final StorageReference ref = storageRef.child("Users/" + userId + "/videos/" + uriString);
            UploadTask uploadTask = ref.putStream(stream);
            uploadToFirebase(ref, uploadTask);
             // videoUri = urlTask;
            // Log.d(TAG, "Video uri: " + videoUri);
        }
    }

    private void uploadToFirebase(final StorageReference ref, UploadTask uploadTask) {
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

    @OnClick(R.id.packetSubmitBtn)
    public void sendPacket() {
        String name = packetNameTV.getText().toString();
        String dob = packetDateOfBirthTV.getText().toString();
        String gender = packetGenderTV.getText().toString();
        String height = packetHeightTV.getText().toString();
        String weight = packetWeightTV.getText().toString();
        String medical = packetMedicalTV.getText().toString();
        String allergies = packetAllergiesTV.getText().toString();
        String message = packetMessageET.getText().toString();

        reference.child("Packet").child("name").setValue(name);
        reference.child("Packet").child("DOB").setValue(dob);
        reference.child("Packet").child("gender").setValue(gender);

        reference.child("Packet").child("allergies").setValue(allergies);
        reference.child("Packet").child("medication").setValue(medical);
    }
}
