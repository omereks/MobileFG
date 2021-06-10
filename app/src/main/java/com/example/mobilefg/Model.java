package com.example.mobilefg;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

//
public class Model  extends AsyncTask<String,String,Void> {
    public int port;
    public String IP;
    public double throttle;
    public double rudder;
    public double aileron;
    public double elevator;


    private Socket socket;
    private DataOutputStream outputStream;
    private BlockingQueue<String> queue = new LinkedBlockingDeque<>();


    public void setThrottle(double throttle) {
        this.throttle = throttle;
        sendCommands("set /controls/engines/current-engine/throttle " + String.valueOf(throttle) + "\r\n");
    }

    public void setRudder(double rudder) {
        this.rudder = rudder;
        sendCommands("set /controls/flight/rudder " + String.valueOf(rudder) + "\r\n");
    }

    public void setAileron(float aileron) {
        this.aileron = aileron;
        Log.e("aileron", String.valueOf(aileron));
        sendCommands("set /controls/flight/aileron " + String.valueOf(aileron) + "\r\n");
    }

    public void setElevator(float elevator) {
        this.elevator = elevator;
        Log.e("elevator", String.valueOf(elevator));
        sendCommands("set /controls/flight/elevator " + String.valueOf(elevator) + "\r\n");
    }

    Model(String IP, int port){
        this.IP = IP;
        this.port = port;
        this.throttle = 0;
        this.rudder = 0;
    }

    public void Connect() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(IP, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                    while (true) {
                        out.print(queue.take());
                        out.flush();
                    }
                } catch (Exception e) {
                    Log.e("Socket Fail", "the create socket fail:", e);
                }
            }
        });
        thread.start();
    }

    @Override
    protected Void doInBackground(String... strings) {
        String ip = strings[0];
        int port = Integer.parseInt(strings[1]);
        try {
            socket = new Socket(IP, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            while (true) {
                out.print(queue.take());
                out.flush();
            }
        } catch (Exception e) {
            Log.e("Socket Fail", "the create socket fail:", e);
        }
        return null;
    }

    public void sendCommands(String str) {
        try {
            this.queue.put(str);
        }catch (Exception e) {
            System.out.println(e.toString());
        }
    }


}
