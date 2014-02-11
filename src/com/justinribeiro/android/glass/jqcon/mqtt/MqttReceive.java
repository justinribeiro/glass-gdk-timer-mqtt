package com.justinribeiro.android.glass.jqcon.mqtt;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttReceive implements MqttCallback {

    private ContextWrapper context;

    public MqttReceive(ContextWrapper context) {

        this.context = context;
    }

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@SuppressLint("NewApi")
	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		
		Log.i(arg0, arg1.toString());
		
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}
}
