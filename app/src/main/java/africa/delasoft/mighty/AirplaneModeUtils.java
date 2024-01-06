package africa.delasoft.mighty;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class AirplaneModeUtils {

    // Function to enable (turn on) Airplane Mode
    public static void enableAirplaneMode(Context context) {
        // Set airplane mode to ON
        Settings.Global.putInt(
                context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 1
        );

        // Post an intent to inform the system that the airplane mode has changed
        // This will trigger the airplane mode change immediately
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", true);
        context.sendBroadcast(intent);
    }

    // Function to disable (turn off) Airplane Mode
    public static void disableAirplaneMode(Context context) {
        // Set airplane mode to OFF
        Settings.Global.putInt(
                context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0
        );

        // Post an intent to inform the system that the airplane mode has changed
        // This will trigger the airplane mode change immediately
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", false);
        context.sendBroadcast(intent);
    }


    // Function to check and request the WRITE_SETTINGS permission
    public static void checkAndRequestWriteSettingsPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

}

