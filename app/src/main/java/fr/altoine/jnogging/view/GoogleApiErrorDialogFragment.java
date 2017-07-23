package fr.altoine.jnogging.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.common.GoogleApiAvailability;

import fr.altoine.jnogging.MainActivity;
import fr.altoine.jnogging.utils.Constants;

/**
 * Created by soulierantoine on 21/07/2017.
 */

public class GoogleApiErrorDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the error code and retrieve the appropriate dialog
        int errorCode = this.getArguments().getInt(Constants.Keys.GOOGLE_API_DIALOG_ERROR);

        return GoogleApiAvailability.getInstance().getErrorDialog(
                this.getActivity(), errorCode, Constants.Keys.REQUEST_RESOLVE_ERROR);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ((MainActivity) getActivity()).onDialogDismissed();
    }
}
