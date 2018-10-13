package team7.seshealthpatient;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import team7.seshealthpatient.Activities.ChatActivity;
import team7.seshealthpatient.Activities.DiagnosisHistoryActivity;
import team7.seshealthpatient.Activities.PatientPacketsActivity;
import team7.seshealthpatient.Activities.ProfileActivity;
import team7.seshealthpatient.Fragments.PatientListFragment;

public class PatientListAdapter extends ArrayAdapter<Patient> {
    private static final String TAG = "PatientListAdapter";
    private Context mContext;
    private PatientListFragment fragment;
    int mResource;

    public PatientListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Patient> objects, PatientListFragment fragment) {
        super(context, resource, objects);
        this.mContext = context;
        this.fragment = fragment;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        final String patientUid = getItem(position).getId();

        Patient patient = new Patient(name, patientUid);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameTV = (TextView) convertView.findViewById(R.id.patientNameTV);
        ImageView patientProfileIV = (ImageView) convertView.findViewById(R.id.patientProfileIV);
        ImageView patientRejectIV = (ImageView) convertView.findViewById(R.id.patientRejectIV);
        ImageView patientAcceptIV = (ImageView) convertView.findViewById(R.id.patientAcceptIV);
        ImageView patientPacketIV = (ImageView) convertView.findViewById(R.id.patientPacketIV);
        ImageView patientDiagnosisIV = (ImageView) convertView.findViewById(R.id.patientDiagnosisIV);

        if (patientProfileIV != null)
            patientProfileIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        Intent patientPackets = new Intent(mContext, ProfileActivity.class);
                        patientPackets.putExtra("uid", patientUid);
                        mContext.startActivity(patientPackets);
                    }
                }
            });

        if (patientRejectIV != null)
            patientRejectIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        DatabaseReference reference = fragment.getDBReference();
                        FirebaseUser mUser = fragment.getUser();
                        reference.child(mUser.getUid()).child("Patients").child(patientUid).child("approved").setValue("declined");
                        reference.child(patientUid).child("Doctor").child("approved").setValue("declined");
                    }
                }
            });

        if (patientAcceptIV != null)
            patientAcceptIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        DatabaseReference reference = fragment.getDBReference();
                        FirebaseUser mUser = fragment.getUser();
                        reference.child(mUser.getUid()).child("Patients").child(patientUid).child("approved").setValue("accepted");
                        reference.child(patientUid).child("Doctor").child("approved").setValue("accepted");
                    }
                }
            });

        if (patientDiagnosisIV != null) {
            patientDiagnosisIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        Intent userChat = new Intent(mContext, DiagnosisHistoryActivity.class);
                        userChat.putExtra("uid", patientUid);
                        mContext.startActivity(userChat);
                    }
                }
            });
        }

        if (patientPacketIV != null)
            patientPacketIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        Intent patientPackets = new Intent(mContext, PatientPacketsActivity.class);
                        patientPackets.putExtra("uid", patientUid);
                        mContext.startActivity(patientPackets);
                    }
                }
            });

        nameTV.setText(name);
        return convertView;
    }
}
