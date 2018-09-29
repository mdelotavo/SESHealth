package team7.seshealthpatient.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import team7.seshealthpatient.Activities.ChatActivity;
import team7.seshealthpatient.R;

public class ChatFragment extends Fragment {

    FirebaseUser user;
    String uid, name;
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

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Profile").child("name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = (String) dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        listOfMessages = v.findViewById(R.id.list_of_users);

        final List<String> userList = new ArrayList<>();
        final List<String> userUidList = new ArrayList<>();

        final ListAdapter adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                userList
        );

        listOfMessages.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            String user = (child.child("Profile").child("name").getValue() != null)
                                    ? child.child("Profile").child("name").getValue().toString() : null;
                            if (user != null && !userUidList.contains(key)) {
                                userList.add(user);
                                userUidList.add(key);
                                listOfMessages.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        listOfMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userUid = userUidList.get(position);

                Intent userChat = new Intent(getActivity(), ChatActivity.class);
                userChat.putExtra("uid", userUid);
                userChat.putExtra("name", name);
                startActivity(userChat);
            }
        });

        return v;
    }
}
