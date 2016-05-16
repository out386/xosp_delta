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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.wang.avi.AVLoadingIndicatorView;

public class DeltaDialogActivity extends Activity {

    TextView loadingText;
    NumberProgressBar progressbar;

    boolean allowBack = false;
    BroadcastReceiver closeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                finish();
        }
    };
    BroadcastReceiver applyReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra(Constants.DIALOG_MESSAGE);
            progressbar.setVisibility(View.GONE);
            loadingText.setText(text);
        }
    };
    BroadcastReceiver progressReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(Constants.PROGRESS, 0);
            progressbar.setVisibility(View.VISIBLE);
            progressbar.setProgress(progress);
            loadingText.setText("Applying the delta");
            // Setting text every two seconds is a dirty fix
        }
    };
    BroadcastReceiver genericMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AVLoadingIndicatorView loader = (AVLoadingIndicatorView)findViewById(R.id.aviLoader);
            RelativeLayout okButton = (RelativeLayout)findViewById(R.id.ok_button);
            String text = intent.getStringExtra(Constants.GENERIC_DIALOG_MESSAGE);
            loader.setVisibility(View.GONE);
            progressbar.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
            allowBack = true;
            final Intent intent2 = intent;
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeStickyBroadcast(intent2);
                    onBackPressed();
                }
            });
            loadingText.setText(text);
        }
    };
    

    @Override
    protected void onResume() {
        IntentFilter apply = new IntentFilter();
        apply.addAction(Constants.ACTION_APPLY_DIALOG);
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(applyReciever, apply);

        IntentFilter progress = new IntentFilter();
        progress.addAction(Constants.PROGRESS_DIALOG);
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(progressReciever, progress);

        IntentFilter close = new IntentFilter();
        close.addAction(Constants.ACTION_CLOSE_DIALOG);
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(closeReciever, close);

        IntentFilter genericMessage = new IntentFilter();
        genericMessage.addAction(Constants.GENERIC_DIALOG);
        registerReceiver(genericMessageReciever, genericMessage);

        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delta_apply_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loadingText = (TextView)findViewById(R.id.loadingText);
        loadingText.setText("Working");
        progressbar = (NumberProgressBar)findViewById(R.id.progressbar);
        intentCheck();
    }
    public void finish() {

        super.finish();
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(closeReciever);
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(applyReciever);
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(progressReciever);
        unregisterReceiver(genericMessageReciever);
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        if(allowBack)
            super.onBackPressed();
    }

    private void intentCheck() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action == null)
            return;
        if(action.equals(Constants.GENERIC_DIALOG))
            genericDialog(intent);
        else if(action.equals(Constants.ACTION_NOT_XOSP_DIALOG))
            notRomDialog();
        else if(action.equals(Constants.ACTION_APPLY_DIALOG))
            applyDialog(intent);
    }
    private void genericDialog(Intent intent) {
        AVLoadingIndicatorView loader = (AVLoadingIndicatorView)findViewById(R.id.aviLoader);
        RelativeLayout okButton = (RelativeLayout)findViewById(R.id.ok_button);
        String text = intent.getStringExtra(Constants.GENERIC_DIALOG_MESSAGE);
        loader.setVisibility(View.GONE);
        okButton.setVisibility(View.VISIBLE);
        allowBack = true;
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadingText.setText(text);
    }
    private void notRomDialog() {
        AVLoadingIndicatorView loader = (AVLoadingIndicatorView)findViewById(R.id.aviLoader);
        RelativeLayout okButton = (RelativeLayout)findViewById(R.id.ok_button);
        String text = "Sorry. This app only works on " + Constants.SUPPORTED_ROM_FULL_NAME;
        loader.setVisibility(View.GONE);
        okButton.setVisibility(View.VISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadingText.setText(text);
    }
    private void applyDialog(Intent intent) {
        Log.v("XOSPDelta", "RECEIVED DELTA");
        String text = intent.getStringExtra(Constants.DIALOG_MESSAGE);
        loadingText.setText(text);
    }
}
