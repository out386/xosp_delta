package delta.out386.xosp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.List;

import delta.out386.xosp.BasketbuildJson.file;

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

public class BuildsAdapterBasketbuild extends ArrayAdapter<file> {
    NumberProgressBar progress;
    file current;

    public BuildsAdapterBasketbuild(Context context, int resource, List<file> items) {
        super(context,resource,items);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null)
            v= LayoutInflater.from(getContext()).inflate(R.layout.builds_item,null);
        current = getItem(position);
        if(current != null) {
            TextView name = (TextView) v.findViewById(R.id.build_name);
            TextView date = (TextView) v.findViewById(R.id.build_date);
            TextView size = (TextView) v.findViewById(R.id.build_size);
            TextView status = (TextView) v.findViewById(R.id.download_status);
            progress = (NumberProgressBar) v.findViewById(R.id.download_progress);
            if (current.isDelta)
                name.setText("XOSP delta for " + Constants.ROM_ZIP_DEVICE_NAME);
            else
                name.setText("XOSP full ROM");
            date.setText("Build date : " + current.stringDate);
            if(current.filesize != null)
                size.setText(current.filesize);



            if(current.downloadProgress == -2)
                return v;
            progress.setVisibility(View.VISIBLE);
            if (current.downloadProgress == -1) {
                progress.setProgress(50);
                status.setVisibility(View.VISIBLE);
                status.setText("Failed");
                progress.setReachedBarColor(0xFFFF0000);
            }
            else if (current.downloadProgress == Integer.MAX_VALUE) {
                status.setVisibility(View.VISIBLE);
                status.setText("Paused");
            }
            else if (current.downloadProgress == 100) {
                progress.setReachedBarColor(0xFF00FF00);
                status.setVisibility(View.VISIBLE);
                status.setText("Complete");
            }
            else {
                progress.setProgress(current.downloadProgress);
                status.setVisibility(View.VISIBLE);
                status.setText("Downloading");
            }
        }
        return v;
    }
}

