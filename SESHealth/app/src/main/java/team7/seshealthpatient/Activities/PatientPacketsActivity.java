package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class PatientPacketsActivity extends AppCompatActivity {

    private String uid, name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        uid = receivedIntent.getStringExtra("uid");
        name = receivedIntent.getStringExtra("name");

        System.out.println(uid);
    }
}
