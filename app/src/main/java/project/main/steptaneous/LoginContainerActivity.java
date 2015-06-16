package project.main.steptaneous;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.telegram.android.AndroidUtilities;
import org.telegram.android.NotificationCenter;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ConnectionsManager;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.PhotoViewer;

import java.util.ArrayList;


public class LoginContainerActivity extends Activity implements ActionBarLayout.ActionBarLayoutDelegate {
    private DrawerLayoutContainer drawerLayoutContainer;
    private ActionBarLayout actionBarLayout;
    private boolean finished;
    private Runnable lockRunnable;
    private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private PasscodeView passcodeView;
    private int currentConnectionState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars(savedInstanceState);
    }

    private void initVars(Bundle savedInstanceState)
    {
        drawerLayoutContainer = new DrawerLayoutContainer(this);
        setContentView(drawerLayoutContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        actionBarLayout = new ActionBarLayout(this);
        drawerLayoutContainer.setParentActionBarLayout(actionBarLayout);
        actionBarLayout.setDrawerLayoutContainer(drawerLayoutContainer);
        actionBarLayout.init(mainFragmentsStack);
        actionBarLayout.setDelegate(this);

        //ApplicationLoader.loadWallpaper(R.mipmap.background_hd);

        passcodeView = new PasscodeView(this);
        drawerLayoutContainer.addView(passcodeView);
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) passcodeView.getLayoutParams();
        layoutParams1.width = FrameLayout.LayoutParams.MATCH_PARENT;
        layoutParams1.height = FrameLayout.LayoutParams.MATCH_PARENT;
        passcodeView.setLayoutParams(layoutParams1);

        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, this);
        currentConnectionState = ConnectionsManager.getInstance().getConnectionState();

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeOtherAppActivities);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdatedConnectionState);
        if (Build.VERSION.SDK_INT < 14) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.screenStateChanged);
        } else {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appSwitchedToForeground);
        }

        if (actionBarLayout.fragmentsStack.isEmpty()) {
            if (!UserConfig.isClientActivated()) {
                actionBarLayout.addFragmentToStack(new LoginActivity());
            }
        }

        handleIntent(getIntent(), false, savedInstanceState != null, false);
        needLayout();
    }

    private boolean handleIntent(Intent intent, boolean isNew, boolean restore, boolean fromPassword) {
        actionBarLayout.showLastFragment();
        return false;
    }

    private void needLayout()
    {
        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) actionBarLayout.getLayoutParams();
        if (relativeLayoutParams == null)
            relativeLayoutParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT);
        else {
            relativeLayoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            relativeLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        }
        actionBarLayout.setLayoutParams(relativeLayoutParams);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    private void onFinish() {
        if (finished) {
            return;
        }
        finished = true;
        if (lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(lockRunnable);
            lockRunnable = null;
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedConnectionState);
        if (Build.VERSION.SDK_INT < 14) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.screenStateChanged);
        } else {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appSwitchedToForeground);
        }
    }

    @Override
    public boolean onPreIme() {
        if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ActionBarLayout layout)
    {
        drawerLayoutContainer.setAllowOpenDrawer(!(fragment instanceof LoginActivity), false);
        return true;
    }

    @Override
    public boolean needAddFragmentToStack(BaseFragment fragment, ActionBarLayout layout)
    {
        drawerLayoutContainer.setAllowOpenDrawer(!(fragment instanceof LoginActivity), false);
        return true;
    }

    @Override
    public boolean needCloseLastFragment(ActionBarLayout layout) {
        if (layout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        }

        return true;
    }

    @Override
    public void onRebuildAllFragments(ActionBarLayout layout)
    {
    }
}
