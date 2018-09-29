package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import team7.seshealthpatient.Fragments.ChatMessage;
import team7.seshealthpatient.R;

public class ChatActivity extends AppCompatActivity {

    FirebaseUser user;
    String uid, name;
    FloatingActionButton fab;
    EditText input;
    ListView listOfMessages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        Intent receivedIntent = getIntent();
        uid = receivedIntent.getStringExtra("uid");
        name = receivedIntent.getStringExtra("name");
        user = FirebaseAuth.getInstance().getCurrentUser();

        fab = findViewById(R.id.fab);
        input = findViewById(R.id.input);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().trim().isEmpty()) {
                    toastMessage("Your message is empty...");
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Users")
                            .child(uid)
                            .child("chat")
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(), name));
                    toastMessage("Sent");
                    input.setText("");
                }
            }
        });

        listOfMessages = findViewById(R.id.list_of_messages);

        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(
                this,
                ChatMessage.class, android.R.layout.simple_list_item_2,
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("chat")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText = v.findViewById(android.R.id.text1);
                messageText.setText(model.getMessageText());

                final android.text.format.DateFormat df = new android.text.format.DateFormat();

                TextView messageInfo = v.findViewById(android.R.id.text2);
                messageInfo.setText(
                        df.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()).toString()
                                + " " + model.getMessageUser()
                );
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
