/*
     * Part of this source code belongs to Telegram for Android v. 1.3.2, Copyright Nikolai Kudashov, 2013.
     * and this source code itself belongs to Stepss for Android v. 1.0
     * It is licensed under GNU GPL v. 2 or later.
     * You should have received a copy of the license in this archive (see LICENSE).
     *
     * Copyright Luiz Peres, 2015.
     */

package project.main.steptaneous;

import java.util.Locale;

import android.content.Intent;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.telegram.android.LocaleController;
import org.telegram.android.NotificationCenter;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ConnectionsManager;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Adapters.DialogsSearchAdapter;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, NotificationCenter.NotificationCenterDelegate {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private ListView searchListView;
    private View searchEmptyView;
    private View progressView;
    private View emptyView;
    private int currentConnectionState;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent theIntent = getIntent();

        ApplicationLoader.postInitApplication();

        if (!UserConfig.isClientActivated())
        {
            if ((theIntent != null) && !theIntent.getBooleanExtra("fromIntro", false))
            {
                Intent i = new Intent(this, IntroActivity.class);
                startActivity(i);
                super.onCreate(savedInstanceState);
                finish();
                return;
            }

            Intent i = new Intent(this, LoginContainerActivity.class);
            startActivity(i);
            super.onCreate(savedInstanceState);
            finish();
            return;
        }
        else
        {
            super.onCreate(savedInstanceState);
            setElementsActivity();
            setNetworkObserver();
            handleIntent(theIntent);
        }
    }

    private void setNetworkObserver()
    {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        if (UserConfig.isClientActivated() && intent != null && intent.getAction() != null)
        {
            if (intent.getAction().startsWith("com.tmessages.openchat"))
            {
                Integer push_chat_id = intent.getIntExtra("chatId", 0);
                Integer push_user_id = intent.getIntExtra("userId", 0);
                Integer push_enc_id = intent.getIntExtra("encId", 0);
                boolean showDialogsList = false;

                if (push_chat_id != 0 || push_user_id != 0 || push_enc_id != 0)
                {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);
                }
                else
                {
                    showDialogsList = true;
                }

                if (push_user_id != 0) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", push_user_id);
                    // TODO Luiz, pay attention to that in the future.
                    //ChatActivity fragment = new ChatActivity(args);
                } else if (push_chat_id != 0) {
                    Bundle args = new Bundle();
                    args.putInt("chat_id", push_chat_id);
                    // TODO Luiz, pay attention to that in the future.
                    //ChatActivity fragment = new ChatActivity(args);
                } else if (push_enc_id != 0) {
                    Bundle args = new Bundle();
                    args.putInt("enc_id", push_enc_id);
                    // TODO Luiz, pay attention to that in the future.
                    //ChatActivity fragment = new ChatActivity(args);
                } else if (showDialogsList) {
                    final ActionBar actionBar = getSupportActionBar();
                    actionBar.setSelectedNavigationItem(mSectionsPagerAdapter.getPositionElements(MessagesFragment.class));
                }

                intent.setAction(null);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Utilities.checkForCrashes(this);
        Utilities.checkForUpdates(this);
        ApplicationLoader.mainInterfacePaused = false;
        ConnectionsManager.getInstance().setAppPaused(false, false);
        updateCurrentConnectionState();
    }

    private void updateCurrentConnectionState() {
        String text = null;
        if (currentConnectionState == 0)
        {
            text = LocaleController.getString("app_name", R.string.app_name);
        }
        if (currentConnectionState == 1) {
            text = LocaleController.getString("WaitingForNetwork", R.string.WaitingForNetwork);
        } else if (currentConnectionState == 2) {
            text = LocaleController.getString("Connecting", R.string.Connecting);
        } else if (currentConnectionState == 3) {
            text = LocaleController.getString("Updating", R.string.Updating);
        }
        getSupportActionBar().setTitle(text);
    }

    private void setElementsActivity() {
        setContentView(R.layout.activity_main);
        // Set up the action bar.
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        createSearchBtn(searchItem);

        return super.onCreateOptionsMenu(menu);
    }

    private void createSearchBtn(MenuItem item)
    {
        searchListView = (ListView) findViewById(R.id.search_list_view);
        progressView = findViewById(R.id.search_progress_layout);

        searchEmptyView = findViewById(R.id.search_empty_view);
        searchEmptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        emptyView = findViewById(R.id.search_empty_view);
        emptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        dialogsSearchAdapter = new DialogsSearchAdapter(getBaseContext(), 2);
        dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.MessagesActivitySearchAdapterDelegate() {
            @Override
            public void searchStateChanged(boolean search) {
                if (searchListView != null) {
                    progressView.setVisibility(search ? View.VISIBLE : View.INVISIBLE);
                    searchEmptyView.setVisibility(search ? View.INVISIBLE : View.VISIBLE);
                    searchListView.setEmptyView(search ? progressView : searchEmptyView);
                }
            }
        });

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                dialogsSearchAdapter.notifyDataSetChanged();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() != 0) {

                    if (searchEmptyView != null && searchListView.getEmptyView() == emptyView) {
                        searchListView.setEmptyView(searchEmptyView);
                        emptyView.setVisibility(View.INVISIBLE);
                        progressView.setVisibility(View.INVISIBLE);
                    }
                }

                if (dialogsSearchAdapter != null) {
                    // This statement will stay here to remind us in the future, if we need it.
                    boolean serverOnly = false;
                    dialogsSearchAdapter.searchDialogs(s, serverOnly);
                }

                return true;
            }
        });

        final ActionBar.TabListener tabListener = this;

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (dialogsSearchAdapter != null) {
                    searchListView.setAdapter(dialogsSearchAdapter);
                    dialogsSearchAdapter.notifyDataSetChanged();
                }

                mViewPager.setVisibility(View.GONE);
                actionBar.removeAllTabs();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                if (dialogsSearchAdapter != null) {
                    dialogsSearchAdapter.searchDialogs(null, false);
                    dialogsSearchAdapter.notifyDataSetChanged();
                }

                mViewPager.setVisibility(View.VISIBLE);
                for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                    actionBar.addTab(
                            actionBar.newTab()
                                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                                    .setTabListener(tabListener));
                }

                return true;
            }
        });

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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void didReceivedNotification(int id, Object... args)
    {
        if (id == NotificationCenter.didUpdatedConnectionState) {
            int state = (Integer)args[0];
            if (currentConnectionState != state) {
                FileLog.e("tmessages", "switch to state " + state);
                currentConnectionState = state;
                updateCurrentConnectionState();
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (getPageTitle(position).toString().toUpperCase().equals(LocaleController.getString("title_section3", R.string.title_section3).toUpperCase()))
            {
                return new MessagesFragment();
            }

            return PlaceholderFragment.newInstance(position + 1);
        }

        public int getPositionElements(Class theClass)
        {
            if (theClass == MessagesFragment.class)
                return 2;
            else
                return -1;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return LocaleController.getString("title_section1", R.string.title_section1).toUpperCase(l);
                case 1:
                    return LocaleController.getString("title_section2", R.string.title_section2).toUpperCase(l);
                case 2:
                    return LocaleController.getString("title_section3", R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
