package com.example.boxing_assistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boxing_assistant.databinding.FragmentBasicPanelBinding;



public class BasicPanel extends Fragment {

    FragmentBasicPanelBinding binding;
    View view;
    CardView timer, workout, music, tutorial, premium;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBasicPanelBinding.inflate(inflater, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getActivity().moveTaskToBack(true);

            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        view = binding.getRoot();
        timer = binding.timercard;

        workout = binding.combocard;
        music = binding.musiccard;
        tutorial = binding.tutorialcard;
        premium = binding.premiumcard;
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_basicPanel_to_basic_Timer);
            }
        });
        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate((R.id.action_basicPanel_to_timer_Sa_Kombinacijama));
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusic();
            }
        });
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_basicPanel_to_tutorial);
            }
        });
        return view;

    }

    public void selection(View v) {
        switch (((Button) v).getText().toString()) {
            case "TAJMER":
                Navigation.findNavController(view).navigate(R.id.action_basicPanel_to_basic_Timer);
                break;
            case "T i DZ":
                Navigation.findNavController(view).navigate((R.id.action_basicPanel_to_timer_Sa_Kombinacijama));
                break;
            case "MUSIC":
                //Navigation.findNavController(view).navigate((R.id.action_basicPanel_to_music));
                openMusic();
                break;

        }


    }

    private void openMusic() {
        Navigation.findNavController(view).navigate(R.id.action_basicPanel_to_music);
    }
}
