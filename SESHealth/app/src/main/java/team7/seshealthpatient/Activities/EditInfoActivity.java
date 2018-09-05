package team7.seshealthpatient.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.seshealthpatient.R;

public class EditInfoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser fireBaseUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private EditText[] editTexts;
    private String[] children;
    private String[] labels;

    @BindView(R.id.editPhoneET)
    EditText editPhoneET;

    @BindView(R.id.editWeightET)
    EditText editWeightET;

    @BindView(R.id.editHeightET)
    EditText editHeightET;

    @BindView(R.id.editAllergiesET)
    EditText editAllergiesET;

    @BindView(R.id.editMedicationET)
    EditText editMedicationET;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.editInfo_toolbar);
        toolbar.setTitle("Edit Info");

        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(fireBaseUser.getUid());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initializeArrays();

        for (int i = 0; i < editTexts.length; i++)
            if (i < 3)
                setETHintsProfile(editTexts[i], children[i], labels[i]);
            else
                setETHints(editTexts[i], children[i], labels[i]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu_save:
                if (!editPhoneET.getText().toString().isEmpty())
                    if (!isValidPhoneNumber(editPhoneET)) {
                        Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                updateValues();
                break;
            case R.id.edit_menu_cancel:
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                break;
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void setETHints(final EditText editText, String child, final String label) {

        reference.child(child)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            editText.setHint(label + "null");
                            Toast.makeText(EditInfoActivity.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                        } else
                            editText.setHint(label + dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public void setETHintsProfile(final EditText editText, String child, final String label) {

        reference.child("Profile").child(child)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            editText.setHint(label + "null");
                            Toast.makeText(EditInfoActivity.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                        } else
                            editText.setHint(label + dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public void initializeArrays() {
        editTexts = new EditText[]{editPhoneET, editWeightET,
                editHeightET, editAllergiesET, editMedicationET};

        children = new String[]{"phoneNO", "weight", "height", "allergies", "medication"};

        labels = new String[]{"Phone No: ", "Weight (kg): ", "Height (cm): ",
                "Allergies: ", "Medication: "};
    }

    public void updateValues() {
        for (int i = 0; i < editTexts.length; i++) {
            if (!editTexts[i].getText().toString().trim().isEmpty())
                if (i == 0)
                    reference.child("Profile").child(children[i]).setValue(editTexts[i].getText().toString().trim());
                else if (i < 3)
                    reference.child("Profile").child(children[i]).setValue(Double.parseDouble(editTexts[i].getText().toString().trim()));
                else
                    reference.child(children[i]).setValue(editTexts[i].getText().toString().trim());
        }
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        finish();
    }

    public boolean isValidPhoneNumber(EditText editText) {
        return (editText.getText().toString().replaceAll(" ", "").length() == 10 &&
                StringUtils.isNumericSpace(editText.getText().toString()));
    }
}
