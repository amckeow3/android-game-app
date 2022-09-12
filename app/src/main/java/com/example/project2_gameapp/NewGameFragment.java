package com.example.project2_gameapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project2_gameapp.databinding.FragmentChatroomsBinding;
import com.example.project2_gameapp.databinding.FragmentNewGameBinding;

public class NewGameFragment extends Fragment {
    FragmentNewGameBinding binding;
    NewGameFragmentListener mListener;

    private static final String ARG_PARAM_USER = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    User opponent;
    String opponentId;
    String opponentName;

    private void setupUI() {
        getActivity().findViewById(R.id.toolbar).setVisibility(View.INVISIBLE);
        binding.textViewRequestGame.setText("Request new UNO game with " + opponentName + "?");
    }

    public NewGameFragment() {
        // Required empty public constructor
    }

    public static NewGameFragment newInstance(User user) {
        NewGameFragment fragment = new NewGameFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            opponent = (User) getArguments().getSerializable(ARG_PARAM_USER);
            opponentId = opponent.getId();
            opponentName = opponent.getFirstName();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewGameBinding.inflate(inflater, container, false);
        setupUI();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (NewGameFragmentListener) context;
    }

    interface NewGameFragmentListener {

    }
}