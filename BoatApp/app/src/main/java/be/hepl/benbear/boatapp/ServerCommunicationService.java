package be.hepl.benbear.boatapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ServerCommunicationService extends Service {
    private final IBinder mBinder = new LocalBinder();
    Socket sock = null;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    SharedPreferences settings;

    @Override
    public void onCreate() {
        Toast.makeText(this, "Creation server connection service", Toast.LENGTH_SHORT).show();
        settings = getSharedPreferences(getString(R.string.config_file), 0);
        establishConnection();
    }

    // TODO Check if connection was established
    public void establishConnection() {
        Toast.makeText(this, "Connecting to server", Toast.LENGTH_LONG).show();
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (sock != null) {
                        sock.close();
                    }

                    sock = new Socket(InetAddress.getByName(settings.getString("server_ip", "0.0.0.0")),settings.getInt("server_port", 30000));
                    dis = new DataInputStream(sock.getInputStream());
                    dos = new DataOutputStream(sock.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Destruction server connection", Toast.LENGTH_LONG).show();
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class LocalBinder extends Binder {
        ServerCommunicationService getService() {
            return ServerCommunicationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
