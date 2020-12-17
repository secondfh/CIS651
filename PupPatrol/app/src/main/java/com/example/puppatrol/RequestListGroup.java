package com.example.puppatrol;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestListGroup {
    private String groupName;
    private List<String> keyList = new ArrayList<>();
    private HashMap<String, RequestListItem> requests = new HashMap<>();

    public RequestListGroup(String groupName){
        this.groupName = groupName;
    }

    public String getGroupName(){
        return groupName;
    }

    public void setGroupName(String groupName){
        this.groupName = groupName;
    }

    public RequestListItem getItem(int position){
        if (position >= 0 && position < keyList.size())
            return requests.get(keyList.get(position));
        else
            return null;
    }

    public int size(){
        return keyList.size();
    }

    public RequestListItem getItemByKey(String key){
        return requests.get(key);
    }

    public void addItem(@NonNull RequestListItem item){
        keyList.add(item.getKey());
        requests.put(item.getKey(), item);
    }

    public RequestListItem removeItem(int position){
        if (position >= 0 && position < requests.size()){
            String key = keyList.get(position);
            RequestListItem item = requests.get(key);
            requests.remove(key);
            keyList.remove(position);
            return item;
        } else {
            return null;
        }
    }

    public RequestListItem removeItemByKey(String key){
        RequestListItem item = requests.get(key);
        if (item != null){
            requests.remove(key);
            keyList.remove(key);
        }
        return item;
    }
}
