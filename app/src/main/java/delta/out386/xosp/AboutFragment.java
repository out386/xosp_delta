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

package delta.out386.xosp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment {
    View rootView;
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ListView lv=(ListView) rootView.findViewById(R.id.aboutListView);
        List<AboutData> aboutList = new ArrayList<>();

        aboutList.add(new AboutData("Android-MaterialRefreshLayout", 2, "https://github.com/android-cjj/Android-MaterialRefreshLayout"));
        aboutList.add(new AboutData("Android-SlideExpandableListView", 0, "https://github.com/tjerkw/Android-SlideExpandableListView"));
        aboutList.add(new AboutData("Apache Commons IO", 0, "http://commons.apache.org/io"));
        aboutList.add(new AboutData("AppIntro", 0, "https://github.com/PaoloRotolo/AppIntro"));
        aboutList.add(new AboutData("AVLoadingIndicatorView", 0, "https://github.com/81813780/AVLoadingIndicatorView"));
        aboutList.add(new AboutData("libopendelta", 1, "https://github.com/omnirom/android_packages_apps_OpenDelta/blob/android-6.0/jni/Android.mk"));
        aboutList.add(new AboutData("libsuperuser", 0, "https://github.com/Chainfire/libsuperuser"));
        aboutList.add(new AboutData("moshi", 0, "https://github.com/square/moshi"));
        aboutList.add(new AboutData("NineOldAndroids", 0, "https://github.com/JakeWharton/NineOldAndroids"));
        aboutList.add(new AboutData("NumberProgressBar", 2, "https://github.com/daimajia/NumberProgressBar"));
        aboutList.add(new AboutData("xdelta3", 3, "https://github.com/jmacd/xdelta/tree/release3_0"));
        AboutAdapter adapter = new AboutAdapter(getContext(),
                R.layout.about_list_item, aboutList);
        lv.setAdapter(adapter);

        return rootView;
    }
}
