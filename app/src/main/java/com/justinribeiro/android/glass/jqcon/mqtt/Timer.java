/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.justinribeiro.android.glass.jqcon.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.os.SystemClock;

/**
 * Model holding the Timer state.
 */
public class Timer {

    /**
     * Interface to listen for changes on the {@link Timer}.
     */
    public interface TimerListener {
        /** Timer has started. */
        public void onStart();
        /** Timer has been paused. */
        public void onPause();
        /** Timer has been reset */
        public void onReset();
    }

    private long mDurationMillis;
    private long mStartTimeMillis;
    private long mPauseTimeMillis;
    private static MqttClient client = null;

    private TimerListener mListener;

    public Timer() {
        this(0);
    }

    public Timer(long durationMillis) {
        setDurationMillis(durationMillis);
    }

    /**
     * Sets the timer's duration in milliseconds.
     */
    public void setDurationMillis(long durationMillis) {
        mDurationMillis = durationMillis;
        if (mListener != null) {
            mListener.onReset();
        }
    }

    /**
     * Gets the timer's duration in milliseconds.
     */
    public long getDurationMillis() {
        return mDurationMillis;
    }

    /**
     * Returns whether or not the timer is running.
     */
    public boolean isRunning() {
        return mStartTimeMillis > 0 && mPauseTimeMillis == 0;
    }

    /**
     * Returns whether or not the timer has been started.
     */
    public boolean isStarted() {
        return mStartTimeMillis > 0;
    }

    /**
     * Gets the remaining time in milliseconds.
     */
    public long getRemainingTimeMillis() {
        long remainingTime = mDurationMillis;

        if (mPauseTimeMillis != 0) {
            remainingTime -= mPauseTimeMillis - mStartTimeMillis;
        } else if (mStartTimeMillis != 0) {
            remainingTime -= SystemClock.elapsedRealtime() - mStartTimeMillis;
        }
        
        if ((client != null) && client.isConnected()) { 
			// Don't do anything
		} else {
			try {
				// File persist not working on Glass
				MemoryPersistence persistence = new MemoryPersistence();
				client = new MqttClient("", "", persistence);
			} catch (MqttException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Connect to the broker
			try {
				client.connect();
			} catch (MqttSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
			

		if ((client != null) && client.isConnected()) {
			MqttTopic topic = client.getTopic("");
			
			String sendtobroker = String.valueOf(remainingTime);
						
			MqttMessage message = new MqttMessage(sendtobroker.getBytes());
			message.setQos(0);
	
			try {
				
				// Give the message to the client for publishing. For QoS 2, this
				// will involve multiple network calls, which will happen
				// asynchronously after this method has returned.
				topic.publish(message);
			
			} catch (MqttException ex) {
				
				// Client has not accepted the message due to a failure
				// Depending on the exception's reason code, we could always retry
				System.err.println("Failed to send message");
				
			}
		}

        return remainingTime;
    }

    /**
     * Starts the timer.
     */
    public void start() {
        long elapsedTime = mPauseTimeMillis - mStartTimeMillis;

        mStartTimeMillis = SystemClock.elapsedRealtime() - elapsedTime;
        mPauseTimeMillis = 0;
		
        if (mListener != null) {
            mListener.onStart();
        }
    }

    /**
     * Pauses the timer.
     */
    public void pause() {
        if (isStarted()) {
            mPauseTimeMillis = SystemClock.elapsedRealtime();
            if (mListener != null) {
                mListener.onPause();
            }
        }
    }

    /**
     * Resets the timer.
     */
    public void reset() {
        mStartTimeMillis = 0;
        mPauseTimeMillis = 0;
        if (mListener != null) {
            mListener.onPause();
            mListener.onReset();
        }
    }

    /**
     * Sets a {@link TimerListener}.
     */
    public void setListener(TimerListener listener) {
        mListener = listener;
    }
}
