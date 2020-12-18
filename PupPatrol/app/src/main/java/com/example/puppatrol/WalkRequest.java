package com.example.puppatrol;

import com.google.firebase.database.ServerValue;

public class WalkRequest {
    private String client;
    private String walker;
    private String status;
    private String offer;
    private Object timestamp;
    private String clientlat, clientlong;

    public WalkRequest() {

    }

    public WalkRequest(String client, String walker, String status) {
        this.client = client;
        this.walker = walker;
        this.status = status;
        this.timestamp= ServerValue.TIMESTAMP;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getWalker() {
        return walker;
    }

    public void setWalker(String walker) {
        this.walker = walker;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOffer(){
        return offer;
    }

    public void setOffer(String offer){
        this.offer = offer;
    }

    public Object getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getClientlat(){
        return clientlat;
    }

    public void setClientlat(String clientlat){
        this.clientlat = clientlat;
    }

    public String getClientlong(){
        return clientlong;
    }

    public void setClientlong(String clientlong){
        this.clientlong = clientlong;
    }
}
