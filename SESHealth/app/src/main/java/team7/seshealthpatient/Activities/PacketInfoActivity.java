package team7.seshealthpatient.Activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import team7.seshealthpatient.PacketInfo;
import team7.seshealthpatient.R;

public class PacketInfoActivity extends AppCompatActivity {

    private TextView[] textViews;
    private String[] childrenKeys;
    ListView listOfPacketInfo;
    private ProgressDialog progressDialog;
    private File videoFile = new File(
            Environment.getExternalStorageDirectory().getPath() + "/healthapp/video.mp4");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_packet_info);
        listOfPacketInfo = findViewById(R.id.list_of_packet_info);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving the video ...");

        final String uid, packetId;

        /*textViews = new TextView[]{nameTV, mobileTV, dobTV, genderTV, weightTV,
                heightTV, allergiesTV, medicationTV, videoDownloadUriTV, locationTV, TimestampTV,
                fileDownloadUriTV, heartBeatTV, messageTV};

        childrenKeys = new String[]{"name", "mobile", "DOB", "gender", "weight",
                "height", "allergies", "medication", "videoDownloadUri", "location", "Timestamp",
                "fileDownloadUri", "heartBeat", "message"};*/

        Intent receivedIntent = getIntent();
        uid = receivedIntent.getStringExtra("uid");
        packetId = receivedIntent.getStringExtra("packetId");

        final List<PacketInfo> packetInfoList = new ArrayList<>();

        final ListAdapter adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                packetInfoList
        );

        listOfPacketInfo.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Packets").child(packetId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            String info = child.getValue() != null ? child.getValue().toString() : null;
                            if (info != null) {
                                packetInfoList.add(new PacketInfo(key, info));
                                listOfPacketInfo.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        listOfPacketInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PacketInfo selectedKey = packetInfoList.get(position);
                if (selectedKey.getkey().equals("videoDownloadUri"))
                    if (!selectedKey.getInfo().trim().isEmpty() && !selectedKey.getInfo().trim().equals("Not included"))
                        new DownloadVideo().execute(selectedKey.getInfo());
                    else
                        Toast.makeText(PacketInfoActivity.this, "no vid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class DownloadVideo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
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
            System.out.println("---------------------------------------------------");
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
}
