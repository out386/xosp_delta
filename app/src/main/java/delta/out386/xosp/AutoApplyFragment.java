package delta.out386.xosp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class AutoApplyFragment extends Fragment {
    public static AutoApplyFragment newInstance() {
        return new AutoApplyFragment();
    }

    public AutoApplyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        final Context context = getActivity();
        if(Constants.SUPPORTED_ROM_PROP == null) {
            RelativeLayout noAuto = (RelativeLayout)getActivity().findViewById(R.id.queueNoAutoLayout);
            noAuto.setVisibility(View.GONE);
            return rootView;
        }
        Intent autoApplyIntent = new Intent(context, AutoApplySetupService.class);
        context.startService(autoApplyIntent);
        return rootView;
    }
}
