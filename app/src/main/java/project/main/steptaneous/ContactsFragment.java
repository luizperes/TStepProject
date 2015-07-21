/*
 * This is the source code of Stepss for Android v. 1.0.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Luiz Peres, 2015.
 */

package project.main.steptaneous;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.telegram.android.ContactsController;
import org.telegram.android.NotificationCenter;
import org.telegram.messenger.TLRPC;

import java.util.HashMap;

import project.main.steptaneous.base.BaseFragmentStep;

public class ContactsFragment extends BaseFragmentStep implements NotificationCenter.NotificationCenterDelegate
{
    private boolean onlyUsers;
    private boolean needPhonebook;
    private boolean destroyAfterSelect;
    private boolean returnAsResult;
    private boolean createSecretChat;
    private boolean creatingChat = false;
    private String selectAlertString = null;
    private HashMap<Integer, TLRPC.User> ignoreUsers;
    private boolean allowUsernameSearch = true;
    private ContactsActivityDelegate delegate;

    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC.User user, String param);
    }

    @SuppressLint("ValidFragment")
    public ContactsFragment(Bundle args)
    {
        super(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVars();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_container, container, false);
        FrameLayout frame = (FrameLayout)rootView.findViewById(R.id.frame_container);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        delegate = null;
    }

    private void initVars()
    {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);

        Bundle arguments = getArguments();

        if (arguments != null) {
            onlyUsers = arguments.getBoolean("onlyUsers", false);
            destroyAfterSelect = arguments.getBoolean("destroyAfterSelect", false);
            returnAsResult = arguments.getBoolean("returnAsResult", false);
            createSecretChat = arguments.getBoolean("createSecretChat", false);
            selectAlertString = arguments.getString("selectAlertString");
            allowUsernameSearch = arguments.getBoolean("allowUsernameSearch", true);
        } else {
            needPhonebook = true;
        }

        ContactsController.getInstance().checkInviteText();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }
}
