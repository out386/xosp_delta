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
    static int section = 1;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BaseFragment newInstance(int sectionNumber) {
        section = sectionNumber;
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
        final Context cont=getActivity();
        switch (section) {
            case 1:
            new SearchZips(cont, false, rootView, "roms").execute();
            break;
            case 2:
                new SearchZips(cont, false, rootView, "deltas").execute();
            break;
            case 3:
                new SearchZips(cont, false, rootView, "kernels").execute();
            break;
            case 4:
                new SearchZips(cont, false, rootView, "others").execute();
            break;
        }
        return rootView;
    }
}
