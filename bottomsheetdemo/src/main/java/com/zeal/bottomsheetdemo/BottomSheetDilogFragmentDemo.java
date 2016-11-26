package com.zeal.bottomsheetdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.View;


/**
 * Created by zeal on 16/11/24.
 */

public class BottomSheetDilogFragmentDemo extends BottomSheetDialogFragment {
    private static final String TAG = "zeal";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View view = View.inflate(getContext(), R.layout.layout_dialog, null);
        dialog.setContentView(view);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //onCancel
        //onDismiss
        Log.e(TAG, "onDismiss: onDismiss");
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.e(TAG, "onCancel: onCancel");
    }



}


