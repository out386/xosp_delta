package delta.out386.borkeddelta;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class DialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setMessage("Delta applied successfully.");
        dialog.show();
    }
}
