package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;

import java.util.List;

import javax.annotation.Nullable;

public class ContactsActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        fAuth = FirebaseAuth.getInstance();
        RecyclerView rv = findViewById(R.id.recycler);

        adapter = new GroupAdapter();

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);

                UserItem userItem = (UserItem) item;
                intent.putExtra("user", userItem.user);

                startActivity(intent);
            }
        });

        fetchUsers();

    }

    private void fetchUsers() {
        FirebaseFirestore.getInstance().collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e("Teste", e.getMessage(), e);
                return;
                }
                if(queryDocumentSnapshots != null) {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc: docs) {
                        User user = doc.toObject(User.class);
                        Log.d("Teste", user.getFName());
                        String uid = FirebaseAuth.getInstance().getUid();
                            adapter.add(new UserItem(user));
                            adapter.notifyDataSetChanged();

                    }
                }
            }
        });
        FirebaseFirestore.getInstance().collection("ProfileUrl").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e("Teste", e.getMessage(), e);
                    return;
                }
                if(queryDocumentSnapshots != null) {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    adapter.clear();
                    for (DocumentSnapshot doc: docs) {
                        User user = doc.toObject(User.class);
                        Log.d("Teste", user.getProfileUrl());
                        String uid = FirebaseAuth.getInstance().getUid();
                        if (user.getUuid().equals(uid))
                            continue;
                        adapter.add(new UserItem(user));
                        adapter.notifyDataSetChanged();

                    }
                }
            }
        });
    }

    private class UserItem extends Item<GroupieViewHolder> {

        private final User user;

        private UserItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            Log.d("Teste", position + "");
            TextView fName = viewHolder.itemView.findViewById(R.id.cont_fName);
            final ImageView profilePic = viewHolder.itemView.findViewById(R.id.cont_profile_pic);

            fName.setText(user.getFName());

            Picasso.get().load(user.getProfileUrl()).into(profilePic);

        }

        @Override
        public int getLayout() {
            return R.layout.item_user;
        }
    }
}