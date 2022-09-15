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


import com.example.project2_gameapp.databinding.FragmentGameRoomBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

    String player1ID, player2ID;
    ArrayList<Card> player1Hand, player2Hand;
    RecyclerView cardHandRecyclerView;
    LinearLayoutManager linearLayoutManager;
    GameRoomRecyclerViewAdapter adapter;
    Card currentCard;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        DocumentReference docRef = db.collection("games").document(gameInstance.gameID);

        player1ID = gameInstance.getPlayer1();
        player2ID = gameInstance.getPlayer2();

        player1Hand = new ArrayList<>();
        player2Hand = new ArrayList<>();
        cardHandRecyclerView = binding.playerHandRecyclerView;
        cardHandRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        cardHandRecyclerView.setLayoutManager(linearLayoutManager);
        if(mAuth.getCurrentUser().getUid().equals(player1ID)) {
            adapter = new GameRoomRecyclerViewAdapter(player1Hand);
        } else {
            adapter = new GameRoomRecyclerViewAdapter(player2Hand);
        }
        cardHandRecyclerView.setAdapter(adapter);


        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    gameInstance = value.toObject(Game.class);
                    currentCard = gameInstance.topCard;
                    binding.currentCardValue.setText(gameInstance.topCard.getValue());
                    binding.currentCardImage.setColorFilter(Color.parseColor(gameInstance.topCard.getColor()));
                }
            }
        });

        binding.textViewGameTitle.setText(gameInstance.getGameTitle());

        binding.drawCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Card newCard = new Card();
                Log.d("qq", "newcard value" + newCard.value);

                if(newCard.getValue().equals(currentCard.value) || newCard.getColor().equals(currentCard.color)) {
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

                }

            }
        });
    }

    /*public void updatePlayerHand(ArrayList<Card> newHand) {
        //String playerID = mAuth.getCurrentUser().getUid();
        Log.d("qq", "updating arrays for player hands...");

        //if(playerID.equals(player1ID)) {
            db.collection("games").document(gameInstance.gameID)
                    .update("player1Hand", playerHand).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("qq", "player1Hand updated: " + playerHand.size());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
       // } else {
            db.collection("games").document(gameInstance.gameID)
                    .update("player2Hand", player2Hand).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("qq", "player2Hand updated: " + player2Hand.size());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
       // }
    }*/

    public void playCard(Card newTopCard) {
        /*db.collection("games").document(gameInstance.gameID)
                .update("topCard", newTopCard).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("qq", "newcard value " + newTopCard.value);
                    Log.d("qq", "newcard comp " + newTopCard.value.equals("Draw 4"));
                }
            }
        });*/
    }

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
                                playCard(new Card("Draw 4", "Red", ""));
                                break;
                            case 1:
                                Log.d("qq", "chose green");
                                playCard(new Card("Draw 4", "Green", ""));
                                break;
                            case 2:
                                Log.d("qq", "chose yellow");
                                playCard(new Card("Draw 4", "Yellow", ""));
                                break;
                            case 3:
                                Log.d("qq", "chose blue");
                                playCard(new Card("Draw 4", "Blue", ""));
                                break;
                        }
                    }
                });
        b.create().show();
    }

    public void dealCards() {
        ArrayList<Card> newHand = new ArrayList<>();
        for(int i = 0; i < FULL_HAND; i++) {
            Card newCard = new Card();
            newHand.add(newCard);
        }

        return newHand;
    }

    public void getPlayerHands() {
        db.collection("games").document(gameInstance.getGameID())
                .collection(gameInstance.getPlayer1())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        player1Hand.clear();

                        for(QueryDocumentSnapshot doc : value) {
                            Card c = doc.toObject(Card.class);
                            player1Hand.add(c);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });

        db.collection("games").document(gameInstance.getGameID())
                .collection(gameInstance.getPlayer2())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        player2Hand.clear();

                        for(QueryDocumentSnapshot doc : value) {
                            Card c = doc.toObject(Card.class);
                            player2Hand.add(c);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
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
                holder.card = cardArrayList.get(position);
                holder.hand = cardArrayList;
                holder.cardPosition = position;
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
            Card card;
            int cardPosition;
            ArrayList<Card> hand;

            public GameRoomViewHolder(@NonNull View itemView) {
                super(itemView);
                cardImage = itemView.findViewById(R.id.imageViewCardBack);
                cardValue = itemView.findViewById(R.id.textViewCardValue);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("qq",  "played " + card.getColor() + " " + card.getValue());
                        playCard(card);
                        hand.remove(cardPosition);
                        adapter.notifyDataSetChanged();
                        //updatePlayerHand(hand);
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

    }*/
}