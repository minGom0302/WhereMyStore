package com.example.wheremystore.application;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permission {
    static final int PERMISSIONS_REQUEST = 1000;

    public static void checkPermission(Activity activity) {
        /*
        Permission 관련 내용
         - Android 11 (API 30) 이상 에서는 특정 권한을 두 번 거절하는 경우 영구적으로 거절한 것으로 판단하여 세번째부터는 권한 요청 박스가 안뜬다.
         - Android 11 (API 30) 이상 에서는 WRITE_EXTERNAL_STORAGE 요청을 안해도 된다.
         - Android 11 (API 30) 이상 에서는 MediaStore 를 통해 다른 앱 파일에 접근할 때에만 READ_EXTERNAL_STORAGE 권한이 필요하다.
           그 외 개별 앱 공간, 다운로드 파일, 갤러리 등 getExternalFilesDir(), Storage Access Storage 를 통해 접근할 때에는 권한이 필요 없다.
        */

        // 권한 ID를 가져옵니다.
        /*int permission01 = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permission02 = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission03 = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission04 = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission05 = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        // 권한이 열려있는지 확인하여 없으면 권한을 요청합니다.
        if(permission01 == PackageManager.PERMISSION_DENIED || permission02 == PackageManager.PERMISSION_DENIED || permission03 == PackageManager.PERMISSION_DENIED
                || permission04 == PackageManager.PERMISSION_DENIED  || permission05 == PackageManager.PERMISSION_DENIED ) {
            // 권한을 거절한 후 다시 실행할 때 재요청하는 함수를 실행합니다. > 아래와 다르게 다시 보지 않기가 표시됩니다.
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 아래 멘트는 굳이 안띄워도 될 거 같아 주석처리
                // Toast.makeText(activity, "앱 실행을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(activity, new String[] {
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                }, PERMISSIONS_REQUEST);
            } else {
                // 처음 앱을 실행할 때 권한을 요청합니다.
                ActivityCompat.requestPermissions(activity, new String[] {
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                }, PERMISSIONS_REQUEST);
            }
        }*/

        // 권한 ID를 가져옵니다.
        int permission01 = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int permission04 = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission05 = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        // 권한이 열려있는지 확인하여 없으면 권한을 요청합니다.
        if(permission01 == PackageManager.PERMISSION_DENIED
                || permission04 == PackageManager.PERMISSION_DENIED  || permission05 == PackageManager.PERMISSION_DENIED ) {
            // 권한을 거절한 후 다시 실행할 때 재요청하는 함수를 실행합니다. > 아래와 다르게 다시 보지 않기가 표시됩니다.
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                /* 아래 멘트는 굳이 안띄워도 될 거 같아 주석처리
                Toast.makeText(activity, "앱 실행을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_SHORT).show(); */
                ActivityCompat.requestPermissions(activity, new String[] {
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, PERMISSIONS_REQUEST);
            } else {
                // 처음 앱을 실행할 때 권한을 요청합니다.
                ActivityCompat.requestPermissions(activity, new String[] {
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, PERMISSIONS_REQUEST);
            }
        }
    }
}
