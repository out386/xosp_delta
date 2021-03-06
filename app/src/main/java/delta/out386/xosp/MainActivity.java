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

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import java.util.List;
import android.support.v4.widget.DrawerLayout;
import eu.chainfire.libsuperuser.Shell;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.grarak.kerneladiutor.fragments.RecoveryFragment;
import com.mikepenz.iconics.context.IconicsContextWrapper;

public class MainActivity extends Activity
{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String [] mDrawerItems;

    final int WRITE_STORAGE_PERMISSION = 1;
    final int READ_STORAGE_PERMISSION = 1;

    BroadcastReceiver applyReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startActivity(new Intent(getApplicationContext(), DeltaDialogActivity.class)
                    .setAction(Constants.ACTION_APPLY_DIALOG)
                    .putExtra(Constants.DIALOG_MESSAGE, "Extracting the delta"));
        }
    };
    BroadcastReceiver genericMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(Constants.GENERIC_DIALOG_MESSAGE);
            startActivity(new Intent(getApplicationContext(), DeltaDialogActivity.class)
                    .setAction(Constants.GENERIC_DIALOG)
                    .putExtra(Constants.GENERIC_DIALOG_MESSAGE, message));
        }
    };

    BroadcastReceiver genericToastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(Constants.GENERIC_TOAST_MESSAGE);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    BroadcastReceiver activateDownloadsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            JenkinsJson json = (JenkinsJson) intent.getSerializableExtra(Constants.DOWNLOADS_JSON);
            download(json.builds);

        }
    };

    BroadcastReceiver pendingDownloadsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FragmentManager fragmentManager = getFragmentManager();
            PendingDownloadsFragment frag = new PendingDownloadsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("json", intent.getSerializableExtra(Constants.PENDING_DOWNLOADS));
            frag.setArguments(bundle);
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, frag)
                        .commit();
                getActionBar().setTitle(R.string.downloads);
            } catch (IllegalStateException e) {}
        }
    };

    BroadcastReceiver downloadsDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            removeStickyBroadcast(intent);
            FragmentManager fragmentManager = getFragmentManager();
            AutoApplyFragment frag = AutoApplyFragment.newInstance();
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, frag)
                        .commit();
                getActionBar().setTitle(R.string.drawer_item1);
            } catch (IllegalStateException e) {}
        }
    };

    @Override
    protected void onResume() {
        IntentFilter apply = new IntentFilter();
        apply.addAction(Constants.ACTION_APPLY_DIALOG_FIRST_START);
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(applyReciever, apply);

        IntentFilter genericMessage = new IntentFilter();
        genericMessage.addAction(Constants.GENERIC_DIALOG_FIRST_START);
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(genericMessageReciever, genericMessage);

        IntentFilter genericToast = new IntentFilter();
        genericToast.addAction(Constants.GENERIC_TOAST);
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(genericToastReciever, genericToast);

        IntentFilter pendingDownloads = new IntentFilter();
        pendingDownloads.addAction(Constants.PENDING_DOWNLOADS_INTENT);
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(pendingDownloadsReceiver, pendingDownloads);

        IntentFilter activateDownloads = new IntentFilter();
        activateDownloads.addAction(Constants.DOWNLOADS_INTENT);
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(activateDownloadsReceiver, activateDownloads);

        IntentFilter downloadsDone = new IntentFilter();
        downloadsDone.addAction(Constants.DOWNLOADS_DONE_INTENT);
        registerReceiver(downloadsDoneReceiver, downloadsDone);

        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        setActionBar(toolbar);
        mDrawerItems = getResources().getStringArray(R.array.drawer_items);
        setTitle(mDrawerItems[0]);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.addHeaderView(getLayoutInflater().inflate(R.layout.drawer_header, mDrawerList, false), null, false);
        mDrawerList.setAdapter(new DrawerAdapter(this,
                R.layout.drawer_list_item, mDrawerItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Set up the look of the statusbar
        int sbHeight = getStatusBarHeight();
        toolbar.setPadding(0, sbHeight, 0, 0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if(Constants.SUPPORTED_ROM_PROP != null) {
            List<String> romVersion = Shell.SH.run("getprop " + Constants.SUPPORTED_ROM_PROP);
            if (romVersion == null || romVersion.size() == 0 || !romVersion.get(0).contains(Constants.SUPPORTED_ROM_PROP_NAME)) {
                // Unsupported ROM. Let's quit.
                startActivity(new Intent(getApplication(), DeltaDialogActivity.class).setAction(Constants.ACTION_NOT_XOSP_DIALOG));
                finish();
            }
        }
        if(! isOfficial()) {
            // Wrong device.
            Toast.makeText(MainActivity.this, "XOSPDelta is only for the official XOSP devices.", Toast.LENGTH_SHORT).show();
            finish();
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION);
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, AutoApplyFragment.newInstance())
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] result) {
        if(result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED)
        {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, AutoApplyFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment=new Fragment();
        switch (position)
        {
            case 1: fragment = AutoApplyFragment.newInstance();
                break;
            case 2: fragment = AboutFragment.newInstance();
                break;
            case 3: fragment = new RecoveryFragment();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerItems[position -1]);
        mDrawerList.setItemsCanFocus(true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }
    private class DrawerAdapter extends ArrayAdapter<String> {
        private String [] items;
        public DrawerAdapter(Context context, int resource, String [] items) {
            super(context, resource, items);
            this.items = items;
        }
        @Override
        public View getView(int position, final View convertView, ViewGroup parent) {
            View v = convertView;
            if(v == null)
                v= LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item,null);
            final String p = getItem(position);
            if(p != null) {
                TextView text = (TextView) v.findViewById(R.id.drawerItemText);
                ImageView icon = (ImageView) v.findViewById(R.id.drawerItemIcon);
                text.setText(items[position]);
                /*switch (position) {
                    case 1: icon.setImageResource(R.drawable.abc_ic_menu_share_material);
                }*/
            }
            return v;
        }
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(applyReciever);
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(genericMessageReciever);
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(genericToastReciever);
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(activateDownloadsReceiver);
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(activateDownloadsReceiver);
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(pendingDownloadsReceiver);
        unregisterReceiver(downloadsDoneReceiver);
        super.onPause();
    }

    public boolean isOfficial() {
        for(String deviceName:Constants.OFFICIAL_LIST)
            if(deviceName.equals(Constants.ROM_ZIP_DEVICE_NAME))
                return true;
        return false;
    }
    public void download(List<JenkinsJson.builds> json) {
        new DownloadBuilds(json, this).download(0);
    }
    public int getStatusBarHeight()
    {
        int height = 0;
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(id > 0)
            height = getResources().getDimensionPixelSize(id);
        return height;
    }
}
