package delta.out386.borkeddelta;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.wang.avi.AVLoadingIndicatorView;

public class DeltaDialogActivity extends Activity {

    TextView loadingText;
    final String TAG = Constants.TAG;
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
        }
    };
    BroadcastReceiver genericMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AVLoadingIndicatorView loader = (AVLoadingIndicatorView)findViewById(R.id.aviLoader);
            RelativeLayout okButton = (RelativeLayout)findViewById(R.id.ok_button);
            progressbar.setVisibility(View.GONE);
            String text = intent.getStringExtra(Constants.GENERIC_DIALOG_MESSAGE);
            loader.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            allowBack = true;
            loadingText.setText(text);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delta_apply_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loadingText = (TextView)findViewById(R.id.loadingText);
        loadingText.setText("Working");
        progressbar = (NumberProgressBar)findViewById(R.id.progressbar);


        IntentFilter apply = new IntentFilter();
        apply.addAction(Constants.ACTION_APPLY_DIALOG);
        registerReceiver(applyReciever, apply);

        IntentFilter progress = new IntentFilter();
        progress.addAction(Constants.PROGRESS_DIALOG);
        registerReceiver(progressReciever, progress);

        IntentFilter close = new IntentFilter();
        close.addAction(Constants.ACTION_CLOSE_DIALOG);
        registerReceiver(closeReciever, close);

        IntentFilter genericMessage = new IntentFilter();
        genericMessage.addAction(Constants.GENERIC_DIALOG);
        registerReceiver(genericMessageReciever, genericMessage);
    }
    public void finish() {
        super.finish();
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(closeReciever);
        unregisterReceiver(applyReciever);
        unregisterReceiver(genericMessageReciever);
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        if(allowBack)
            super.onBackPressed();
    }
}
