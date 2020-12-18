package com.example.puppatrol;

public class RequestListItem {
    private String requestKey;
    private WalkRequest walkRequest;

    public RequestListItem(String requestKey, WalkRequest walkRequest){
        this.requestKey = requestKey;
        this.walkRequest = walkRequest;
    }

    public String getKey(){
        return requestKey;
    }

    public WalkRequest getRequest(){
        return walkRequest;
    }

    public void setStatus(String status){
        walkRequest.setStatus(status);
    }

}
