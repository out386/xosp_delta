package delta.out386.borkeddelta;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class DeltaDialogActivity extends Activity {


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_delta_apply_dialog);
        TextView loadingText = (TextView)findViewById(R.id.loadingText);
        loadingText.setText("Checking MD5s");

        IntentFilter apply = new IntentFilter();
        apply.addAction(Constants.ACTION_APPLY_DIALOG);
        registerReceiver(applyReciever, apply);

        IntentFilter close = new IntentFilter();
        close.addAction(Constants.ACTION_CLOSE_DIALOG);
        registerReceiver(closeReciever, close);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delta_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void finish() {

        super.finish();
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(closeReciever);
        unregisterReceiver(applyReciever);
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
    }
}
