package studio.olimpik.moyritm.namaz;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
public class MainActivity extends Activity {
 @Override public void onCreate(Bundle b){super.onCreate(b);
 TextView v=new TextView(this);v.setText("МОЙ РИТМ · Намазы\n\nДобавь виджет на главный экран:\nЗажми пустое место → Виджеты → МОЙ РИТМ · Намазы.\n\nГород: Астрахань\nМетод: Muslim World League, Аср: шафиитский.\n\nДля ежедневного обновления нужен интернет. Если связи нет, сохраняется последнее полученное расписание.\n\nВремя может отличаться от расписания твоей мечети.");v.setTextColor(Color.WHITE);v.setTextSize(18);v.setGravity(Gravity.CENTER);v.setPadding(30,30,30,30);v.setBackgroundColor(Color.rgb(20,23,25));setContentView(v,new ViewGroup.LayoutParams(-1,-1));
 PrayerWidget.updateAll(this);
 }
}