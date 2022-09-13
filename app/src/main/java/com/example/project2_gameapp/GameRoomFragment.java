package com.example.project2_gameapp;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;


import com.example.project2_gameapp.databinding.FragmentGameRoomBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class GameRoomFragment extends Fragment {
    FragmentGameRoomBinding binding;
    //GameRoomFragmentListener mListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String ARG_GAME = "ARG_GAME";

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

    ArrayList<Card> playerHand;
    RecyclerView cardHandRecyclerView;
    LinearLayoutManager linearLayoutManager;
    GameRoomRecyclerViewAdapter adapter;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();

        cardHandRecyclerView = binding.playerHandRecyclerView;
        cardHandRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        cardHandRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new GameRoomRecyclerViewAdapter(playerHand);
        cardHandRecyclerView.setAdapter(adapter);

        //TODO: get game document
        DocumentReference docRef = db.collection("games").document(gameInstance.gameID);

        //TODO: deal 7 cards per hand

        Card currentCard = gameInstance.topCard;

        binding.textViewGameTitle.setText(gameInstance.getGameTitle());
        binding.currentCardValue.setText(currentCard.getValue());

        binding.currentCardImage.setColorFilter(Color.parseColor(currentCard.getColor()));

        binding.drawCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Card newCard = new Card();
                binding.currentCardValue.setText(newCard.getValue());
                binding.currentCardImage.setColorFilter(Color.parseColor(newCard.getColor()));
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

            public GameRoomViewHolder(@NonNull View itemView) {
                super(itemView);
                cardImage = itemView.findViewById(R.id.imageViewCardBack);
                cardValue = itemView.findViewById(R.id.textViewCardValue);

                //TODO: play card when clicked
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