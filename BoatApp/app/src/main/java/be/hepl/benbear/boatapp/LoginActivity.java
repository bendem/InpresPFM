package be.hepl.benbear.boatapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import be.hepl.benbear.iobrep.LoginPacket;
import be.hepl.benbear.iobrep.LoginResponsePacket;
import be.hepl.benbear.iobrep.ResponsePacket;

public class LoginActivity extends AppCompatActivity implements PacketNotificationListener{
    private ServerCommunicationService scs = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServerCommunicationService.LocalBinder binder = (ServerCommunicationService.LocalBinder) service;
            scs = binder.getService();
            scs.addOnPacketReceptionListener(LoginActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            scs = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent i = new Intent(this, ServerCommunicationService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);

        Button b = (Button)this.findViewById(R.id.buttonSignIn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scs.isEstablished()) {
                    EditText username = (EditText) findViewById(R.id.editTextUsername);
                    EditText password = (EditText) findViewById(R.id.editTextPassword);
                    try {
                        sendLogin(username.getText().toString(), password.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Can't reach the server", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scs.removeOnPacketReceptionListener(this);
        unbindService(mConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(this, "Settings menu clicked", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DEBUG", "Settings sent results");
        scs.establishConnection();
    }

    private void sendLogin(String username, String password) throws IOException {
        Log.d("DEBUG", "sendlogin(" + username + ", " + password + ")");
        scs.writePacket(new LoginPacket(username, password));
    }

    @Override
    public void onPacketReception() {
        final ResponsePacket rp = scs.getPacket();

        switch(rp.getId()) {
            case LOGIN_RESPONSE:
                if(rp.isOk()) {
                    scs.removeOnPacketReceptionListener(this);
                    scs.setSession(((LoginResponsePacket) rp).getSession());
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Error")
                                    .setMessage(rp.getReason())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    });

                    Log.d("DEBUG", "Packet is not ok: " + rp.getReason());
                }
                break;
            default:

                break;
        }

    }
}
