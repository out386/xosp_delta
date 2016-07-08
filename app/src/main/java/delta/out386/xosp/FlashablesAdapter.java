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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

public class FlashablesAdapter extends ArrayAdapter<Flashables> {
    private Context context;
    public FlashablesAdapter(Context context, int resource,List<Flashables> items) {
        super(context,resource,items);
        this.context = context;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null)
            v=LayoutInflater.from(getContext()).inflate(R.layout.list_item,null);
        final Flashables p = getItem(position);
        if(p != null) {
            TextView name = (TextView) v.findViewById(R.id.romNameText);
            TextView type = (TextView) v.findViewById(R.id.romTypeText);
            RelativeLayout select = (RelativeLayout) v.findViewById(R.id.selectFileButton);
            select.setOnClickListener(new View.OnClickListener(){
                                            @Override
                                            public void onClick(View v) {
                                                new WriteFlashablesQueue(p, context, false).execute();
                                                Toast.makeText(context, "Zip selected", Toast.LENGTH_SHORT).show();
                                            }
                                      }

            );
            final TextView size = (TextView) v.findViewById(R.id.expandableTextView);
            if(name != null)
                name.setText(p.file.getName());
            if(type != null)
                type.setText(p.type);
            size.setText(Tools.sizeFormat(p.size));

        }
        return v;
    }
}
