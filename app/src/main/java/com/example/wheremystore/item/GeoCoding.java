package com.example.wheremystore.item;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoCoding {

    public static String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> addressList;
        String nowAddress = null;
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            addressList = geocoder.getFromLocation(lat, lng, 1);
            if(addressList.size() > 0) {
                nowAddress = addressList.get(0).getAddressLine(0).substring(5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nowAddress;
    }


    /*  하나의 주소에 몇개의 좌표가 있을지 모름
    public static Location getLatLng(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> addressList;
        Location nowLocation = new Location("");
        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList.size() > 0) {
                Address addr = addressList.get(0);
                nowLocation.setLatitude(addr.getLatitude());
                nowLocation.setLongitude(addr.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nowLocation;
    }*/
}
