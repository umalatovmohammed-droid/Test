package studio.olimpik.moyritm.namaz;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
public class PrayerWidget extends AppWidgetProvider{
 private static final String[] KEYS={"Fajr","Dhuhr","Asr","Maghrib","Isha"};
 private static final int[] IDS={R.id.time1,R.id.time2,R.id.time3,R.id.time4,R.id.time5};
 private static final TimeZone TZ=TimeZone.getTimeZone("Europe/Astrakhan");
 private static String today(){SimpleDateFormat f=new SimpleDateFormat("dd-MM-yyyy",Locale.US);f.setTimeZone(TZ);return f.format(new Date());}
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){updateAll(c);}
 @Override public void onEnabled(Context c){updateAll(c);}
 public static void updateAll(Context c){
   Context app=c.getApplicationContext();render(app);
   Executors.newSingleThreadExecutor().execute(()->{
     HttpURLConnection conn=null;
     try{
       String date=today();
       URL url=new URL("https://api.aladhan.com/v1/timingsByCity/"+date+"?city=Astrakhan&country=Russia&method=3&school=0");
       conn=(HttpURLConnection)url.openConnection();conn.setConnectTimeout(12000);conn.setReadTimeout(12000);
       try(InputStream stream=conn.getInputStream();ByteArrayOutputStream buf=new ByteArrayOutputStream()){
         byte[] bytes=new byte[4096];int n;while((n=stream.read(bytes))!=-1)buf.write(bytes,0,n);
         JSONObject timings=new JSONObject(buf.toString("UTF-8")).getJSONObject("data").getJSONObject("timings");
         SharedPreferences.Editor edit=app.getSharedPreferences("prayers",0).edit();
         for(String key:KEYS){String value=timings.getString(key);if(value.length()<5)throw new Exception("Invalid time");edit.putString(key,value.substring(0,5));}
         edit.putString("date",date);edit.apply();
       }
     }catch(Exception ignored){}finally{if(conn!=null)conn.disconnect();}
     new Handler(Looper.getMainLooper()).post(()->render(app));
   });
 }
 private static void render(Context c){
   AppWidgetManager m=AppWidgetManager.getInstance(c);
   int[] ids=m.getAppWidgetIds(new ComponentName(c,PrayerWidget.class));
   SharedPreferences p=c.getSharedPreferences("prayers",0);
   boolean fresh=today().equals(p.getString("date",""));
   for(int id:ids){
     RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
     for(int i=0;i<5;i++)v.setTextViewText(IDS[i],p.getString(KEYS[i],"--:--"));
     v.setTextViewText(R.id.status,fresh?"Астрахань · "+today().replace('-','.')+" · шафиитский Аср":"Нет свежих данных · показано последнее расписание");
     Intent intent=new Intent(c,MainActivity.class);
     PendingIntent pi=PendingIntent.getActivity(c,0,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
     v.setOnClickPendingIntent(R.id.location,pi);
     m.updateAppWidget(id,v);
   }
 }
}