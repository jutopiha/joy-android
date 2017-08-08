package org.androidtown.newtiggle.object;

/**
 * Created by Juha on 2017-06-17.
 */

public class User {
    //get mySQL Database info
    private String userId;
    private String name;
    private int birth;
    private String gender;
    private String profilePicture;
    private String mainBank;
    private boolean onAutoParse;
    private boolean onAutoAlarm;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirth() {
        return birth;
    }

    public void setBirth(int birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getMainBank() {
        return mainBank;
    }

    public void setMainBank(String mainBank) {
        this.mainBank = mainBank;
    }

    public boolean isOnAutoParse() {
        return onAutoParse;
    }

    public void setOnAutoParse(boolean onAutoParse) {
        this.onAutoParse = onAutoParse;
    }

    public boolean isOnAutoAlarm() {
        return onAutoAlarm;
    }

    public void setOnAutoAlarm(boolean onAutoAlarm) {
        this.onAutoAlarm = onAutoAlarm;
    }

    @Override
    public String toString() {
        return "FirebaseUserData{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", birth=" + birth +
                ", gender='" + gender + '\'' +
                ", profilePicture=" + profilePicture +
                ", mainBank=" + mainBank +
                ", onAutoParse=" + onAutoParse +
                ", onAutoAlarm=" + onAutoAlarm +
                '}';
    }

}
