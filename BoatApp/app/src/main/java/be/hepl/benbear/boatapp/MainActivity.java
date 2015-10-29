package be.hepl.benbear.boatapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Calendar;

import be.hepl.benbear.iobrep.Container;
import be.hepl.benbear.iobrep.ContainerInResponsePacket;
import be.hepl.benbear.iobrep.GetContainersResponsePacket;
import be.hepl.benbear.iobrep.ResponsePacket;

public class MainActivity extends AppCompatActivity implements PacketNotificationListener {
    /*package*/ ServerCommunicationService scs = null;
    private ContainerMoveDAO containerMoveDAO;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServerCommunicationService.LocalBinder binder = (ServerCommunicationService.LocalBinder) service;
            scs = binder.getService();
            scs.addOnPacketReceptionListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            scs = null;
        }
    };

    private LoadingFragment fragLoad = LoadingFragment.newInstance();
    private UnloadingFragment fragUnload = UnloadingFragment.newInstance();

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, ServerCommunicationService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        containerMoveDAO = new ContainerMoveDAO(this);
    }

    @Override
    public void onBackPressed() {
        scs.removeOnPacketReceptionListener(this);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

    @Override
    public void onPacketReception() {
        ResponsePacket rp = scs.getPacket();

        if (!rp.isOk()) {
            // Check the error
            // Most likely return to Login Activity
        }

        switch(rp.getId()) {
            case GET_CONTAINERS_RESPONSE:
                if(((GetContainersResponsePacket)rp).getContainers() != null) {
                    fragLoad.fillContainerList(((GetContainersResponsePacket)rp).getContainers());
                } else {
                    // TODO GIVE FEEDBACK
                    Log.d("DEBUG GET", "No containers for that destination");
                }
                break;
            case CONTAINER_OUT_RESPONSE:
                Container contOut = fragLoad.containerLoaded();
                try {
                    containerMoveDAO.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                containerMoveDAO.addContainerMove(contOut.getId(), contOut.getDestination(), Calendar.getInstance().getTime().getTime(), "OUT");
                containerMoveDAO.close();
                break;
            case CONTAINER_OUT_END_RESPONSE:
                fragLoad.clearContainerList();
                break;
            case BOAT_ARRIVED_RESPONSE:
                // TODO GIVE FEEDBACK
                Log.d("DEBUG ABOAT", "Boat arrived");
                break;
            case CONTAINER_IN_RESPONSE:
                Container contIn = ((ContainerInResponsePacket)rp).getContainer();
                fragUnload.containerUnloaded(contIn);
                try {
                    containerMoveDAO.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                containerMoveDAO.addContainerMove(contIn.getId(), contIn.getDestination(), Calendar.getInstance().getTime().getTime(), "IN");
                containerMoveDAO.close();
                break;
            case CONTAINER_IN_END_RESPONSE:
                fragUnload.clearContainerList();
                break;
            default:

                break;
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
            if (position == 0) {
                return fragLoad;
            } else {
                return fragUnload;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
