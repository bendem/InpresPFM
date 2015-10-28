package be.hepl.benbear.boatapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import be.hepl.benbear.iobrep.LoginPacket;
import be.hepl.benbear.iobrep.ResponsePacket;

public class LoginActivity extends AppCompatActivity implements PacketNotificationListener{
    ServerCommunicationService scs = null;
    boolean serviceConnected = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServerCommunicationService.LocalBinder binder = (ServerCommunicationService.LocalBinder) service;
            scs = binder.getService();
            scs.addOnPacketReceptionListener(LoginActivity.this);
            serviceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            scs = null;
            serviceConnected = false;
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
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
        Intent i = new Intent(this, ServerCommunicationService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scs.removeOnPacketReceptionListener(this);
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
        Toast.makeText(this,"Got result, re-create the connection accordingly", Toast.LENGTH_LONG).show();

        scs.establishConnection();
    }

    private void sendLogin(String username, String password) throws IOException {
        scs.writePacket(new LoginPacket(username, password));
    }

    @Override
    public void onPacketReception() {
        ResponsePacket rp = scs.getPacket();

        switch(rp.getId()) {
            case LOGIN_RESPONSE:
                if(rp.isOk()) {
                    scs.removeOnPacketReceptionListener(this);
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    Toast.makeText(this,"Bad login.", Toast.LENGTH_LONG).show();
                }
                break;
            default:

                break;
        }

    }
}
