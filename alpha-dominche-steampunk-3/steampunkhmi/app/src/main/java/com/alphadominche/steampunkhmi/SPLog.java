package com.alphadominche.steampunkhmi;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import com.alphadominche.steampunkhmi.restclient.contentprocessor.ContentProcessor.ContentProcessorCallbacks;
import com.alphadominche.steampunkhmi.restclient.contentprocessor.DefaultContentProcessor;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class SPLog {
    private final static boolean LOGGING_ON = true; //set to false to kill all logging
    private final static boolean DEBUG_ON = true; //set to false to disable debug messages
    private final static boolean CREATE_DATE_STAMPS = true;

    //target
    public final static int REMOTE_SERVER = 0;
    public final static int LOCAL_FILE = 1;
    public final static int SYSTEM_OUT = 2;
    public final static int REMOTE_SERVER_FROM_SERVICE = 3;
    public final static String TARGET[] = {"REMOTE_SERVER", "LOCAL_FILE", "SYSTEM_OUT", "REMOTE_SERVER_FROM_SERVICE"};

    //severity
    public final static int DEBUG = 0;
    public final static int INFO = 1;
    public final static int WARNING = 2;
    public final static int ERROR = 3;
    public final static String SEVERITY[] = {"DEBUG", "INFO", "WARNING", "ERROR"};

    //types
    public final static int BREW = 0;
    public final static int APPLICATION = 1;
    public final static int MACHINE = 2;
    public final static int GENERAL = 3;
    public final static String TYPES[] = {"BREW", "APPLICATION", "MACHINE", "GENERAL"};

    public static int sWhereToSendLog = SYSTEM_OUT;

    private static boolean sLogSentForCurrentSettings = false;
    private static String sRecipeUuid;
    private static int sCrucibleIndex;
    private static int sSeverity;
    private static int sType;
    private static Context sContext;
    private static ContentProcessorCallbacks sCallBacks;

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSSS");

    @SuppressWarnings({"unused", "all"})
    public static void send(String msg) {

        if (!LOGGING_ON) return;
        if (!sLogSentForCurrentSettings && !DEBUG_ON && sSeverity == DEBUG) return;

        if (sWhereToSendLog == REMOTE_SERVER_FROM_SERVICE) {
            if (sContext == null) return;

            DefaultContentProcessor.getInstance(sContext, sCallBacks).createLog(
                    SteampunkUtils.getRequestId(sContext),
                    SteampunkUtils.getMachineId(sContext),
                    SteampunkUtils.getCurrentUserId(sContext),
                    SteampunkUtils.getCurrentDateString(),
                    sRecipeUuid,
                    sCrucibleIndex,
                    sSeverity,
                    sType,
                    msg);
        } else if (sWhereToSendLog == REMOTE_SERVER) {
            if (sContext == null) return;
            DefaultPersistenceServiceHelper.getInstance(sContext).createLog(null, sCrucibleIndex, sSeverity, sType, msg);
        } else if (sWhereToSendLog == LOCAL_FILE) {

        } else if (sWhereToSendLog == SYSTEM_OUT) {
            System.out.println("SPLog" + formDate() + ": " + msg + formExtras());
        }
        sLogSentForCurrentSettings = true;
    }

    public static void send(int severity, String msg) {
        sWhereToSendLog = SYSTEM_OUT;
        sSeverity = severity;
        send(msg);
        sLogSentForCurrentSettings = false;
    }

    public static void send(Context context, String recipeUuid, int crucibleIndex, int severity, int type, String msg) {
        sWhereToSendLog = REMOTE_SERVER;
        if (context != null) {
            sContext = context;
        }
        sRecipeUuid = recipeUuid;
        sCrucibleIndex = crucibleIndex;
        sSeverity = severity;
        sType = type;
        sLogSentForCurrentSettings = false;
        send(msg);
    }

    public static void fromService(Context context, ContentProcessorCallbacks cpcb, String recipeUuid, int crucibleIndex, int severity, int type, String msg) {
        sWhereToSendLog = REMOTE_SERVER_FROM_SERVICE;
        if (context != null) {
            sContext = context;
        }
        sRecipeUuid = recipeUuid;
        sCrucibleIndex = crucibleIndex;
        sSeverity = severity;
        sType = type;
        sCallBacks = cpcb;
        sLogSentForCurrentSettings = false;
        send(msg);

    }

    public static void debug(String msg) {
        sSeverity = DEBUG;
        sRecipeUuid = "_";
        sCrucibleIndex = -1;
        sType = GENERAL;
        sWhereToSendLog = SYSTEM_OUT;
        sLogSentForCurrentSettings = false;
        send(msg);
    }

    public static void debug(int target, String msg) {
        sSeverity = DEBUG;
        sRecipeUuid = "_";
        sCrucibleIndex = -1;
        sType = GENERAL;
        sWhereToSendLog = target;
        sLogSentForCurrentSettings = false;
        send(msg);
    }

    private static String formExtras() {
        return ((!sLogSentForCurrentSettings) ? (" " + SEVERITY[sSeverity] + " " + TYPES[sType]) : (""));
    }

    private static String formDate() {
        if (!CREATE_DATE_STAMPS) return "";
        return "(" + sFormatter.format(new Date()) + ")";
    }
}
