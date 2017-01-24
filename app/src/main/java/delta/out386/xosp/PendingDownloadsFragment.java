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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;

import br.com.bemobi.medescope.Medescope;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;


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
        final ImageView download = (ImageView) rootView.findViewById(R.id.download);
        final ImageView cancel = (ImageView) rootView.findViewById(R.id.cancel);

        IntentFilter progressFilter = new IntentFilter();
        progressFilter.addAction(Constants.DOWNLOADS_PROGRESS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(progressReceiver, progressFilter);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.buildsRefresh);
        if (refreshLayout != null) {
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
                download.animate()
                        .translationY(60)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                download.setVisibility(View.GONE);
                                cancel.animate()
                                        .translationY(60)
                                        .setDuration(0)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                cancel.setVisibility(View.VISIBLE);
                                                cancel.animate()
                                                        .translationY(0)
                                                        .setDuration(500);
                                            }
                                        });

                            }
                        });
                Intent download = new Intent(Constants.DOWNLOADS_INTENT);
                download.putExtra(Constants.DOWNLOADS_JSON, json);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(download);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (builds current : json.builds) {
                    Medescope.getInstance(context).cancel(current.id);
                    Log.i(Constants.TAG, "CANCEL DOWNLOAD  " + current.id);
                }

                cancel.animate()
                        .translationY(60)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                cancel.setVisibility(View.GONE);
                                download.animate()
                                        .translationY(60)
                                        .setDuration(0)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                download.setVisibility(View.VISIBLE);
                                                download.animate()
                                                        .translationY(0)
                                                        .setDuration(500);
                                            }
                                        });

                            }
                        });
            }
        });
    }

    public void progress(int progress, String id) {
        for (builds current : json.builds) {
            if (current.id.equals(id)) {
                current.downloadProgress = progress;
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }
}
