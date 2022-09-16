package com.example.project2_gameapp;

import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.project2_gameapp.databinding.FragmentGameRoomBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class GameRoomFragment extends Fragment {
    FragmentGameRoomBinding binding;
    //GameRoomFragmentListener mListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String ARG_GAME = "ARG_GAME";
    private static final int FULL_HAND = 7;

    private Game gameInstance;

    private void setupUI() {
        getActivity().setTitle("Game Room");
    }
    public GameRoomFragment() {
        // Required empty public constructor
    }

    public static GameRoomFragment newInstance(Game game) {
        GameRoomFragment fragment = new GameRoomFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GAME, game);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameInstance = (Game) getArguments().getSerializable(ARG_GAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameRoomBinding.inflate(inflater, container, false);
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    String player1ID, player2ID, topCardDocRefID;
    ArrayList<Card> playerHand;
    RecyclerView cardHandRecyclerView;
    LinearLayoutManager linearLayoutManager;
    GameRoomRecyclerViewAdapter adapter;
    Card currentCard;
    String turn;
    //boolean atStart = true;
    //TODO: add docRefs here to reduce code reuse
    DocumentReference turnDocRef, cardDocRef, gameDocRef;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        binding.textViewGameTitle.setText(gameInstance.getGameTitle());

        playerHand = new ArrayList<>();
        getPlayerHands();
        dealCards(mAuth.getCurrentUser().getUid());
        cardHandRecyclerView = binding.playerHandRecyclerView;
        cardHandRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        cardHandRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new GameRoomRecyclerViewAdapter(playerHand);
        cardHandRecyclerView.setAdapter(adapter);

        gameDocRef = db.collection("games").document(gameInstance.gameID);

        turnDocRef = db.collection("games").document(gameInstance.gameID)
                .collection("turn").document("current");

        cardDocRef = db.collection("games").document(gameInstance.gameID)
                .collection("topCard").document("current");

        /*DocumentReference docRef = db.collection("games").document(gameInstance.gameID)
                .collection("topCard").document();
        topCardDocRefID = docRef.getId();

        if(atStart) {
            docRef.set(gameInstance.topCard).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("qq", "Top card set");
                    currentCard = gameInstance.topCard;
                }
            });
            atStart = false;
        }*/

        //DocumentReference turnDocRef = db.collection("games").document(gameInstance.gameID).collection("turn").document("current");

        HashMap<String, String> data = new HashMap<>();
        data.put("currentTurn", gameInstance.currentTurn);
        turnDocRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("qq", "Initial turn set");
            }
        });

        turnDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    String currentTurnUserID = value.getString("currentTurn");
                    turn = currentTurnUserID;

                    db.collection("users").document(currentTurnUserID)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        User user = task.getResult().toObject(User.class);
                                        binding.textViewTurn.setText(user.getFirstName() + "'s Turn");
                                    }
                                }
                            });
                }
            }
        });

        //discard pile query + snapshot listener
        //DocumentReference cardDocRef = db.collection("games").document(gameInstance.gameID).collection("topCard").document("current");
        cardDocRef.set(gameInstance.topCard).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("qq", "Initial top card set");
                        currentCard = gameInstance.topCard;
                    }
                });

        cardDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    Card topCard = value.toObject(Card.class);
                    currentCard = topCard;
                    binding.currentCardValue.setText(topCard.getValue());
                    binding.currentCardImage.setColorFilter(Color.parseColor(topCard.getColor()));
                }
            }
        });
        //discard pile query + snapshot listener end

        //game document snapshot listener
        //DocumentReference gameDocRef = db.collection("games").document(gameInstance.gameID);
        gameDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                //TODO: add win condition, delete stuff once game ends, then mListener.goBackToLobby
                Log.d("qq", "gameFinished value: " + value.getBoolean("gameFinished"));
                if(value.getBoolean("gameFinished")) {
                    Log.d("qq", "game finished, deleting game here");
                    db.collection("users").document(mAuth.getCurrentUser().getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        User user = task.getResult().toObject(User.class);
                                        binding.textViewTurn.setText(user.getFirstName() + " Wins!");
                                    }
                                }
                            });
                }
            }
        });
        //game document snapshot listener end

        //draw button
        binding.drawCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(turn.equals(mAuth.getCurrentUser().getUid())){
                    Card newCard = new Card();
                    Log.d("qq", "newcard value" + newCard.value);

                    if(newCard.getValue().equals(currentCard.value) || newCard.getColor().equals(currentCard.color)) {
                        playCard(newCard);
                    } else {
                        String collectionName = "hand-" + mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("games").document(gameInstance.gameID)
                                .collection(collectionName).document();
                        newCard.setCardID(documentReference.getId());

                        documentReference.set(newCard).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "drew card" + newCard.value + newCard.color, Toast.LENGTH_SHORT).show();
                                    switchTurn();
                                }
                            }
                        });//TODO: add functionality for special cards
                    }
                } else {
                    Toast.makeText(getActivity(), "Waiting for other player to finish turn", Toast.LENGTH_SHORT).show();
                }

                /*if(newCard.getValue().equals(currentCard.value) || newCard.getColor().equals(currentCard.color)) {
                    playCard(newCard);
                } else if (newCard.getValue().equals("Draw 4")) {
                    //playDrawFour();
                } else {
                    if(mAuth.getCurrentUser().getUid().equals(player1ID)) {
                        player1Hand.add(newCard);
                        //updatePlayerHand(player1Hand);
                    } else {
                        player2Hand.add(newCard);
                        //updatePlayerHand(player2Hand);
                    }
                    adapter.notifyDataSetChanged();

                }*/

            }
        });
        //draw button end
        //TODO: add leave button functionality????
    }

    public void playCard(Card newTopCard) {
        //TODO: remove this, since not needed?
        /*CollectionReference cRef = db.collection("games").document(gameInstance.gameID)
                .collection("topCard");

        cRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot doc : task.getResult()) {
                        db.collection("games").document(gameInstance.gameID)
                                .collection("topCard").document(doc.getId())
                                .set(newTopCard).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("qq", "onSuccess: ");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("qq", "onFailure: ");
                                    }
                                });

                    }
                }
            }
        });*/
        //DocumentReference cardDocRef = db.collection("games").document(gameInstance.gameID)
                //.collection("topCard").document("current");

        if(newTopCard.getValue().equals("Draw 4")) {
            String[] colorSet = {"Red", "Green", "Yellow", "Blue"};

            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
            b.setTitle("Please choose a color")
                    .setItems(colorSet, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case 0:
                                    Log.d("qq", "chose red");
                                    newTopCard.setColor("Red");
                                    break;
                                case 1:
                                    Log.d("qq", "chose green");
                                    newTopCard.setColor("Green");
                                    break;
                                case 2:
                                    Log.d("qq", "chose yellow");
                                    newTopCard.setColor("Yellow");
                                    break;
                                case 3:
                                    Log.d("qq", "chose blue");
                                    newTopCard.setColor("Blue");
                                    break;
                            }
                        }
                    });
            b.create().show();

        }

        cardDocRef.set(newTopCard).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("qq", "new top card successfully set in playcard");
                String toastText = "Played" + newTopCard.getColor() + newTopCard.getValue();
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
                if(newTopCard.getValue().equals("Draw 4")) {
                    if()
                    String collectionName = "hand-" + player;
                    for(int i = 0; i < 4; i++) {

                    }
                } else if(newTopCard.getValue().equals("Skip")) {
                    switchTurn();
                    switchTurn();
                } else {
                    switchTurn();
                }

            }
        });


    }
    //TODO: add functionality for draw four
    public void playDrawFour() {
        String[] colorSet = {"Red", "Green", "Yellow", "Blue"};

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Please choose a color")
                .setItems(colorSet, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Log.d("qq", "chose red");
                                //playCard(new Card("Draw 4", "Red", ""));
                                break;
                            case 1:
                                Log.d("qq", "chose green");
                                //playCard(new Card("Draw 4", "Green", ""));
                                break;
                            case 2:
                                Log.d("qq", "chose yellow");
                                //playCard(new Card("Draw 4", "Yellow", ""));
                                break;
                            case 3:
                                Log.d("qq", "chose blue");
                                //playCard(new Card("Draw 4", "Blue", ""));
                                break;
                        }
                    }
                });
        b.create().show();
    }

    public void switchTurn(){
        String newTurn;
        if(turn.equals(gameInstance.player1)) {
            newTurn = gameInstance.player2;
        } else {
            newTurn = gameInstance.player1;
        }

        /*db.collection("users").document(newTurn)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            binding.textViewTurn.setText(user.getFirstName() + "'s Turn");
                        }
                    }
                });

        db.collection("games").document(gameInstance.gameID)
                .update("currentTurn", newTurn).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("qq", "new turn value: " + newTurn);
                    }
                });*/

        //DocumentReference turnDocRef = db.collection("games").document(gameInstance.gameID)
        //        .collection("turn").document("current");

        turnDocRef.update("currentTurn", newTurn).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("qq", "new turn successfully updated in switchturn");
                }
            }
        });
    }

    public void dealCards(String player) {
        String collectionName = "hand-" + player;
        for(int i = 0; i < FULL_HAND; i++) {
            DocumentReference documentReference = db.collection("games").document(gameInstance.gameID)
                    .collection(collectionName).document();
            Card newCard = new Card();
            newCard.setCardID(documentReference.getId());

            documentReference.set(newCard).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //Log.d("qq", "added card: " + newCard.value + " " + newCard.color + " to " + player + "'s hand");
                    } else {
                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                        b.setTitle("Error dealing cards")
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
        }
    }

    public void getPlayerHands() {
        String path = "hand-" + mAuth.getCurrentUser().getUid();
        db.collection("games").document(gameInstance.getGameID())
                .collection(path)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        playerHand.clear();
                        Log.d("qq", "snapshot listener for called for " + path);

                        for(QueryDocumentSnapshot doc : value) {
                            Card c = doc.toObject(Card.class);
                            playerHand.add(c);
                        }
                        Log.d("qq", "size of hand is: " + playerHand.size());
                        adapter.notifyDataSetChanged();

                        if(playerHand.size() == 0) {
                            //TODO: turn off snapshot listeners when/before gameFinished=true? so other docs don't update and screw up
                            db.collection("games").document(gameInstance.gameID)
                                    .update("gameFinished", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("qq", "set gameFinished to true");
                                        }
                                    });
                        }
                    }
                });

        /*db.collection("games").document(gameInstance.getGameID())
                .collection(gameInstance.getPlayer2())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        player2Hand.clear();
                        Log.d("qq", "snapshot listener for p2 called, updating hand");

                        for(QueryDocumentSnapshot doc : value) {
                            Card c = doc.toObject(Card.class);
                            player2Hand.add(c);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });*/
    }

    class GameRoomRecyclerViewAdapter extends RecyclerView.Adapter<GameRoomRecyclerViewAdapter.GameRoomViewHolder> {
        ArrayList<Card> cardArrayList;

        public GameRoomRecyclerViewAdapter(ArrayList<Card> cards){
            this.cardArrayList = cards;
        }
        @NonNull
        @Override
        public GameRoomRecyclerViewAdapter.GameRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_line_item, parent, false);
            GameRoomViewHolder gameRoomViewHolder = new GameRoomViewHolder(view);
            return gameRoomViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull GameRoomRecyclerViewAdapter.GameRoomViewHolder holder, int position) {
            if(cardArrayList.size() != 0) {
                Card card = cardArrayList.get(position);
                holder.cardID = card.getCardID();
                holder.cardValue.setText(cardArrayList.get(position).getValue());
                holder.cardImage.setColorFilter(Color.parseColor(cardArrayList.get(position).getColor()));
            }
        }

        @Override
        public int getItemCount() {
            return cardArrayList.size();
        }

        class GameRoomViewHolder extends RecyclerView.ViewHolder {
            ImageView cardImage;
            TextView cardValue;
            String cardID;
            boolean isTurn;

            public GameRoomViewHolder(@NonNull View itemView) {
                super(itemView);
                cardImage = itemView.findViewById(R.id.imageViewCardBack);
                cardValue = itemView.findViewById(R.id.textViewCardValue);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(turn.equals(mAuth.getCurrentUser().getUid())){
                            String path = "hand-" + mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = db.collection("games").document(gameInstance.gameID)
                                    .collection(path).document(cardID);
                            //Log.d("qq",  "id(cardID) is " + cardID);

                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Card playedCard = task.getResult().toObject(Card.class);
                                        if(playedCard.getValue().equals(currentCard.getValue()) ||
                                                playedCard.getColor().equals(currentCard.getColor()) ||
                                                playedCard.getValue().equals("Draw 4")) {

                                            playCard(playedCard);

                                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("qq", "card " + cardID + " deleted");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                        } else {
                                            Toast.makeText(getActivity(), "Card does not match top card, please choose another", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Waiting for other player to finish turn", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        }
    }

    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (GameRoomFragmentListener) context;
    }

    interface GameRoomFragmentListener {
        void goBackToLobby();
    }*/
}