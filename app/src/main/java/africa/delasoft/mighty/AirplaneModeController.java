package africa.delasoft.mighty;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;

import java.lang.reflect.Field;

public class AirplaneModeController {

    // Method to check if airplane mode is enabled
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

    }

    // Method to turn on/off airplane mode
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setAirplaneMode(Context context, boolean enable) {
        // Requesting WRITE_SETTINGS permission in the manifest is required for changing airplane mode
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M
                || Settings.System.canWrite(context)) {

            // Use reflection to access Settings.Global.AIRPLANE_MODE_ON
            try {
                // toggle airplane mode

                // toggle airplane mode
                Settings.Global.putInt(context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, enable ? 0 : 1);

                // Post an intent to reload
                Intent changeMode = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                changeMode.putExtra("state", !enable);
                context.sendBroadcast(changeMode);


                Log.e("tango",enable+" T");



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





   // adb shell pm grant africa.delasoft.mighty android.permission.WRITE_SECURE_SETTINGS



}

