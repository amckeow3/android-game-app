package com.example.project2_gameapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameLobbyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameLobbyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GameLobbyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameLobbyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameLobbyFragment newInstance(String param1, String param2) {
        GameLobbyFragment fragment = new GameLobbyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_lobby, container, false);
    }

    ArrayList<Game> games;
    RecyclerView gameList;
    LinearLayoutManager linearLayoutManager;
    //adapter
    Button makeGame;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.game_lobby_fragment);

        games = new ArrayList<>();
        gameList = view.findViewById(R.id.gameList);
    }

    class GameLobbyRecyclerViewAdapter extends RecyclerView.Adapter<GameLobbyRecyclerViewAdapter.GameLobbyViewHolder> {
        ArrayList<Game> gamesArrayList;

        public GameLobbyRecyclerViewAdapter (ArrayList<Game> games) {
            this.gamesArrayList = games;
        }

        @NonNull
        @Override
        public GameLobbyRecyclerViewAdapter.GameLobbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_line_item, parent, false);
            GameLobbyViewHolder gameLobbyViewHolder = new GameLobbyRecyclerViewAdapter.GameLobbyViewHolder(view);
            return gameLobbyViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull GameLobbyRecyclerViewAdapter.GameLobbyViewHolder holder, int position) {
            if(gamesArrayList.size() != 0) {
                Game game = gamesArrayList.get(position);
            }
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class GameLobbyViewHolder extends RecyclerView.ViewHolder {
            TextView gameTitle;

            public GameLobbyViewHolder(@NonNull View itemView) {
                super(itemView);
                gameTitle = itemView.findViewById(R.id.gameTitle);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: clicking game item joins game
                    }
                });
            }

        }
    }
}