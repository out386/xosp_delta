package delta.out386.borkeddelta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by J-PC on 3/21/2016.
 */
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
                                                new WriteFlashablesQueue(p, context).execute();
                                                Toast.makeText(context, "Zip selected", Toast.LENGTH_SHORT).show();
                                            }
                                      }

            );
            final TextView size = (TextView) v.findViewById(R.id.expandableTextView);
            if(name != null)
                name.setText(p.file.getName());
            if(type != null)
                type.setText(p.type);
            double printSize = p.size;
            String unit = " B";
            if (printSize > 1024 && printSize < 1048576) {
                unit = " KiB";
                printSize = printSize / 1024;
            } else if (printSize >= 1048576) {
                unit = " MiB";
                printSize = printSize / 1024 / 1024;
            }
            size.setText(new DecimalFormat("#0.00").format(printSize) + unit);

        }
        return v;
    }
}
