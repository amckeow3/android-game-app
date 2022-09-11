package com.example.project2_gameapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.project2_gameapp.databinding.ChatroomLineItemBinding;
import com.example.project2_gameapp.databinding.FragmentChatroomsBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatroomsFragment extends Fragment {
    private static final String TAG = "chatrooms fragment";
    ChatroomsFragmentListener mListener;
    FragmentChatroomsBinding binding;
    private FirebaseAuth mAuth;
    ArrayList<Chatroom> chatrooms = new ArrayList<>();
    ArrayList<Chatroom> userChatrooms = new ArrayList<>();
    ArrayList<User> membersList = new ArrayList<>();
    ChatroomsListAdapter chatroomsListAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    private void setupUI() {
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        getActivity().setTitle("Chatrooms");
        getUserAccountInfo();

        getAllChatroomsData();

        binding.imageViewNewChatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.createNewChatroom();
            }
        });
    }

    private void getUserAccountInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String userUid = user.getUid();

        db.collection("users")
                .document(userUid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Log.d(TAG, "user data ---->" + value.getData());
                        String name = value.getString("firstName") + " " + value.getString("lastName");
                    }
                });
    }

    private void getAllChatroomsData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("chatrooms")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        chatrooms.clear();
                        membersList.clear();

                        for (QueryDocumentSnapshot document: value) {
                            Log.d(TAG, "Chatroom Info: " + document);
                            mAuth = FirebaseAuth.getInstance();
                            Chatroom chatroom = new Chatroom();
                            chatroom.setId(document.getId());
                            chatroom.setName(document.getString("name"));
                            ArrayList<User> members = document.toObject(Chatroom.class).members;
                            for (User user : members) {
                                membersList.add(user);
                            }
                            Log.d(TAG, "MEMBERS ARRAY LIST" + membersList);
                            chatroom.members.addAll(membersList);
                            Log.d(TAG, "Members added ************* " + chatroom.members);
                            chatrooms.add(chatroom);
                        }
                        Log.d(TAG, "Chatrooms Array Items ---------> " + chatrooms);

                        chatroomsListAdapter.notifyDataSetChanged();
                    }
                });
    }

    class ChatroomsListAdapter extends RecyclerView.Adapter<ChatroomsListAdapter.ChatroomsViewHolder> {
        ArrayList<Chatroom> mChatrooms;

        public ChatroomsListAdapter(ArrayList<Chatroom> data) {
            this.mChatrooms = data;
        }

        @NonNull
        @Override
        public ChatroomsListAdapter.ChatroomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChatroomLineItemBinding binding = ChatroomLineItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ChatroomsListAdapter.ChatroomsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatroomsListAdapter.ChatroomsViewHolder holder, int position) {
            Chatroom chatroom = mChatrooms.get(position);
            holder.setupUI(chatroom);
        }

        @Override
        public int getItemCount() {
            return this.mChatrooms.size();
        }

        public class ChatroomsViewHolder extends RecyclerView.ViewHolder {
            ChatroomLineItemBinding mBinding;
            Chatroom mChatroom;
            int position;

            public ChatroomsViewHolder(@NonNull ChatroomLineItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Chatroom chatroom) {
                getActivity().setTitle("Chatrooms");
                mChatroom = chatroom;
                mBinding.textViewChatroomName.setText(mChatroom.getName());
                Log.d(TAG, "chatrooms: " + chatrooms.toString());
                Log.d(TAG, "userChatrooms: " + userChatrooms.toString());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String id = user.getUid();

                mBinding.buttonJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Chatroom selectedChatroom = mChatroom;
                        Log.d(TAG, "onClick: Selected Chatroom Id " + selectedChatroom);
                        mListener.openSelectedChatroom(selectedChatroom);
                    }
                });
            }
        }
    }

    public ChatroomsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatroomsBinding.inflate(inflater, container, false);
        getAllChatroomsData();
        setupUI();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.chatroomsRecyclerView;
        recyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        chatroomsListAdapter = new ChatroomsListAdapter(chatrooms);
        recyclerView.setAdapter(chatroomsListAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ChatroomsFragmentListener) context;
    }

    public interface ChatroomsFragmentListener {
        void goToLogin();
        void createNewChatroom();
        void openSelectedChatroom(Chatroom chatroom);
    }
}