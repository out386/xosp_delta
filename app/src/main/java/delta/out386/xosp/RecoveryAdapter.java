package delta.out386.xosp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

public class RecoveryAdapter extends ArrayAdapter<Flashables> {
    Flashables current;

    public RecoveryAdapter(Context context, int resource, List<Flashables> items) {
        super(context,resource,items);
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null)
            v= LayoutInflater.from(getContext()).inflate(R.layout.flash_item,null);
        current = getItem(position);
        if(current != null) {
            TextView path = (TextView) v.findViewById(R.id.flash_path);
            TextView type = (TextView) v.findViewById(R.id.flash_type);
            path.setText(current.file.getPath());
            type.setText(current.type);
        }
        return v;
    }
}

