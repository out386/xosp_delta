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
import android.widget.ImageButton;
import android.widget.ListView;



public class PendingDownloadsFragment extends Fragment {
    JenkinsJson jsonJenkins;
    BasketbuildJson jsonBB;
    View rootView;
    Context context;
    BuildsAdapterJenkins jenkinsAdapter;
    BuildsAdapterBasketbuild basketbuildAdapter;
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
        if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_BASKETBUILD))
            jsonBB = (BasketbuildJson) getArguments().getSerializable("json");
        else if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_JENKINS))
            jsonJenkins = (JenkinsJson) getArguments().getSerializable("json");

        rootView = inflater.inflate(R.layout.fragment_builds, container, false);
        context = getContext();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ListView lv = (ListView) rootView.findViewById(R.id.build_list);

        if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_BASKETBUILD)) {
            basketbuildAdapter = new BuildsAdapterBasketbuild(context, R.layout.builds_item, jsonBB.files);
            lv.setAdapter(basketbuildAdapter);
        }
        else if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_JENKINS)) {
            jenkinsAdapter = new BuildsAdapterJenkins(context, R.layout.builds_item, jsonJenkins.builds);
            lv.setAdapter(jenkinsAdapter);
        }

        final ImageButton download = (ImageButton) rootView.findViewById(R.id.download);
        final ImageButton cancel = (ImageButton) rootView.findViewById(R.id.cancel);

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
                if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_BASKETBUILD))
                    download.putExtra(Constants.DOWNLOADS_JSON, jsonBB);
                else if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_JENKINS))
                    download.putExtra(Constants.DOWNLOADS_JSON, jsonJenkins);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(download);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_BASKETBUILD)) {
                    for (BasketbuildJson.file current : jsonBB.files) {
                        Medescope.getInstance(context).cancel(current.filemd5);
                        Log.i(Constants.TAG, "CANCEL DOWNLOAD  " + current.filemd5);
                    }
                }
                else if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_JENKINS)) {
                    for (JenkinsJson.builds current : jsonJenkins.builds) {
                        Medescope.getInstance(context).cancel(current.id);
                        Log.i(Constants.TAG, "CANCEL DOWNLOAD  " + current.id);
                    }
                }
            }
        });
    }
    public void progress(int progress, String id) {
        if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_BASKETBUILD)) {
            for (BasketbuildJson.file current : jsonBB.files) {
                if (current.filemd5.equals(id)) {
                    current.downloadProgress = progress;
                    break;
                }
            }
            basketbuildAdapter.notifyDataSetChanged();
        }
        else if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_JENKINS)) {
            for (JenkinsJson.builds current : jsonJenkins.builds) {
                if (current.id.equals(id)) {
                    current.downloadProgress = progress;
                    break;
                }
            }
            jenkinsAdapter.notifyDataSetChanged();
        }
    }
}
