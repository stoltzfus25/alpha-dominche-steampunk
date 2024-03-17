package com.alphadominche.steampunkhmi;

public class UID {
    private static boolean sPositive = true;
    private static int sNext = 0;

    public static void goNegative() {
        sPositive = false;
        sNext = -1;
    }

    public static void goPositive() {
        sPositive = false;
        sNext = 0;
    }

    private static int getUniqueId() {
        if (sPositive) {
            return sNext++;
        } else {
            return sNext--;
        }
    }

    private int mId;

    UID() {
        mId = getUniqueId();
    }

    private int getValue() {
        return mId;
    }

    public boolean equals(Object o) {
        if (!(o instanceof UID)) {
            return false;
        }

        UID other = (UID) o;
        return other.getValue() == mId;
    }

    public String toString() {
        return "" + mId;
    }
}
