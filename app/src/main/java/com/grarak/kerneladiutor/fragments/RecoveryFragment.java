/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file was originally a part of Kernel Adiutor.
 *
 * Adapted for XOSPDelta by Ritayan Chakraborty (out386)
 * XOSPDelta is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * XOSPDelta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with XOSPDelta.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.graphics.Color;

import android.widget.ListView;
import android.widget.Toast;

import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.utils.tools.Recovery;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import delta.out386.xosp.Constants;
import delta.out386.xosp.R;
import delta.out386.xosp.RecoveryAdapter;
import delta.out386.xosp.SortFileType;
import eu.chainfire.libsuperuser.Shell;

/**
 * Created by willi on 12.07.16.
 */
public class RecoveryFragment extends Fragment {

    private List<Recovery> mCommands = new ArrayList<>();
    private int mRecoveryOption;
    private RecoveryAdapter adapter;
    private String loc;
    final private int FILE_CODE = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_recovery, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.flash_list);
        mRecoveryOption = Prefs.getInt("recovery_option", 0, getActivity());
        Log.i(Constants.TAG, "Recovery : " + mRecoveryOption);

        String[] options = getResources().getStringArray(R.array.recovery_options);

        final List<CheckBox> checkBoxes = new ArrayList<>();
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.flash_checkboxes);
        for (int i = 0; i < options.length; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(options[i]);
            checkBox.setTextColor(Color.WHITE);
            checkBox.setChecked(i == mRecoveryOption);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            final int position = i;
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        checkBoxes.get(i).setChecked(position == i);
                    }
                    Prefs.saveInt("recovery_option", position, getActivity());
                    mRecoveryOption = position;
                }
            });

            checkBoxes.add(checkBox);
            layout.addView(checkBox);
            SharedPreferences preferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
            String defaultDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            loc = preferences.getString("location", defaultDir);
        }

        adapter = new RecoveryAdapter(getContext(), R.layout.flash_item, mCommands);
        listView.setAdapter(adapter);

        rootView.findViewById(R.id.flash_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCommands != null && mCommands.size() > 0) {
                    FlashNow flashNow = new FlashNow(mCommands, mRecoveryOption);
                    flashNow.execute();
                } else {
                    Utils.toast(R.string.add_action_first, getActivity());
                }
            }
        });

        rootView.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        rootView.findViewById(R.id.clear_flash_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommands.clear();
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    private void add() {
        Intent i = new Intent(getContext(), FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, loc);
        startActivityForResult(i, FILE_CODE);
    }

    private void addAction(Recovery.RECOVERY_COMMAND recovery_command, String path) {
        String summary = null;
        switch (recovery_command) {
            case WIPE_DATA:
                summary = getString(R.string.wipe_data);
                break;
            case WIPE_CACHE:
                summary = getString(R.string.wipe_cache);
                break;
            case FLASH_ZIP:
                summary = new File(path).getName();
                break;
        }

        String type = null;
        try {
            if(path != null)
            type = new SortFileType().sort(new File(path));
            if(type != null) {
                if(type.equals("noFlash")) {
                    Utils.toast(path + getResources().getString(R.string.not_flashable_zip), getActivity());
                    return;
                }
                else if(type.equals("delta")) {
                    Utils.toast(getResources().getString(R.string.not_flashable_zip_delta), getActivity(), Toast.LENGTH_LONG);
                    return;
                }
            }
        } catch(IOException e) {
            Utils.toast(path + getResources().getString(R.string.not_flashable_zip), getActivity());
            return;
        }
        final Recovery recovery = new Recovery(recovery_command, path == null ? null : new File(path), summary, type);
        mCommands.add(recovery);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            ClipData clip = data.getClipData();
            if (clip != null) {
                for (int i = 0; i < clip.getItemCount(); i++) {
                    Uri uri = clip.getItemAt(i).getUri();
                    addAction(Recovery.RECOVERY_COMMAND.FLASH_ZIP, uri.getPath());
                }
            }
        }
    }
            /*rootView.findViewById(R.id.reboot_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecoveryFragment != null) {
                        mRecoveryFragment.reboot();
                    }
                }
            });
             return rootView;
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCommands.clear();
    }
}
    class FlashNow extends AsyncTask<Void,Void,Void> {
        private List<Recovery> mCommands;
        private int mRecoveryOption;
        FlashNow(List<Recovery> commands, int recoveryOption) {
            mCommands = commands;
            mRecoveryOption = recoveryOption;
        }
        @Override
        public Void doInBackground(Void... p) {
            String file = "/cache/recovery/" + mCommands.get(0).getFile(mRecoveryOption == 1 ?
                    Recovery.RECOVERY.TWRP : Recovery.RECOVERY.CWM);
            RootFile recoveryFile = new RootFile(file);
            recoveryFile.delete();
            for (Recovery commands : mCommands) {
                for (String command : commands.getCommands(mRecoveryOption == 1 ?
                        Recovery.RECOVERY.TWRP :
                        Recovery.RECOVERY.CWM))
                    recoveryFile.write(command, true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void p) {
            Shell.SU.run("reboot recovery");
        }
    }
