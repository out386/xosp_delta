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
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class PendingDownloadsFragment extends Fragment {
    JenkinsJson json;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        json = (JenkinsJson) getArguments().getSerializable("json");
        rootView = inflater.inflate(R.layout.fragment_builds, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BuildsAdapter adapter = new BuildsAdapter(getContext(), R.layout.builds_item, json.builds);
        ListView lv = (ListView) rootView.findViewById(R.id.build_list);
        lv.setAdapter(adapter);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.buildsRefresh);
        if(refreshLayout != null)
        {
            refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new DownloadUpdateJson(getContext(), rootView).execute();
                }
            });
        }
    }
}
