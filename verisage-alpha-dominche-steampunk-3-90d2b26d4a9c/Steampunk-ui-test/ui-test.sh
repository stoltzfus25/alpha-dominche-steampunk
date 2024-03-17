if [ ! -e ../SteampunkHMI/bin/Steampunk.apk ]
    then
        echo "You need to build the SteampunkHMI project first in order to install Steampunk.apk"
        exit
fi

adb shell am force-stop com.alphadominche.steampunkhmi
adb shell pm uninstall com.alphadominche.steampunkhmi
adb install ../SteampunkHMI/bin/Steampunk.apk
ant build
adb push bin/Steampunk-ui-test.jar /data/local/tmp
adb shell uiautomator runtest Steampunk-ui-test.jar -c com.alphadominche.steampunkhmi.uitest.SideBarMenuScreensTest