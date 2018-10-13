package team7.seshealthpatient.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisHistoryActivity extends AppCompatActivity {
    private final String TAG = "DiagnosisHistory";

    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String patientId;

    @BindView(R.id.diagnosisHistoryToolbar)
    Toolbar toolbar;

    @BindView(R.id.noDiagnosticsMadeTV)
    TextView noDiagnosticsMadeTV;

    @BindView(R.id.diagnosis_history_list)
    ListView diagnosisListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_history);

        ButterKnife.bind(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        Bundle extras = getIntent().getExtras();

        database = FirebaseDatabase.getInstance();

        toolbar.setTitle("Diagnosis History");

        // Set up the menu button
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        final List<String> diagnosisList = new ArrayList<>();
        final List<String> diagnosisUidList = new ArrayList<>();

        final ListAdapter diagnosisListAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                diagnosisList
        );

        diagnosisListView.setAdapter(diagnosisListAdapter);

        patientId = extras.getString("uid");

        reference.child(patientId).child("Diagnosis")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        diagnosisList.clear();
                        noDiagnosticsMadeTV.setVisibility(View.VISIBLE);

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey().toString();
                            String timeStamp = (child.child("Timestamp").getValue() != null)
                                    ? child.child("Timestamp").getValue().toString() : null;
                            if (timeStamp != null) {
                                if(noDiagnosticsMadeTV.getVisibility() != View.INVISIBLE)
                                    noDiagnosticsMadeTV.setVisibility(View.INVISIBLE);
                                diagnosisList.add(timeStamp);
                                diagnosisUidList.add(key.toString());
                                diagnosisListView.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        diagnosisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String key = diagnosisUidList.get(position);
                Intent diagnosisInfo = new Intent(DiagnosisHistoryActivity.this, DiagnosisInfoActivity.class);
                diagnosisInfo.putExtra("patientId", patientId);
                diagnosisInfo.putExtra("packetId", key.toString());
                DiagnosisHistoryActivity.this.startActivity(diagnosisInfo);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

