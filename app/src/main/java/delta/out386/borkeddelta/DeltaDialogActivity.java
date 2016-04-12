package delta.out386.borkeddelta;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wang.avi.AVLoadingIndicatorView;


public class DeltaDialogActivity extends Activity {

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
            TextView loadingText = (TextView)findViewById(R.id.loadingText);
            String text = intent.getStringExtra(Constants.DIALOG_MESSAGE);
            loadingText.setText(text);
        }
    };
    BroadcastReceiver genericMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView loadingText = (TextView)findViewById(R.id.loadingText);
            AVLoadingIndicatorView loader = (AVLoadingIndicatorView)findViewById(R.id.aviLoader);
            RelativeLayout okButton = (RelativeLayout)findViewById(R.id.ok_button);
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
        //moveTaskToBack(true);
        TextView loadingText = (TextView)findViewById(R.id.loadingText);
        loadingText.setText("Working");

        IntentFilter apply = new IntentFilter();
        apply.addAction(Constants.ACTION_APPLY_DIALOG);
        registerReceiver(applyReciever, apply);

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
