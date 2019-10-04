package com.example.multihoppin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.InetAddresses;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button WifiOn;
    Button Discover;
    Button Send;
    ListView AvailablePeers;
    TextView ConnectionStatus;
    TextView ReceivedMessage;

    EditText WriteMessage;

    WifiManager wifiManger;

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;


    BroadcastReceiver wifiP2PBroadcast;
    IntentFilter filter;


    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    String[] DevicesNames;
    WifiP2pDevice[] Devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialize();
        AddListeners();
    }

    private void AddListeners() {
        //Toggle Wifi Listener
        WifiOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wifiManger.isWifiEnabled()) {

                    wifiManger.setWifiEnabled(false);
                    WifiOn.setText("On");


                } else {
                    wifiManger.setWifiEnabled(true);
                    WifiOn.setText("Off");


                }

            }
        });

        // Discover Peers Listener

        Discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        ConnectionStatus.setText("Discovery started");

                    }

                    @Override
                    public void onFailure(int reason) {

                        ConnectionStatus.setText("Discovery failed");


                    }
                });
            }
        });

        AvailablePeers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = AvailablePeers.getItemAtPosition(position);
                final WifiP2pDevice device = Devices[position];
                WifiP2pConfig config=new WifiP2pConfig();
                config.deviceAddress=device.deviceAddress;
                wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                        Toast.makeText(getApplicationContext(),"Connected to Device "+device.deviceName,Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailure(int reason) {

                        Toast.makeText(getApplicationContext(),"Failed to connect",Toast.LENGTH_SHORT).show();


                    }
                });

            }
        });

    }


    WifiP2pManager.ConnectionInfoListener connectionInfoListener= new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {

            final InetAddress groupOwnerAddress=info.groupOwnerAddress;

            // Am i the group owner? make me host
            if(info.groupFormed && info.isGroupOwner){

                ConnectionStatus.setText("Host");

                //Host code comes here

            }
            else{

                ConnectionStatus.setText("Client");

            }


        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiP2PBroadcast);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiP2PBroadcast, filter);
    }

    private void Initialize() {

        WifiOn = (Button) findViewById(R.id.WifiOn);
        Discover = (Button) findViewById(R.id.Discover);
        Send = (Button) findViewById(R.id.Send);

        WriteMessage = (EditText) findViewById(R.id.WriteMessage);
        ConnectionStatus = (TextView) findViewById(R.id.status);
        ReceivedMessage = (TextView) findViewById(R.id.message);

        AvailablePeers = (ListView) findViewById(R.id.list_items);

        wifiManger = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);

        wifiP2PBroadcast = new WifiDirectBroadCast(wifiP2pManager, channel, this);


        filter = new IntentFilter();

        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


    }


    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){

                peers.clear();
                peers.addAll(peerList.getDeviceList());
                DevicesNames = new String[peerList.getDeviceList().size()];
                Devices = new WifiP2pDevice[peerList.getDeviceList().size()];
                int i =0;
                for(WifiP2pDevice device: peerList.getDeviceList()){

                    DevicesNames[i]=device.deviceName;
                    Devices[i]=device;


                    i++;

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,DevicesNames);
                AvailablePeers.setAdapter(adapter);
            }
            if(peers.size()==0){
                Toast.makeText(getApplicationContext(),"No peers available", Toast.LENGTH_SHORT).show();
                return;

            }

        }
    };

}
