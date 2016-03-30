package delta.out386.borkeddelta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by J-PC on 3/21/2016.
 */
public class FlashablesAdapter extends ArrayAdapter<Flashables> {
    private LayoutInflater inflater ;
    private Context context;
    private Flashables listItem;
    private List<Flashables> list;
    public FlashablesAdapter(Context context, int textView) {
        super(context, textView);
    }
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
            ImageButton select = (ImageButton) v.findViewById(R.id.selectFileButton);
            select.setOnClickListener(new View.OnClickListener(){
                                            @Override
                                            public void onClick(View v) {
                                                new writeFlashables(p, context).execute();
                                            }
                                      }

            );
            final TextView size = (TextView) v.findViewById(R.id.expandableTextView);
            if(name != null)
                name.setText(p.file.getName());
            if(type != null)
                type.setText(p.type);
            /*ImageButton expandableButton = (ImageButton) v.findViewById(R.id.expandableButton);
            expandableButton.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {*/
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
                        /*}
                    }
            );*/
        }
        return v;
    }
}
