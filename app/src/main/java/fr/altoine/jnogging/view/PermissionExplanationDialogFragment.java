package fr.altoine.jnogging.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by soulierantoine on 03/07/2017.
 */

public class PermissionExplanationDialogFragment extends DialogFragment {

    private final static String TAG = PermissionExplanationDialogFragment.class.getSimpleName();

    public interface PermissionExplanationDialogFragmentListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    PermissionExplanationDialogFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("title")
                .setMessage("foo")
                .setPositiveButton("foo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(PermissionExplanationDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // Instantiate the PermissionExplanationDialogFragmentListener so we can send events to the host
            mListener = (PermissionExplanationDialogFragmentListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, context.toString() + " must implement PermissionExplanationDialogFragmentListener");
            e.printStackTrace();
        }
    }
}
