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
import delta.out386.xosp.JenkinsJson.builds;
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
import android.widget.Button;
import android.widget.ListView;


public class PendingDownloadsFragment extends Fragment {
    JenkinsJson json;
    View rootView;
    Context context;
    BuildsAdapter adapter;
    BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int i = intent.getIntExtra(Constants.DOWNLOADS_PROGRESS_VALUE, 0);
            String id = intent.getStringExtra(Constants.DOWNLOADS_PROGRESS_ID);
            progress(i, id);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        json = (JenkinsJson) getArguments().getSerializable("json");
        rootView = inflater.inflate(R.layout.fragment_builds, container, false);
        context = getContext();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new BuildsAdapter(context, R.layout.builds_item, json.builds);
        ListView lv = (ListView) rootView.findViewById(R.id.build_list);
        lv.setAdapter(adapter);
        Button download = (Button) rootView.findViewById(R.id.download);

        IntentFilter progressFilter = new IntentFilter();
        progressFilter.addAction(Constants.DOWNLOADS_PROGRESS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(progressReceiver, progressFilter);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.buildsRefresh);
        if(refreshLayout != null)
        {
            refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new DownloadUpdateJson(context, rootView).execute();
                }
            });
        }
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent download = new Intent(Constants.DOWNLOADS_INTENT);
                download.putExtra(Constants.DOWNLOADS_JSON, json);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(download);
            }
        });
    }
    public void progress(int progress, String id) {
        for(builds current : json.builds) {
            if(current.id.equals(id)) {
                current.downloadProgress = progress;
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }
}
