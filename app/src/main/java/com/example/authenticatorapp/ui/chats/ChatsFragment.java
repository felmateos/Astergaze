package com.example.authenticatorapp.ui.chats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticatorapp.ChatActivity;
import com.example.authenticatorapp.R;
import com.example.authenticatorapp.User;
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

public class ChatsFragment extends Fragment {

    private ChatsViewModel chatsViewModel;
    private GroupAdapter adapter;
    FirebaseAuth fAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatsViewModel =
                ViewModelProviders.of(this).get(ChatsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chats, container, false);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.recycler_frag);

        fAuth = FirebaseAuth.getInstance();

        adapter = new GroupAdapter();

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(ChatsFragment.this.getActivity(), ChatActivity.class);

                ChatsFragment.UserItem userItem = (ChatsFragment.UserItem) item;
                intent.putExtra("user", userItem.user);

                startActivity(intent);
            }
        });

        fetchUsers();
        return root;
    }

    private void fetchUsers() {
        FirebaseFirestore.getInstance().collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Teste", e.getMessage(), e);
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        User user = doc.toObject(User.class);
                        Log.d("Teste", user.getFName());
                        String uid = FirebaseAuth.getInstance().getUid();
                        adapter.add(new ChatsFragment.UserItem(user));
                        adapter.notifyDataSetChanged();

                    }
                }
            }
        });
        FirebaseFirestore.getInstance().collection("ProfileUrl").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Teste", e.getMessage(), e);
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        User user = doc.toObject(User.class);
                        Log.d("Teste", user.getProfileUrl());
                        String uid = FirebaseAuth.getInstance().getUid();
                        if (user.getUuid().equals(uid))
                            continue;
                        adapter.add(new ChatsFragment.UserItem(user));
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