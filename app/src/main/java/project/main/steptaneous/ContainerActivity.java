package project.main.steptaneous;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import project.main.steptaneous.base.BaseFragmentStep;

public class ContainerActivity extends ActionBarActivity {

    public enum TYPE_SCREEN
    {
        TS_CONTACTS (1),
        TS_CHATS (2);

        private final int value;

        private TYPE_SCREEN(int value) {
            this.value = value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        handleExtras(getIntent());
    }

    private void handleExtras(Intent theIntent)
    {
        BaseFragmentStep fragment = null;

        switch (TYPE_SCREEN.values()[theIntent.getExtras().getInt("typeScreen")])
        {
            case TS_CONTACTS:
                fragment = new ContactsFragment(theIntent.getExtras());
                break;
        }

        if (fragment != null)
        {
            getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_container, menu);
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
}
