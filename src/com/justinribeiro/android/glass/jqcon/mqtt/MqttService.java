package com.justinribeiro.android.glass.jqcon.mqtt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.*;

/**
* @author Dominik Obermaier
*/
public class MqttService extends Service {

    /* 	In a real application, you should get an Unique Client ID of the device and use this, see
		http://android-developers.blogspot.de/2011/03/identifying-app-installations.html */
    public static final String clientId = "android-client";

    public static final String TOPIC = "office/ops";
    private MqttClient mqttClient;


    public IBinder onBind(Intent intent) {
        return null;
    }

	@Override
    public void onStart(Intent intent, int startId) {

        try {
        	String host = intent.getStringExtra("host");
        	
            mqttClient = new MqttClient(host, clientId, new MemoryPersistence());

            mqttClient.setCallback(new MqttReceive(this));
            mqttClient.connect();

            //Subscribe to all subtopics of homeautomation
            mqttClient.subscribe(TOPIC);

        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        try {
            mqttClient.disconnect(0);
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
