package delta.out386.borkeddelta;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;


public class DialogActivity extends Activity {

    BroadcastReceiver messageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView loadingText = (TextView)findViewById(R.id.messageText);
            String text = intent.getStringExtra(Constants.GENERIC_DIALOG_MESSAGE);
            loadingText.setText(text);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_message_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        /*AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage("Delta applied successfully.");
        dialog.show();*/
        IntentFilter message = new IntentFilter();
        message.addAction(Constants.GENERIC_DIALOG);
        registerReceiver(messageReciever, message);
    }
}
