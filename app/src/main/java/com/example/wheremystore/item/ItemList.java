package com.example.wheremystore.item;

import android.net.Uri;

import com.example.wheremystore.dto.ImgDTO;

import java.util.ArrayList;
import java.util.List;

public class ItemList {
    public static List<Uri> uriList = new ArrayList<>();
    public static List<ImgDTO> imgDTOList = new ArrayList<>();

    public static List<Uri> getUriList() {
        return uriList;
    }

    public static void setUriList(List<Uri> mUriList) {
        uriList = mUriList;
    }

    public static List<ImgDTO> getImgDTOList() {
        return imgDTOList;
    }

    public static void setImgDTOList(List<ImgDTO> mImgDTOList) {
        imgDTOList = mImgDTOList;
    }
}
