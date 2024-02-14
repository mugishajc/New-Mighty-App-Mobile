package africa.delasoft.mighty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("tango", "Alarm received at: " + new Date().toString());
        makeUSSDCall(context);
    }

    private void makeUSSDCall(Context context) {
        // Replace "your_USSD_code" with the actual USSD code you want to dial
        String ussdCode = "*131";

        Intent dialerIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode + Uri.encode("#")));
        dialerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialerIntent);


    }
}

