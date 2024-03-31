package com.ritorno.geoblocks;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrocaListaUsuariosActivity extends Activity implements AdapterView.OnItemClickListener {

    static final int REQUEST_ENABLE_BT = 1;
    protected static final String NAME = "SERVIDOR_TROCAS";
    protected static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    List<BluetoothDevice> bluetoothDevices = null;

    protected AcceptThread acceptThread;
    protected ConnectThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_user_list);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Inicializa a lista
        bluetoothDevices = new ArrayList<BluetoothDevice>();

        //inicia o servidor
        acceptThread = new AcceptThread();
        acceptThread.start();

        //busca dispositivos
        //bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (device.getName() != null)
                    bluetoothDevices.add(device);

            }
            Button btn = (Button) findViewById(R.id.buscar);
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // No início da busca, alerta o usuário para o período de espera
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, "Searchig...", duration);
                toast.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // Habilita o botão novamente e restaura a cor original
                btn.setEnabled(true);
                btn.setTextColor(Color.parseColor("#000000"));
                listar();
            }
        }
    };

    public void updateBT(View v) {

        bluetoothDevices.clear();

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(receiver, filter);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothAdapter.startDiscovery();
    }

    public void listar() {

        try {

            ListView listView = (ListView) findViewById(R.id.bluetooth_user_list);
            AdapterTroca adapterTroca = new AdapterTroca(bluetoothDevices, this);

            listView.setAdapter(adapterTroca);
            listView.setOnItemClickListener(this);

        } catch (Exception e) {
            Log.e("TROCA", "ERRO AO LISTAR: " + e.getMessage());
        }

    }

    public void clickVoltar(View v) {

        finish();
    }

    @Override
    protected void onDestroy() {

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);

        if (acceptThread != null)
            acceptThread.cancel();

        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int idx, long id) {
        // Recupera o device selecionado
        BluetoothDevice device = bluetoothDevices.get(idx);

        //Encerra a conexão antiga
        if (connectThread != null)
            connectThread.cancel();

        //Inicia uma nova
        connectThread = new ConnectThread(device);
        connectThread.start();

    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                }
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
            } catch (IOException e) {
                Log.e("Socket Fail", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.

            Log.e("TROCA", "Running Server");

            while (true) {
                try {
                    socket = mmServerSocket.accept();

                    if (socket != null) {
                        // A connection was accepted. Perform work associated with
                        // the connection in a separate thread.
                        manageMyConnectedSocket(socket);
                        mmServerSocket.close();
                        break;
                    }
                } catch (IOException e) {
                    Log.e("TROCA", "Socket's accept() method failed", e);
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {

            Log.e("TROCA", "Closing Server");

            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("TROCA", "Could not close the connect socket", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                }
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e("TROCA", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            bluetoothAdapter.cancelDiscovery();

            Log.e("TROCA", "Running Client");

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("TROCA", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {

            Log.e("Troca", "Closing Client");

            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("TROCA", "Could not close the client socket", e);
            }
        }
    }

    public void manageMyConnectedSocket(BluetoothSocket socket){

        Log.e("TROCA", "Conexao Efetuada");

        if(acceptThread != null)
            acceptThread.cancel();

        MyApp.setBluetoothSocket(socket);

        Intent it = new Intent(this, TradeNFT.class);
        startActivity(it);
    }
}
