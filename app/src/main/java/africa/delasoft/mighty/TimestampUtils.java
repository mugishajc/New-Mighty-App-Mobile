package africa.delasoft.mighty;

import android.content.Context;
import android.content.SharedPreferences;

public class TimestampUtils {
    public static void saveLastTimestamp(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, System.currentTimeMillis());
        editor.apply();
    }
}
