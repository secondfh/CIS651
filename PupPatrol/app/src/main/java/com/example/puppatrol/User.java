package com.example.puppatrol;

import com.google.firebase.database.ServerValue;

public class User {
    public String displayname;
    public String email;
    public String phone;
    public Object timestamp;
	public Double walker_reviews, total_rating, walker_rating;
    public String url;

    public User(String displayname, String email, String phone,Double walker_rating, Double walker_reviews, Double total_rating) {
        this.displayname=displayname;
        this.email=email;
        this.phone=phone;
        this.timestamp= ServerValue.TIMESTAMP;
		this.total_rating=total_rating;
        this.walker_rating=walker_rating;
        this.walker_reviews=walker_reviews;
    }
    public User(String displayname, String email, String phone, boolean equals, boolean equals1, boolean equals2, Double walker_rating, Double walker_reviews, Double total_rating ) {
    }

    public Object getTimestamp(){
        return timestamp;
    }
    public User() {

    }
    public User(String displayname, String email, String phone, String url, Double walker_rating, Double walker_reviews, Double total_rating) {
        this(displayname, email, phone, walker_rating, walker_reviews, total_rating);
        this.url = url;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}
