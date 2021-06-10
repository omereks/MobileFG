package com.example.mobilefg;
//
public class Model {
    int port;
    String IP;
    double throttle;
    double rudder;

    public void setThrottle(double throttle) {
        this.throttle = throttle;
    }

    public void setRudder(double rudder) {
        this.rudder = rudder;
    }

    Model(String IP, int port){
        this.IP = IP;
        this.port = port;
        this.throttle = 0;
        this.rudder = 0;
    }

    void Connect(){
        // kvar
    }

}
