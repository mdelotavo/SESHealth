package team7.seshealthpatient.Fragments;


import android.Manifest;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordVideoFragment extends Fragment {
    private static String TAG = "RecordVideoFragment";
    private static final int  CAMERA_REQUEST_CODE = 5;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private static final String[] CAMERA_PERMISSION = {
            Manifest.permission.CAMERA
    };


    @BindView(R.id.cameraBtn)
    Button cameraBtn;

    @BindView(R.id.cameraVideoView)
    VideoView mVideoView;

    public RecordVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Record Video");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_record_video, container, false);
        ButterKnife.bind(this, v);
        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    // During onClick event, the camera application will open up allowing users to record video
    @OnClick(R.id.cameraBtn)
    public void cameraOnClick() {
        if(checkPermissions(CAMERA_PERMISSION[0])){
            Log.d(TAG, "onClick: starting camera");
            Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(getActivity(),
                    getString(R.string.cameraPermissionException),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    // This is triggered after the user presses 'ok' after recording video
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
            Uri videoUri = data.getData();
            Log.d(TAG, "onActivityResult: done taking a video");
            mVideoView.setVideoURI(videoUri);
            mVideoView.start();

            String uri = getRealPathFromURI(videoUri);
            InputStream stream = null;
            try {
                stream = new FileInputStream(new File(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Profile");

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String[] uriPath = uri.split("/[a-zA-z0-9]");
            String uriString = uriPath[uriPath.length-1];

            StorageReference videoRef = storageRef.child("Users/" + userId + "/videos/" + uriString);
            UploadTask uploadTask = videoRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "An error occurred in uploading the file: " + exception);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Log.d(TAG, "Upload video succeeded: " + taskSnapshot.toString());
                }
            });
        }
    }

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


}
