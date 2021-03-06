package delta.out386.xosp;
/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386)
 */
/*
 * This file is part of XOSPDelta.
 *
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
 * along with XOSPDelta. If not, see <http://www.gnu.org/licenses/>.
 */

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class AutoApplyFragment extends Fragment {
    View rootView;
    boolean jsonAvailable = false;

    public static AutoApplyFragment newInstance() {
        return new AutoApplyFragment();
    }

    public AutoApplyFragment() {}

    BroadcastReceiver autoApplyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Flashables base = (Flashables) intent.getSerializableExtra(Constants.AUTO_UPDATE_BASE);
            Flashables delta = (Flashables) intent.getSerializableExtra(Constants.AUTO_UPDATE_DELTA);
            FlashablesTypeList list = new FlashablesTypeList();
            list.addFlashable(base);
            list.addFlashable(delta);
            new ReadFlashablesQueue(context, rootView, list).execute();
        }
    };

    BroadcastReceiver noRomsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(! jsonAvailable) {
                return;
            }
            new ReadFlashablesQueue(context, rootView, null).execute();
        }
    };

    BroadcastReceiver noJsonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            jsonAvailable = intent.getBooleanExtra(Constants.IS_JSON_AVAILABLE, false);
            if(!jsonAvailable) {
                Log.i(Constants.TAG, "No update descriptors available");
                TextView statusText = (TextView) rootView.findViewById(R.id.queueEmptyTextview);
                statusText.setVisibility(View.VISIBLE);
                statusText.setText(R.string.no_internet);
            }
            Intent autoApplyService = new Intent(context, AutoApplySetupService.class);
            context.startService(autoApplyService);
        }
    };

    @Override
    public void onResume() {
        IntentFilter autoApplyFilter = new IntentFilter();
        autoApplyFilter.addAction(Constants.AUTO_UPDATE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(autoApplyReceiver, autoApplyFilter);

        IntentFilter noRomsFilter = new IntentFilter();
        noRomsFilter.addAction(Constants.NO_ROMS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(noRomsReceiver, noRomsFilter);

        IntentFilter noJson = new IntentFilter();
        noJson.addAction(Constants.JSON_AVAILABILITY);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(noJsonReceiver, noJson);

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        final Context context = getActivity();
        if(Constants.SUPPORTED_ROM_PROP != null) {
            List<String> romVersion = Shell.SH.run("getprop " + Constants.SUPPORTED_ROM_PROP);
            if (romVersion == null || romVersion.size() == 0 || !romVersion.get(0).contains(Constants.SUPPORTED_ROM_PROP_NAME)) {
                // Unsupported ROM. Let's quit.
                return rootView;
            }
        }
        if(Constants.SUPPORTED_ROM_PROP == null) {
            RelativeLayout noAuto = (RelativeLayout)getActivity().findViewById(R.id.queueNoAutoLayout);
            noAuto.setVisibility(View.GONE);
            return rootView;
        }

        final SwipeRefreshLayout emptyRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.emptyRefresh);
        if(emptyRefresh != null)
        {
            emptyRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
            emptyRefresh.post(new Runnable() {
                @Override
                public void run() {
                    emptyRefresh.setRefreshing(true);
                }
            });
            new DownloadUpdateJson(context, rootView).execute();
            emptyRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new DownloadUpdateJson(context, rootView).execute();
                }
            });
        }
            return rootView;
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(autoApplyReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(noRomsReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(noJsonReceiver);
        super.onPause();
    }
}
