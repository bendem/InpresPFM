package be.hepl.benbear.boatapp;

import android.app.AlertDialog;
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

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import be.hepl.benbear.iobrep.Container;
import be.hepl.benbear.iobrep.ContainerInResponsePacket;
import be.hepl.benbear.iobrep.GetContainersResponsePacket;
import be.hepl.benbear.iobrep.ResponsePacket;

public class MainActivity extends AppCompatActivity implements PacketNotificationListener {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("y-M-d");

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

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, ServerCommunicationService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
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
        int id = item.getItemId();

        if (id == R.id.action_graphs) {
            startActivity(new Intent(this, GraphActivity.class));
            return true;
        } else if (id == R.id.action_fill) {
            fillWithDummyData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPacketReception() {
        final ResponsePacket rp = scs.getPacket();

        switch(rp.getId()) {
            case GET_CONTAINERS_RESPONSE:
                if (rp.isOk()) {
                    if(((GetContainersResponsePacket) rp).getContainers() != null) {
                        fragLoad.fillContainerList(((GetContainersResponsePacket) rp).getContainers());
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Error")
                                        .setMessage(rp.getReason())
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }
                        });
                        Log.i("LOG", "No containers for that destination");
                    }
                } else {
                    Log.i("LOG", rp.getReason());
                }
                break;
            case CONTAINER_OUT_RESPONSE:
                if (rp.isOk()) {
                    Container contOut = fragLoad.containerLoaded();
                    try {
                        containerMoveDAO.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return;
                    }
                    containerMoveDAO.addContainerMove(contOut.getId(), contOut.getDestination(), Calendar.getInstance().getTime(), ContainerMoveSQLiteHelper.MoveType.OUT);
                    containerMoveDAO.close();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Error")
                                    .setMessage(rp.getReason())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    });
                    Log.i("LOG", rp.getReason());
                }
                break;
            case CONTAINER_OUT_END_RESPONSE:
                if (rp.isOk()) {
                    fragLoad.clearContainerList();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Error")
                                    .setMessage(rp.getReason())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    });
                    Log.i("LOG", rp.getReason());
                }
                break;
            case BOAT_ARRIVED_RESPONSE:
                if (rp.isOk()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Info")
                                    .setMessage("Boat recorded")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    });
                    Log.i("DEBUG ABOAT", "Boat arrived");
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Error")
                                    .setMessage(rp.getReason())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    });
                    Log.i("LOG", rp.getReason());
                }
                break;
            case CONTAINER_IN_RESPONSE:
                if (rp.isOk()) {
                    Container contIn = ((ContainerInResponsePacket) rp).getContainer();
                    fragUnload.containerUnloaded(contIn);
                    try {
                        containerMoveDAO.open();
                        return;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    containerMoveDAO.addContainerMove(contIn.getId(), contIn.getDestination(), Calendar.getInstance().getTime(), ContainerMoveSQLiteHelper.MoveType.IN);
                    containerMoveDAO.close();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Error")
                                    .setMessage(rp.getReason())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    });
                    Log.i("LOG", rp.getReason());
                }
                break;
            case CONTAINER_IN_END_RESPONSE:
                if (rp.isOk()) {
                    fragUnload.clearContainerList();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Error")
                                    .setMessage(rp.getReason())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    });
                    Log.i("LOG", rp.getReason());
                }
                break;
            case INVALID_SESSION_RESPONSE:
                Log.i("LOG", rp.getReason());
                scs.setSession(null);
                startActivity(new Intent(this, LoginActivity.class));
            default:
                break;
        }
    }

    private void fillWithDummyData() {
        try {
            containerMoveDAO.open();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        try {
            containerMoveDAO.addContainerMove("Container1", "Paris", DATE_FORMAT.parse("2015-10-20"), ContainerMoveSQLiteHelper.MoveType.IN);
            containerMoveDAO.addContainerMove("Container2", "Rome", DATE_FORMAT.parse("2015-10-21"), ContainerMoveSQLiteHelper.MoveType.IN);
            containerMoveDAO.addContainerMove("Container3", "Londres", DATE_FORMAT.parse("2015-10-21"), ContainerMoveSQLiteHelper.MoveType.IN);
            containerMoveDAO.addContainerMove("Container4", "Paris", DATE_FORMAT.parse("2015-10-22"), ContainerMoveSQLiteHelper.MoveType.IN);
            containerMoveDAO.addContainerMove("Container1", "Paris", DATE_FORMAT.parse("2015-10-22"), ContainerMoveSQLiteHelper.MoveType.OUT);
            containerMoveDAO.addContainerMove("Container2", "Rome", DATE_FORMAT.parse("2015-10-23"), ContainerMoveSQLiteHelper.MoveType.OUT);
            containerMoveDAO.addContainerMove("Container3", "Londres", DATE_FORMAT.parse("2015-10-23"), ContainerMoveSQLiteHelper.MoveType.OUT);
            containerMoveDAO.addContainerMove("Container5", "Paris", DATE_FORMAT.parse("2015-10-23"), ContainerMoveSQLiteHelper.MoveType.IN);
            containerMoveDAO.addContainerMove("Container6", "Rome", DATE_FORMAT.parse("2015-10-24"), ContainerMoveSQLiteHelper.MoveType.IN);
            containerMoveDAO.addContainerMove("Container4", "Paris", DATE_FORMAT.parse("2015-10-25"), ContainerMoveSQLiteHelper.MoveType.OUT);
            containerMoveDAO.addContainerMove("Container5", "Paris", DATE_FORMAT.parse("2015-10-25"), ContainerMoveSQLiteHelper.MoveType.OUT);
            containerMoveDAO.addContainerMove("Container7", "Londres", DATE_FORMAT.parse("2015-10-26"), ContainerMoveSQLiteHelper.MoveType.IN);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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
