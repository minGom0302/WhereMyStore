package com.example.wheremystore.dto;

import com.google.gson.annotations.SerializedName;

public class ImgDTO {
    @SerializedName("seq")
    int seq;
    @SerializedName("file_name")
    String file_name;
    @SerializedName("file_path")
    String file_path;
    @SerializedName("storeSeq")
    int StoreSeq;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getStoreSeq() {
        return StoreSeq;
    }

    public void setStoreSeq(int storeSeq) {
        StoreSeq = storeSeq;
    }
}
