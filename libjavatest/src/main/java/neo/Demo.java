package neo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by neo on 17/12/1.
 */

public class Demo {
    public static void main(String[] args) {
        try {
            //获取当前日期
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = sf.format(date);
            System.out.println(nowDate);
            //通过日历获取下一天日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(sf.parse(nowDate));
            cal.add(Calendar.DAY_OF_YEAR, +1);
            String nextDate_1 = sf.format(cal.getTime());
            System.out.println(nextDate_1);
            //通过秒获取下一天日期
            long time = (date.getTime() / 1000) + 60 * 60 * 24;//秒
            date.setTime(time * 1000);//毫秒
            String nextDate_2 = sf.format(date).toString();
            System.out.println(nextDate_2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(Integer.parseInt("9:00-17:00".substring(0,2)));
        String[] split = "09:00-17:00".split(":");
        System.out.println(Integer.parseInt(split[0]));
    }
}
