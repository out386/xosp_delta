package delta.out386.borkeddelta;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

/**
 * Created by J-PC on 3/18/2016.
 */
public class BaseFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BaseFragment newInstance(int sectionNumber) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_base, container, false);
        //Button button = (Button) rootView.findViewById(R.id.button);
        //TextView textview = (TextView) rootView.findViewById(R.id.textView);
        //textview.setText(Environment.getExternalStorageState());
        final Context cont=getActivity();
        /*button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {*/
                new SearchZips(cont,rootView).execute();//.setText("working");
            /*}
        });*/
        return rootView;
    }

    private class SearchZips extends AsyncTask<Void, Void,List<Flashables> >{

        Context context;
        View rootView;
		LoadingDialogFragment loading = new LoadingDialogFragment();
        public SearchZips(Context cont,View rootView){
            this.rootView = rootView;
            context=cont;
        }

        @Override
        protected void onPreExecute(){
            /*TextView outputTV = (TextView) rootView.findViewById(R.id.textView);
            outputTV.setText("working");*/

			loading.setCancelable(false);
			loading.show(getFragmentManager(),"dialog");
        }
        @Override
        protected List<Flashables> doInBackground(Void... params){
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/UCDownloads");
            Collection zipsCollection = FileUtils.listFiles(directory, new String [] {"zip"}, false);
			return new FilesCatagorize().run(zipsCollection, rootView, context);
           // return zipsCollection;
        }

        @Override
        protected void onPostExecute(List<Flashables> output){
			/*TextView out = (TextView) rootView.findViewById(R.id.textView);
			out.setText(output);*/
            ListView lv=(ListView) rootView.findViewById(R.id.listView);
            FlashablesAdapter adapter = new FlashablesAdapter(context,
                    R.layout.list_item, output);

            // Assign adapter to ListView
            lv.setAdapter(
                    new SlideExpandableListAdapter(
                            adapter,
                            R.id.list_normal_view,
                            R.id.list_expandable_view)
            );

            loading.dismiss();
        }
    }

}
