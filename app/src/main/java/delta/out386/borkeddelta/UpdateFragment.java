package delta.out386.borkeddelta;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by J-PC on 4/7/2016.
 */
public class UpdateFragment extends Fragment {
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
    public static UpdateFragment newInstance(int sectionNumber) {
        section = sectionNumber;
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_update, container, false);
        final Context cont = getActivity();
        new DownloadUpdateJson(cont, rootView).execute();
        return rootView;
    }
}