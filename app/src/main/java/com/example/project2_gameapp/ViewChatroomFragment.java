package com.example.project2_gameapp;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project2_gameapp.databinding.FragmentViewChatroomBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewChatroomFragment extends Fragment {
    private static final String TAG = "view chatroom frag";
    ViewChatroomFragment.ViewChatroomFragmentListener mListener;
    FragmentViewChatroomBinding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    private static final String ARG_PARAM_CHATROOM = "param1";

    Chatroom chatroomObject;
    String chatroomName;
    String chatroomId;
    Button leaveButton;
    FloatingActionButton sendButton;
    RecyclerView recyclerView, viewersRecyclerView;
    LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    ViewChatroomRecyclerViewAdapter adapter;
    ChatViewersRecyclerViewAdapter membersAdapter;

    ArrayList<Message> messageList;
    ArrayList<User> membersList;

    public ViewChatroomFragment() {
        // Required empty public constructor
    }

    public static ViewChatroomFragment newInstance(Chatroom chatroom) {
        ViewChatroomFragment fragment = new ViewChatroomFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CHATROOM, chatroom);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatroomObject = (Chatroom) getArguments().getSerializable(ARG_PARAM_CHATROOM);
            chatroomId = chatroomObject.getId();
            chatroomName = chatroomObject.getName();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewChatroomBinding.inflate(inflater, container, false);
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        getActivity().setTitle(chatroomName);
        getActivity().findViewById(R.id.toolbar).findViewById(R.id.buttonLeaveChatroom).setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        messageList = new ArrayList<>();
        recyclerView = binding.messagesRecyclerView;
        recyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ViewChatroomRecyclerViewAdapter(messageList);
        recyclerView.setAdapter(adapter);
        getMessages();

        membersList = new ArrayList<>();
        viewersRecyclerView = binding.viewersRecyclerView;
        viewersRecyclerView.setHasFixedSize(false);
        linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        viewersRecyclerView.setLayoutManager(linearLayoutManager2);
        membersAdapter = new ChatViewersRecyclerViewAdapter(membersList);
        viewersRecyclerView.setAdapter(membersAdapter);

        DocumentReference docRef = db.collection("chatrooms").document(chatroomId)
                .collection("members").document();

        HashMap<String, String> newMember = new HashMap<>();
        newMember.put("name", user.getDisplayName());
        newMember.put("userID", user.getUid());

        docRef.set(newMember).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Joined chatroom " + chatroomName);
                } else {
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage(task.getException().getMessage())
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                }
            }
        });

        getChatroomMembers();

        sendButton = view.findViewById(R.id.buttonSendMessage);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageTextEntered = binding.editTextMessage.getText().toString();

                if (messageTextEntered.isEmpty()) {
                    //alertdialog
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                    b.setTitle("Error")
                            .setMessage("Please enter a valid message!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    b.create().show();
                } else {
                    DocumentReference docRef = db.collection("chatrooms")
                            .document(chatroomId)
                            .collection("messages")
                            .document();

                    HashMap<String, Object> data = new HashMap<>();

                    data.put("id", docRef.getId());
                    data.put("message", messageTextEntered);
                    data.put("dateCreated", FieldValue.serverTimestamp());
                    data.put("creator", user.getDisplayName());
                    data.put("creatorID", user.getUid());
                    data.put("likes", new ArrayList<String>());

                    docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Message successfully posted!");
                            } else {
                                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                b.setTitle("Error creating message")
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                b.create().show();
                            }
                        }
                    });

                    binding.editTextMessage.setText("");

                }
            }
        });

        //leaveButton = view.findViewById(R.id.buttonLeave);
        getActivity().findViewById(R.id.toolbar).findViewById(R.id.buttonLeaveChatroom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = db.collection("chatrooms").document(chatroomId)
                        .collection("members")
                        .whereEqualTo("userID", user.getUid());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                db.collection("chatrooms").document(chatroomId)
                                        .collection("members")
                                        .document(doc.getId()).delete();
                            }
                            mListener.leaveChatroom();
                        }
                    }
                });
            }
    });

    binding.cardViewUnoGame.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mListener.newGame();
        }
    });
    }

    void getChatroomMembers() {
        db.collection("chatrooms").document(chatroomId)
                .collection("members")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        membersList.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            User viewer = new User();
                            viewer.id = doc.getString("userID");
                            viewer.firstName = doc.getString("name");

                            membersList.add(viewer);
                        }

                        membersAdapter.notifyDataSetChanged();
                    }
                });
    }

    void getMessages() {
        db.collection("chatrooms").document(chatroomId)
                .collection("messages")
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messageList.clear();

                        for (QueryDocumentSnapshot doc : value) {
                            Message m = doc.toObject(Message.class);
                            messageList.add(m);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ViewChatroomFragment.ViewChatroomFragmentListener) context;
    }

    class ViewChatroomRecyclerViewAdapter extends RecyclerView.Adapter<ViewChatroomRecyclerViewAdapter.ViewChatroomViewHolder> {
        ArrayList<Message> messageArrayList;

        public ViewChatroomRecyclerViewAdapter(ArrayList<Message> messages) {
            this.messageArrayList = messages;
        }

        @NonNull
        @Override
        public ViewChatroomRecyclerViewAdapter.ViewChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_line_item, parent, false);
            ViewChatroomViewHolder viewChatroomViewHolder = new ViewChatroomRecyclerViewAdapter.ViewChatroomViewHolder(view);

            return viewChatroomViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewChatroomRecyclerViewAdapter.ViewChatroomViewHolder holder, int position) {
            if (messageArrayList.size() != 0) {
                Message message = messageArrayList.get(position);
                holder.messageTextview.setText(message.getMessage());
                holder.posterName.setText(message.getCreator());
                holder.messageID = message.getId();
                holder.likes = message.getLikes();
                int likesCount = message.getLikes().size();

                if (likesCount == 1) {
                    holder.numLikes.setText(R.string.one_like_txt);
                } else {
                    holder.numLikes.setText(String.valueOf(likesCount) + " Likes | ");
                }

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference profilePic = storage.getReference().child("images/").child(message.getCreatorID());
                if (profilePic != null) {
                    profilePic.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Glide.with(getActivity())
                                        .load(task.getResult())
                                        .into(holder.profilePicture);
                            }
                        }
                    });
                } else {
                    holder.profilePicture.setImageResource(R.drawable.ic_person);
                }

                if (message.getDateCreated() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    String dateStr = sdf.format(message.getDateCreated().toDate());

                    holder.postDate.setText(dateStr);
                } else {
                    holder.postDate.setText("Loading...");
                }

                //adds delete button to user's posted comments only
                FirebaseUser user = mAuth.getCurrentUser();
                String id = user.getUid();

                if (message.getCreatorID().equals(id)) {
                    holder.deleteButton.setClickable(true);
                    holder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteButton.setClickable(false);
                    holder.deleteButton.setVisibility(View.INVISIBLE);
                }

                //set like button to liked/unliked img
                ArrayList<String> listOfUserLikes = message.getLikes();
                if (listOfUserLikes.contains(user.getUid())) {
                    holder.likeButton.setImageResource(R.drawable.like_favorite);
                } else {
                    holder.likeButton.setImageResource(R.drawable.like_not_favorite);
                }
            }
        }

        @Override
        public int getItemCount() {
            return messageArrayList.size();
        }

        class ViewChatroomViewHolder extends RecyclerView.ViewHolder {
            TextView posterName, postDate, numLikes, messageTextview;
            ImageView deleteButton, likeButton, profilePicture;
            String messageID;
            ArrayList<String> likes;

            public ViewChatroomViewHolder(@NonNull View itemView) {
                super(itemView);
                posterName = itemView.findViewById(R.id.textViewUserName);
                postDate = itemView.findViewById(R.id.textViewPostDate);
                numLikes = itemView.findViewById(R.id.textViewChatNumLikes);
                messageTextview = itemView.findViewById(R.id.textViewChatMessage);
                profilePicture = itemView.findViewById(R.id.imageViewAcctProfilePic);
                deleteButton = itemView.findViewById(R.id.imageViewDeleteButton);
                likeButton = itemView.findViewById(R.id.imageViewLikeButton);

                String userID = mAuth.getCurrentUser().getUid();

                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //like or unlike post
                        if (likes.contains(userID)) {
                            db.collection("chatrooms").document(chatroomId)
                                    .collection("messages").document(messageID)
                                    .update("likes", FieldValue.arrayRemove(userID));
                        } else {
                            db.collection("chatrooms").document(chatroomId)
                                    .collection("messages").document(messageID)
                                    .update("likes", FieldValue.arrayUnion(userID));
                        }
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.collection("chatrooms").document(chatroomId)
                                .collection("messages").document(messageID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Message successfully deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                                        b.setTitle("Error deleting message")
                                                .setMessage(e.getMessage())
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });
                                        b.create().show();
                                    }
                                });
                    }
                });
            }
        }
    }

    class ChatViewersRecyclerViewAdapter extends RecyclerView.Adapter<ChatViewersRecyclerViewAdapter.ChatViewersViewHolder> {
        ArrayList<User> membersArrayList;

        public ChatViewersRecyclerViewAdapter(ArrayList<User> members) {
            this.membersArrayList = members;
        }

        @NonNull
        @Override
        public ChatViewersRecyclerViewAdapter.ChatViewersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewer_line_item, parent, false);
            ChatViewersRecyclerViewAdapter.ChatViewersViewHolder viewersViewHolder = new ChatViewersRecyclerViewAdapter.ChatViewersViewHolder(view);

            return viewersViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewersRecyclerViewAdapter.ChatViewersViewHolder holder, int position) {
            if (membersArrayList.size() != 0) {
                User viewer = membersArrayList.get(position);
                holder.name.setText(viewer.firstName);

                FirebaseStorage storage = FirebaseStorage.getInstance();
               // StorageReference profilePic = storage.getReference().child("images/").child(viewer.getId());
                /*if (profilePic != null) {
                    profilePic.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Glide.with(getActivity())
                                        .load(task.getResult())
                                        .into(holder.profile);
                            }
                        }
                    });
                } else {
                    holder.profile.setImageResource(R.drawable.ic_person);
                }*/
            }
        }

        @Override
        public int getItemCount() {
            return membersArrayList.size();
        }

        class ChatViewersViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView profile;

            public ChatViewersViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.textViewerName);
                profile = itemView.findViewById(R.id.imageViewAcctProfilePic);


            }
        }
    }

    public interface ViewChatroomFragmentListener {
        void leaveChatroom();
        void newGame();
    }
}