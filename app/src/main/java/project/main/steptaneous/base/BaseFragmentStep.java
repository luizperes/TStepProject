/*
 * This is the source code of Stepss for Android v. 1.0.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Luiz Peres, 2015.
 */
package project.main.steptaneous.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.telegram.messenger.ConnectionsManager;
import org.telegram.messenger.FileLog;

public class BaseFragmentStep extends Fragment
{
    protected int classGuid = 0;
    protected AlertDialog visibleDialog = null;

    public BaseFragmentStep()
    {
        this(null);
    }

    @SuppressLint("ValidFragment")
    public BaseFragmentStep(Bundle args) {
        super();
        super.setArguments(args);
        this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (visibleDialog != null && visibleDialog.isShowing()) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e("stepmessages", e);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ConnectionsManager.getInstance().cancelRpcsForClassGuid(classGuid);
    }

    public void onBeginSlide() {
        try {
            if (visibleDialog != null && visibleDialog.isShowing()) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e("stepmessages", e);
        }
    }

    public AlertDialog showAlertDialog(AlertDialog.Builder builder) {
        try {
            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e("stepmessages", e);
        }
        try {
            visibleDialog = builder.show();
            visibleDialog.setCanceledOnTouchOutside(true);
            visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    visibleDialog = null;
                    onDialogDismiss();
                }
            });
            return visibleDialog;
        } catch (Exception e) {
            FileLog.e("stepmessages", e);
        }
        return null;
    }

    protected void onDialogDismiss() {

    }
}
