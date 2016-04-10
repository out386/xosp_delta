package delta.out386.borkeddelta;

import android.app.DialogFragment;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class LoadingDialogFragment extends DialogFragment {

    int xml;
    public LoadingDialogFragment(){}
    public LoadingDialogFragment(int xml) {
        this.xml = xml;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.app_name));
        View view = inflater.inflate(xml,
									 container);

        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }
	@Override
	public void onStart() {
		super.onStart();

		Dialog dialog = getDialog();
		if (dialog != null) {
			dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0x88, 0x00, 0x00, 0x00)));
		}
	}
}
