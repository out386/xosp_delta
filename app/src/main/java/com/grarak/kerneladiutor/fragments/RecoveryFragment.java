/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.graphics.Color;

import android.widget.ListView;
import com.github.developerpaul123.filepickerlibrary.enums.MimeType;
import com.github.developerpaul123.filepickerlibrary.enums.Request;
import com.github.developerpaul123.filepickerlibrary.enums.Scope;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.RootFile;
import com.grarak.kerneladiutor.utils.tools.Recovery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import delta.out386.xosp.Constants;
import delta.out386.xosp.R;
import com.github.developerpaul123.filepickerlibrary.FilePickerActivity;
import delta.out386.xosp.RecoveryAdapter;
import delta.out386.xosp.SortFileType;

import static android.app.Activity.RESULT_OK;
import static com.github.developerpaul123.filepickerlibrary.FilePickerActivity.REQUEST_FILE;

/**
 * Created by willi on 12.07.16.
 */
public class RecoveryFragment extends Fragment {

    private List<Recovery> mCommands = new ArrayList<>();
    private int mRecoveryOption;
    private RecoveryAdapter adapter;
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
                    Log.i(Constants.TAG, "onClick: Saving Reco " + position);
                    Prefs.saveInt("recovery_option", position, getActivity());
                    mRecoveryOption = position;
                }
            });

            checkBoxes.add(checkBox);
            layout.addView(checkBox);
        }

        rootView.findViewById(R.id.flash_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCommands != null && mCommands.size() > 0) {
                    flashNow(mRecoveryOption);
                    Utils.toast("Selected reco " + mRecoveryOption, getActivity());
                } else {
                    Utils.toast(R.string.add_action_first, getActivity());
                }
                add();
            }
        });


        adapter = new RecoveryAdapter(getContext(), R.layout.flash_item, mCommands);
        listView.setAdapter(adapter);

        return rootView;
    }

    private void add() {
        /*Intent intent = new Intent(getActivity(), FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.PATH_INTENT, "/sdcard");
        intent.putExtra(FilePickerActivity.EXTENSION_INTENT, ".zip");
        startActivityForResult(intent, 0);*/
        /*FilePickerBuilder.getInstance()
                .setActivityTheme(R.style.AppTheme)
                .pickDocument(RecoveryFragment.this)*/;//getActivity());
        Intent filePicker = new Intent(getContext(), FilePickerActivity.class);
        filePicker.putExtra(FilePickerActivity.SCOPE, Scope.ALL);
        filePicker.putExtra(FilePickerActivity.REQUEST, Request.FILE);
        filePicker.putExtra(FilePickerActivity.INTENT_EXTRA_COLOR_ID, getResources().getColor(R.color.colorPrimary));
        filePicker.putExtra(FilePickerActivity.MIME_TYPE, MimeType.TXT);
        startActivityForResult(filePicker, REQUEST_FILE);
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
        } catch(IOException e) {
            Utils.toast("File " + path + " is not a zip", getActivity());
            return;
        }
        final Recovery recovery = new Recovery(recovery_command, path == null ? null : new File(path), summary, type);
        mCommands.add(recovery);
        adapter.notifyDataSetChanged();
        /*CardView cardView = new CardView(getActivity());
        cardView.setOnMenuListener(new CardView.OnMenuListener() {
            @Override
            public void onMenuReady(final CardView cardView, PopupMenu popupMenu) {
                popupMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, getString(R.string.delete));
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == 0) {
                            mCommands.remove(recovery);
                            removeItem(cardView);
                        }
                        return false;
                    }
                });
            }
        });*/

        /*DescriptionView descriptionView = new DescriptionView();
        if (path != null) {
            descriptionView.setTitle(getString(R.string.flash_zip));
        }
        descriptionView.setSummary(summary);

        cardView.addItem(descriptionView);
        addItem(cardView);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_FILE) && (resultCode == RESULT_OK)) {
            addAction(Recovery.RECOVERY_COMMAND.FLASH_ZIP, data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH));
            Utils.toast(data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH), getActivity());
        }
    }

    private void flashNow(final int recoveryOption) {
        String file = "/cache/recovery/" + mCommands.get(0).getFile(recoveryOption == 1 ?
                Recovery.RECOVERY.TWRP : Recovery.RECOVERY.CWM);
        RootFile recoveryFile = new RootFile(file);
        recoveryFile.delete();
        for (Recovery commands : mCommands) {
            for (String command : commands.getCommands(recoveryOption == 1 ?
                    Recovery.RECOVERY.TWRP :
                    Recovery.RECOVERY.CWM))
                recoveryFile.write(command, true);
        }
        //Shell.SU.run("reboot recovery");
    }


    //public static class OptionsFragment extends BaseFragment {
        /*public static OptionsFragment newInstance(RecoveryFragment recoveryFragment) {
            OptionsFragment fragment = new OptionsFragment();
            fragment.mRecoveryFragment = recoveryFragment;
            return fragment;
        }*/

        /*private RecoveryFragment mRecoveryFragment;
        private int mRecoveryOption;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_recovery_options, container, false);*/
            /*mRecoveryOption = Prefs.getInt("recovery_option", 0, getActivity());

            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);
            String[] options = getResources().getStringArray(R.array.recovery_options);

            final List<AppCompatCheckBox> checkBoxes = new ArrayList<>();
            for (int i = 0; i < options.length; i++) {
                AppCompatCheckBox checkBox = new AppCompatCheckBox(
                        new ContextThemeWrapper(getActivity(),
                                R.style.Base_Widget_AppCompat_CompoundButton_CheckBox_Custom), null, 0);
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
            }

            rootView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mRecoveryFragment != null && mRecoveryFragment.itemsSize() > 0) {
                        mRecoveryFragment.flashNow(mRecoveryOption);
                    } else {
                        Utils.toast(R.string.add_action_first, getActivity());
                    }
                }
            });

            rootView.findViewById(R.id.reboot_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecoveryFragment != null) {
                        mRecoveryFragment.reboot();
                    }
                }
            });*/

           /* return rootView;
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCommands.clear();
    }
}
