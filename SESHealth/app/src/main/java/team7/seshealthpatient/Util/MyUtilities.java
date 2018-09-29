package team7.seshealthpatient.Util;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MyUtilities {


    public static void setTVValues(DatabaseReference reference, final TextView textView, String child) {
        reference.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    textView.setText("null");
                else {
                    textView.setText("");
                    for (String value : dataSnapshot.getValue().toString().split(","))
                        textView.append("- " + value.trim() + "\n");
                    textView.setText(textView.getText().toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void setTVValuesProfile(DatabaseReference reference, final TextView textView, String child) {
        reference.child("Profile").child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    textView.setText("null");
                } else {
                    // Added (+ """) to make our Long values Strings so that we could set appropriate text values
                    textView.setText((dataSnapshot.getValue() + "").toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
