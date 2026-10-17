package com.example.boxing_assistant;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boxing_assistant.Adapters.ItemTouchHelperMusic;
import com.example.boxing_assistant.Adapters.RecyclerViewForMusic;
import com.example.boxing_assistant.MusicDataBase.MusicDataBase;
import com.example.boxing_assistant.MusicDataBase.MusicEntity;
import com.example.boxing_assistant.databinding.MusicFragmentBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class Music extends Fragment implements RecyclerViewForMusic.OnNoteListener {
    MusicFragmentBinding binding;
    RecyclerViewForMusic adapter;
    View view;
    FloatingActionButton musicBack;
    FloatingActionButton button;
    MusicDataBase mdb;
    MusicEntity mc;
    List<MusicEntity> list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MusicFragmentBinding.inflate(inflater, container, false);
        mdb = MusicDataBase.getInstance(getContext());
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).navigate(R.id.action_music_to_basicPanel);

            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        view = binding.getRoot();
        button = binding.fab;
        musicBack = binding.musicBack;
        musicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_music_to_basicPanel);
            }
        });
        RecyclerView recyclerView = binding.musicRecyclerview;
        list = mdb.mainDao().getAll();
        adapter = new RecyclerViewForMusic(list, this);
        ItemTouchHelper.Callback kalbek = new ItemTouchHelperMusic(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(kalbek);
        adapter.setmTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getItemCount() >= 100) {
                    Toast.makeText(getContext(), "Maximum number of songs are 100!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/mpeg");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, 1001);

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                if (data != null) {
                    if (data.getClipData() != null) {
                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            Uri uri = data.getClipData().getItemAt(index).getUri();
                            saveSelectedAudio(data, uri);
                        }

                    } else {
                        Uri uri = data.getData();
                        saveSelectedAudio(data, uri);

                    }
                }
            }
        }
    }

    private void saveSelectedAudio(Intent result, Uri uri) {
        try {
            int readPermission = result.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            int persistablePermission = result.getFlags() & Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
            if (readPermission != 0 && persistablePermission != 0) {
                requireContext().getContentResolver().takePersistableUriPermission(uri, readPermission);
            }
            mc = new MusicEntity();
            mc.setMusicurl(uri.toString());
            mc.setMusicname(DocumentFile.fromSingleUri(getContext(), uri).getName());
            mdb.mainDao().insert(mc);
            list.add(mc);
            adapter.notifyDataSetChanged();
        } catch (SecurityException exception) {
            Toast.makeText(getContext(), "The selected audio file is no longer accessible.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getMusicurl().equals
                    (mdb.mainDao().getAll().get(i).getMusicurl())) {
                list.get(i).setId(mdb.mainDao().getAll().get(i).getId());
                mdb.mainDao().updateSong(list.get(i));
                Log.d("PROVERA", mdb.mainDao().getAll().get(i).getMusicname());
            }

            binding = null;


        }
    }

    @Override
    public void onNoteClick(int position) {

    }

    @Override
    public void onButtonClick(int position) {

        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getMusicurl().equals(mdb.mainDao().getAll().get(i).getMusicurl())) {
                list.get(i).setId(mdb.mainDao().getAll().get(i).getId());
                mdb.mainDao().updateSong(list.get(i));

            }
        }

        mdb.mainDao().delete(mdb.mainDao().getAll().get(position));
        list.remove(position);
        adapter.notifyItemRemoved(position);


    }
}
