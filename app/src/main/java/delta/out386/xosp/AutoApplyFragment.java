package delta.out386.xosp;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class AutoApplyFragment extends Fragment {
    View rootView;
    public static AutoApplyFragment newInstance() {
        return new AutoApplyFragment();
    }

    public AutoApplyFragment() {}
    BroadcastReceiver autoApplyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Flashables base = (Flashables) intent.getSerializableExtra(Constants.AUTO_UPDATE_BASE);
            Flashables delta = (Flashables) intent.getSerializableExtra(Constants.AUTO_UPDATE_DELTA);
            new WriteFlashablesQueue(base, context, true).execute();
            new WriteFlashablesQueue(delta, context, false).execute();
            new ReadFlashablesQueue(context, rootView).execute();
        }
    };
    BroadcastReceiver noRomsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new ReadFlashablesQueue(context, rootView).execute();
        }
    };

    @Override
    public void onResume() {
        IntentFilter autoApplyFilter = new IntentFilter();
        autoApplyFilter.addAction(Constants.AUTO_UPDATE);
        getActivity().registerReceiver(autoApplyReceiver, autoApplyFilter);

        IntentFilter noRomsFilter = new IntentFilter();
        noRomsFilter.addAction(Constants.NO_ROMS);
        getActivity().registerReceiver(noRomsReceiver, noRomsFilter);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        final Context context = getActivity();
        if(Constants.SUPPORTED_ROM_PROP == null) {
            RelativeLayout noAuto = (RelativeLayout)getActivity().findViewById(R.id.queueNoAutoLayout);
            noAuto.setVisibility(View.GONE);
            return rootView;
        }
        Intent autoApplyService = new Intent(context, AutoApplySetupService.class);
        context.startService(autoApplyService);

        return rootView;
    }
    @Override
    public void onPause() {
        getActivity().unregisterReceiver(autoApplyReceiver);
        super.onPause();
    }
}
