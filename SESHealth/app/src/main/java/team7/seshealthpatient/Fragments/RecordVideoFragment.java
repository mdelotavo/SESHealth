package team7.seshealthpatient.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;
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

    @OnClick(R.id.cameraBtn)
    public void cameraOnClick() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 8);
        if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
           Log.d(TAG, "OnClick: starting camera");
           startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
       } else {
           Log.d(TAG, "Sending user back to home screen");
           Intent intent = new Intent(getActivity(), MainActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           startActivity(intent);
       }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE) {
            Uri videoUri = data.getData();
            Log.d(TAG, "onActivityResult: done taking a photo");
            mVideoView.setVideoURI(videoUri);
            mVideoView.start();
        }
    }



}
