package team7.seshealthpatient.Fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.telecom.ConnectionService;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.Activities.DiagnosisHistoryActivity;
import team7.seshealthpatient.Activities.DiagnosisInfoActivity;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.Activities.ProfileActivity;
import team7.seshealthpatient.Patient;
import team7.seshealthpatient.R;

public class DiagnosisHistoryFragment extends Fragment {

    private final static String TAG = "DiagnosisHistoryFragment";
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String patientId;

    @BindView(R.id.noDiagnosticsMadeTV)
    TextView noDiagnosticsMadeTV;

    @BindView(R.id.diagnosis_history_list)
    ListView diagnosisListView;

    public DiagnosisHistoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Diagnosis History");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        mUser = ((MainActivity)getActivity()).getFirebaseAuth().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diagnosis_history, container, false);

        ButterKnife.bind(this, view);

        final List<String> diagnosisList = new ArrayList<>();
        final List<String> diagnosisUidList = new ArrayList<>();
        final List<String> diagnosisDoctorUidList = new ArrayList<>();

        final ListAdapter diagnosisListAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                diagnosisList
        );

        diagnosisListView.setAdapter(diagnosisListAdapter);
        diagnosisListView.setAdapter(diagnosisListAdapter);

        patientId = mUser.getUid();

        reference.child(patientId).child("Diagnosis")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        diagnosisList.clear();
                        noDiagnosticsMadeTV.setVisibility(View.VISIBLE);

                        if (dataSnapshot.exists()) {
                            // Loops through the doctors that have provided diagnostics for the user
                            for (DataSnapshot doctorSnapshot : dataSnapshot.getChildren()) {
                                if(doctorSnapshot.exists()) {
                                    // Loops through the packets that the doctor has replied to
                                    for(DataSnapshot packet : doctorSnapshot.getChildren()) {
                                        if(packet.exists()) {
                                            String key = packet.getKey().toString();
                                            String timeStamp = (packet.child("Timestamp").getValue() != null)
                                                    ? packet.child("Timestamp").getValue().toString() : null;
                                            if (timeStamp != null) {
                                                if(noDiagnosticsMadeTV.getVisibility() != View.INVISIBLE)
                                                    noDiagnosticsMadeTV.setVisibility(View.INVISIBLE);
                                                diagnosisList.add(timeStamp);
                                                diagnosisUidList.add(key.toString());
                                                diagnosisDoctorUidList.add(doctorSnapshot.getKey().toString());
                                                diagnosisListView.invalidateViews();
                                            }
                                        }
                                    }
                                }
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
                String doctorId = diagnosisDoctorUidList.get(position);
                Intent diagnosisInfo = new Intent(getActivity(), DiagnosisInfoActivity.class);
                diagnosisInfo.putExtra("patientId", patientId);
                diagnosisInfo.putExtra("packetId", key.toString());
                diagnosisInfo.putExtra("doctorId", doctorId.toString());
                startActivity(diagnosisInfo);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now that the view has been created, we can use butter knife functionality
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
