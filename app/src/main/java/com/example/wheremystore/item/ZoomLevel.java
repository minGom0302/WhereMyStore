package com.example.wheremystore.item;

public class ZoomLevel {
    // 구글맵의 줌 래밸 별 거리인데 한번 적용해봄
    static float returnValue;

    public static double kilometer (float zoomLevel) {
        if (zoomLevel < 7) {
           returnValue = (float) 768/2;
        } else if (7 <= zoomLevel && zoomLevel < 8) {
            returnValue = (float) 384/2;
        } else if (8 <= zoomLevel && zoomLevel < 9) {
            returnValue = (float) 192/2;
        } else if (9 <= zoomLevel && zoomLevel < 10) {
            returnValue = (float) 96 / 2;
        } else if (10 <= zoomLevel && zoomLevel < 11) {
            returnValue = (float) 48/2;
        } else if (11 <= zoomLevel && zoomLevel < 12) {
            returnValue = (float) 24/2;
        } else if (12 <= zoomLevel && zoomLevel < 13) {
            returnValue = (float) 12/2;
        } else if (13 <= zoomLevel && zoomLevel < 14) {
            returnValue = (float) 6/2;
        } else if (14 <= zoomLevel && zoomLevel < 15) {
            returnValue = (float) 3/2;
        } else if (15 <= zoomLevel && zoomLevel < 16) {
            returnValue = (float) 1.5/2;
        } else if (16 <= zoomLevel && zoomLevel < 17) {
            returnValue = (float) 0.75/2;
        } else if (17 <= zoomLevel && zoomLevel < 18) {
            returnValue = (float) 0.375/2;
        } else {
            returnValue = (float) 188/2;
        }

        return returnValue;
    }
}
