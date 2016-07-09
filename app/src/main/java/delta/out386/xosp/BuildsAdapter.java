package delta.out386.xosp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

import delta.out386.xosp.JenkinsJson.builds;

public class BuildsAdapter extends ArrayAdapter<builds> {
    public BuildsAdapter(Context context, int resource, List<builds> items) {
        super(context,resource,items);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null)
            v= LayoutInflater.from(getContext()).inflate(R.layout.builds_item,null);
        final builds p = getItem(position);
        if(p != null) {
            TextView name = (TextView) v.findViewById(R.id.build_name);
            TextView date = (TextView) v.findViewById(R.id.build_date);
            if(name != null)
                name.setText(p.artifacts[0].fileName);
            if(date != null)
                date.setText(p.stringDate);
        }
        return v;
    }
}

