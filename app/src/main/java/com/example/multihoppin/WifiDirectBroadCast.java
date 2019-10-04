package com.example.multihoppin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class WifiDirectBroadCast extends BroadcastReceiver {

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    MainActivity mainActivity;
    public WifiDirectBroadCast(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, MainActivity mainActivity){

        this.mainActivity=mainActivity;
        this.channel=channel;
        this.wifiP2pManager=wifiP2pManager;


    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String Action=intent.getAction();
        if(wifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals((Action))){}
        else if(wifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals((Action))){
            wifiP2pManager.requestPeers(channel,mainActivity.peerListListener);



        }
        else if(wifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals((Action))){

            int state=intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
            if(state==wifiP2pManager.WIFI_P2P_STATE_ENABLED){

                Toast.makeText(context,"Wifi p2p is On",Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(context,"Wifi p2p is Off",Toast.LENGTH_SHORT).show();


            }
        }
        else if(wifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals((Action))){}


    }
}
