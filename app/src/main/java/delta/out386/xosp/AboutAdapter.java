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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class AboutAdapter extends ArrayAdapter<AboutData> {
    private Context context;
    public AboutAdapter(Context context, int resource,List<AboutData> items) {
        super(context,resource,items);
        this.context = context;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null)
            v= LayoutInflater.from(getContext()).inflate(R.layout.about_list_item,null);
        final AboutData p = getItem(position);
        if(p != null) {
            TextView name = (TextView) v.findViewById(R.id.libraryNameText);
            TextView link = (TextView) v.findViewById(R.id.libraryLinkText);
            String license = null;
            if(p.license == 0)
                license = "Apache License, version 2.0";
            else if(p.license == 1)
                license = "GNU General Public License 3";
            else if(p.license == 2)
                license = "MIT License (MIT)";
            else if(p.license == 3)
                license  = "GNU General Public Licence 2";
            else if(p.license == 4)
                license  = "Mozilla Public License, v. 2.0";
            if(license == null)
                return v;
            name.setText(p.name + ", licensed under the " + license);
            link.setText(p.link);
        }
        return v;
    }
}

