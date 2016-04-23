package delta.out386.xosp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QueueFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    static int section = 1;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static QueueFragment newInstance(int sectionNumber) {
        section = sectionNumber;
        QueueFragment fragment = new QueueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public QueueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        final Context cont=getActivity();

        new ReadFlashablesQueue(cont, rootView).execute();
        return rootView;
    }
}
