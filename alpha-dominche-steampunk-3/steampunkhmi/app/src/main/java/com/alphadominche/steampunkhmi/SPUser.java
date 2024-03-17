package com.alphadominche.steampunkhmi;

public class SPUser {
    public static String ADMIN = "ADMIN";
    public static String ROASTER = "ROASTER";
    public static String BARISTA = "BARISTA";

    private long mId;
    private String mName;
    private String mType;

    public SPUser(long id, String name, String type) {
        mId = id;
        mName = name;
        mType = type;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SPUser)) {
            return false;
        }

        SPUser other = (SPUser) o;
        if (mId == other.getId() && mName.equals(other.getName()) && mType.equals(other.getType())) {
            return true;
        } else {
            return false;
        }
    }

    public void save() {
    }

    public void delete() {
    }
}
