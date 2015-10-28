package be.hepl.benbear.boatapp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import be.hepl.benbear.iobrep.Packet;
import be.hepl.benbear.iobrep.ResponsePacket;

public class ServerCommunicationService extends Service {
    private final IBinder mBinder = new LocalBinder();
    Socket sock = null;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    SharedPreferences settings;
    private final Queue<ResponsePacket> packetQueue = new ConcurrentLinkedQueue<>();

    ArrayList<PacketNotificationListener> listeners = new ArrayList<PacketNotificationListener> ();

    public void addOnPacketReceptionListener(PacketNotificationListener listener) {
        this.listeners.add(listener);
    }

    public void removeOnPacketReceptionListener(PacketNotificationListener listener) {
        this.listeners.remove(listener);
    }

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

                    sock = new Socket(InetAddress.getByName(settings.getString("server_ip", "0.0.0.0")), settings.getInt("server_port", 30000));
                    ois = new ObjectInputStream(sock.getInputStream());
                    oos = new ObjectOutputStream(sock.getOutputStream());
                    startReadTask();
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

    public void startReadTask() {
        new ReadPacketTask().execute();
    }

    public ResponsePacket getPacket() {
        return packetQueue.poll();
    }

    public class LocalBinder extends Binder {
        ServerCommunicationService getService() {
            return ServerCommunicationService.this;
        }
    }

    public boolean isEstablished() {
        if(sock != null) {
            return true;
        }
        return false;
    }

    private class ReadPacketTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while(!Thread.interrupted()) {
                try {
                    readPacket();
                } catch (ProtocolException e) {

                }
            }
            return null;
        }
    }

    public void writePacket(Packet p) throws ProtocolException {
        try {
            oos.writeObject(p);
        } catch (IOException e) {
            throw new ProtocolException("Network error", e);
        }
    }

    private void readPacket() throws ProtocolException {
        packetQueue.add(readPacket(0));
        for (PacketNotificationListener listener : listeners) {
            listener.onPacketReception();
        }
    }

    private ResponsePacket readPacket(int tries) throws ProtocolException {
        try {
            Object o = ois.readObject();

            if (o instanceof ResponsePacket) {
                return ((ResponsePacket) o);
            }
            throw new ProtocolException("Invalid packet received: " + o.getClass().getName());
        } catch (ClassNotFoundException | IOException e) {
            if(tries < 3) {
                try {
                    TimeUnit.MILLISECONDS.sleep(tries * 500);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                }
                return readPacket(tries + 1);
            }
            throw new ProtocolException(e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
