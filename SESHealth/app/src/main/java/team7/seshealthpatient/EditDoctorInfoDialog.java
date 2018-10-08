package team7.seshealthpatient;

import android.app.ProgressDialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class EditDoctorInfoDialog extends DialogPreference {

    private FirebaseAuth mAuth;
    private FirebaseUser fireBaseUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @BindView(R.id.occupationDialogET)
    EditText occupationDialogET;

    @BindView(R.id.locationDialogET)
    EditText locationDialogET;

    @Override
    protected View onCreateView(ViewGroup parent) {
        return super.onCreateView(parent);
    }

    @Override
    protected void onBindDialogView(View view) {
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(fireBaseUser.getUid());

        setETHints("location", "Address", locationDialogET);
        setETHints("occupation", "Occupation", occupationDialogET);

        super.onBindDialogView(view);
    }

    @Override
    protected View onCreateDialogView() {
        return super.onCreateDialogView();
    }

    public EditDoctorInfoDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_edit_doctor);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (!occupationDialogET.getText().toString().trim().isEmpty())
                updateDoctorOccupation();
            if (!locationDialogET.getText().toString().trim().isEmpty())
                updateDoctorLocation();
            Toast.makeText(getContext(), "Your information has updated successfully", Toast.LENGTH_SHORT).show();
        }

        super.onDialogClosed(positiveResult);
    }

    public void updateDoctorLocation() {
        reference.child("Profile").child("location").setValue(locationDialogET.getText().toString().trim());
    }

    public void updateDoctorOccupation() {
        reference.child("Profile").child("occupation").setValue(occupationDialogET.getText().toString().trim());
    }

    public void setETHints(String key, final String label, final EditText editText) {
        reference.child("Profile").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                   editText.setHint(label + ": " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
