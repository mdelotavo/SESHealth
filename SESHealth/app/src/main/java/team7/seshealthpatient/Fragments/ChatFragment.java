package team7.seshealthpatient.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import team7.seshealthpatient.R;

public class ChatFragment extends Fragment {

    FirebaseUser user;
    String uid;
    FloatingActionButton fab;
    EditText input;
    ListView listOfMessages;

    public ChatFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Chat");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        fab = v.findViewById(R.id.fab);
        input = v.findViewById(R.id.input);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users")
                        .child(uid)
                        .child("chat")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                        );
                toastMessage("Sent");
                input.setText("");
            }
        });

        listOfMessages = v.findViewById(R.id.list_of_messages);

        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(
                getActivity(),
                ChatMessage.class, android.R.layout.simple_list_item_1,
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("chat")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText = v.findViewById(android.R.id.text1);
                messageText.setText(model.getMessageText());
            }
        };

        listOfMessages.setAdapter(adapter);

        return v;
    }

    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
