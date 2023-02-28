package com.example.wheremystore.dto;

import com.google.gson.annotations.SerializedName;

public class StoreInfoDTO {
    // store table
    @SerializedName("seq")
    int seq;
    @SerializedName("name")
    String name;
    @SerializedName("address")
    String address;
    @SerializedName("lat")
    String lat;
    @SerializedName("lng")
    String lng;
    @SerializedName("openTime")
    String openTime;
    @SerializedName("closeTime")
    String closeTime;
    @SerializedName("type")
    String type;
    @SerializedName("day")
    String day;
    @SerializedName("credit")
    String credit;
    @SerializedName("memo")
    String memo;
    @SerializedName("distance")
    String distance;
    @SerializedName("id")
    String id;
    // bookmark table
    @SerializedName("bookmarkSeq")
    int bookmarkSeq;
    @SerializedName("userId")
    String userId;
    @SerializedName("storeSeq")
    int storeSeq;

    public int getSeq() { return seq; }

    public void setSeq(int seq) { this.seq = seq;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getDistance() { return distance; }

    public void setDistance(String distance) { this.distance = distance; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public int getBookmarkSeq() {
        return bookmarkSeq;
    }

    public void setBookmarkSeq(int bookmarkSeq) {
        this.bookmarkSeq = bookmarkSeq;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStoreSeq() {
        return storeSeq;
    }

    public void setStoreSeq(int storeSeq) {
        this.storeSeq = storeSeq;
    }
}
