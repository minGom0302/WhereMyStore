package com.example.wheremystore.dto;

import com.google.gson.annotations.SerializedName;

public class UserInfoDTO {
    @SerializedName("platformId")
    String platformId; // 카카오톡 회원 번호 혹은 구글 회원 번호로 입력
    @SerializedName("userName")
    String userName;
    @SerializedName("userId")
    String userId; // 카카오톡일 경우 이메일이 아이디로 됨
    @SerializedName("userPw")
    String userPw;
    @SerializedName("userPwAgain")
    String userPwAgain;
    @SerializedName("userNickname")
    String userNickname;
    @SerializedName("userPhone")
    String userPhone;
    @SerializedName("userBirth")
    String userBirth;
    @SerializedName("userSex")
    String userSex; // 0 - 남자 , 1 - 여자 , 9 - 선택안함
    @SerializedName("userAgree01")
    String userAgree01; // 0 - 체크 안함 , 1 - 체크
    @SerializedName("userAgree02")
    String userAgree02; // 0 - 체크 안함 , 1 - 체크


    /* Constructor */
    public UserInfoDTO(String platformId, String userName, String userId, String userPw, String userPwAgain, String userNickname, String userPhone, String userBirth, String userSex, String userAgree01, String userAgree02) {
        this.platformId = platformId;
        this.userName = userName;
        this.userId = userId;
        this.userPw = userPw;
        this.userPwAgain = userPwAgain;
        this.userNickname = userNickname;
        this.userPhone = userPhone;
        this.userBirth = userBirth;
        this.userSex = userSex;
        this.userAgree01 = userAgree01;
        this.userAgree02 = userAgree02;
    }



    /* Setter & Getter */
    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserPwAgain() {
        return userPwAgain;
    }

    public void setUserPwAgain(String userPwAgain) {
        this.userPwAgain = userPwAgain;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserAgree01() {
        return userAgree01;
    }

    public void setUserAgree01(String userAgree01) {
        this.userAgree01 = userAgree01;
    }

    public String getUserAgree02() {
        return userAgree02;
    }

    public void setUserAgree02(String userAgree02) {
        this.userAgree02 = userAgree02;
    }
}
