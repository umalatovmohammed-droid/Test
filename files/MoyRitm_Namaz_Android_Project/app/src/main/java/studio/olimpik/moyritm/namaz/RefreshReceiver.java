package studio.olimpik.moyritm.namaz;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class RefreshReceiver extends BroadcastReceiver{
 @Override public void onReceive(Context context,Intent intent){PrayerWidget.updateAll(context);}
}