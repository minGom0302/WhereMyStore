package com.example.wheremystore.item;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Sp {
    SharedPreferences sp;
    SharedPreferences.Editor sp_e;
    /*
    SharedPreferences 는 Default 저장소를 사용할 수 있고 내가 저장소를 지정해서 사용할 수 있다.
     - Default 로 사용하는 방법
      1) SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
      2) SharedPreferences.Editor sp_e = sp.edit();
      3) sp.get****(key, defaultValues); , sp_e.set****(key, values); , sp_e.commit();  >  sp_e 를 통해 값을 작성한 후 반드시 commit 을 해줘야 한다.

     - 특정 저장소로 사용하는 방법
      1) SharedPreferences sp = getSharedPreferences("storageName", Context.MODE_PRIVATE);
       > AppCompatActivity 을 참조면 위처럼 바로 사용할 수 있고, 참조 안했으면 Activity 혹은 Context 를 선언해줘야해한다.
         sp = activity.getSharedPreferences("name", Context.MODE_PRIVATE);
       > storageName 이 같아야 공유하면서 값을 불러오고 저장할 수 있다. SQL Table 개념이라과 생각하면 된다.
      2) SharedPreferences.Editor sp_e = sp.edit();
      3) sp.get****(key, defaultValues); , sp_e.set****(key, values); , sp_e.commit();  >  sp_e 를 통해 값을 작성한 후 반드시 commit 을 해줘야 한다.

     - 사용법 (첫번째와 두번째 동일)
      1) sp_e.setString("strA", "A"); sp_e.getBoolean("booleanB", true); sp_e.commit();
      2) String a = sp.getString("strA", null);  boolean b = sp.getBoolean("booleanB", false);
     */


    /* Constructor 영역 */
    public Sp(Context context) {
        sp = context.getSharedPreferences("whereMyStore", Context.MODE_PRIVATE);
        sp_e = sp.edit();
    }


    /* Setter 영역 */
    public void setPlatformLogin(boolean platformLogin) { sp_e.putBoolean("platformLogin", platformLogin).commit(); }
    public void setPermissionCheck(boolean permissionCheck) { sp_e.putBoolean("permissionCheck", permissionCheck).commit(); }
    public void setUserPlatformId(String userPlatformId) { sp_e.putString("userPlatformId", userPlatformId).commit(); }
    public void setUserName(String userName) { sp_e.putString("userName", userName); }
    public void setUserId(String userId) { sp_e.putString("userId", userId).commit(); }
    public void setUserPw(String userPw) { sp_e.putString("userPw", userPw).commit(); }
    public void setUserNickname(String userNickname) { sp_e.putString("userNickname", userNickname).commit(); }
    public void setUserPhone(String userPhone) { sp_e.putString("userPhone", userPhone).commit(); }
    public void setUserBirth(String userBirth) { sp_e.putString("userBirth", userBirth).commit(); }
    public void setUserSex(String userSex) { sp_e.putString("userSex", userSex).commit(); }
    public void setOption(String option) { sp_e.putString("option", option).commit(); }
    public void setAutoLogin(boolean autoLogin) { sp_e.putBoolean("autoLogin", autoLogin).commit(); }
    public void setIdSave(boolean idSave) { sp_e.putBoolean("idSave", idSave).commit(); }


    /* Getter 영역 */
    public boolean getPlatformLogin() { return sp.getBoolean("platformLogin", false); }
    public boolean getPermissionCheck() { return sp.getBoolean("permissionCheck", false); }
    public String getUserPlatformId() { return sp.getString("userPlatformId", ""); }
    public String getUserName() { return sp.getString("userName", ""); }
    public String getUserId() { return sp.getString("userId", ""); }
    public String getUserPw() { return sp.getString("userPw", ""); }
    public String getUserNickname() { return sp.getString("userNickname", ""); }
    public String getUserPhone() { return sp.getString("userPhone", ""); }
    public String getUserBirth() { return sp.getString("userBirth", ""); }
    public String getUserSex() { return sp.getString("userSex", ""); }
    public String getOption() { return sp.getString("option", "1111111"); }
    public boolean getAutoLogin() { return sp.getBoolean("autoLogin", false); }
    public boolean getIdSave() { return sp.getBoolean("idSave", false); }
}
