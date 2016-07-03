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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class AutoApplyFragment extends Fragment {
    View rootView;
    public static AutoApplyFragment newInstance() {
        return new AutoApplyFragment();
    }

    public AutoApplyFragment() {}
    BroadcastReceiver autoApplyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Flashables base = (Flashables) intent.getSerializableExtra(Constants.AUTO_UPDATE_BASE);
            Flashables delta = (Flashables) intent.getSerializableExtra(Constants.AUTO_UPDATE_DELTA);
            new WriteFlashablesQueue(base, context, true).execute();
            new WriteFlashablesQueue(delta, context, false).execute();
            new ReadFlashablesQueue(context, rootView).execute();
        }
    };
    BroadcastReceiver noRomsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new ReadFlashablesQueue(context, rootView).execute();
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
        Intent autoApplyService = new Intent(context, AutoApplySetupService.class);
        context.startService(autoApplyService);

        MaterialRefreshLayout emptyRefresh = (MaterialRefreshLayout) rootView.findViewById(R.id.emptyRefresh);
        new DownloadUpdateJson(context, rootView).execute();
        emptyRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                new DownloadUpdateJson(context, rootView).execute();
            }
        });
            return rootView;
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(autoApplyReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(noRomsReceiver);
        super.onPause();
    }
}
