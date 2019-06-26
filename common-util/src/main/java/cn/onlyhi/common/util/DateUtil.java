package cn.onlyhi.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/12.
 */
public class DateUtil {
    private static final DateFormat DATE_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 根据日期字符串判断该日期是星期几
     *
     * @param str yyyy-MM-dd
     * @return week 1:星期日,2:星期一,3:星期二,4:星期三,5:星期四,6:星期五,7:星期六
     * @throws ParseException
     */
    public static int getWeek(String str) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DATE_FORMAT_YYYY_MM_DD.parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //第几周
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    /**
     * 根据日期字符串判断当月第几周
     *
     * @param str yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public static int getWeekOfMonth(String str) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DATE_FORMAT_YYYY_MM_DD.parse(str);
        Calendar calendar = Calendar.getInstance();
        //设置一周的第一天为星期一
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        //第几周
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        return weekOfMonth;
    }

    /*public static void main(String[] args) throws ParseException {
        String dataStr = "2018-03-30";
        int weekOfMonth = getWeekOfMonth(dataStr);
        int week = getWeek(dataStr);
        System.out.println(weekOfMonth);
        System.out.println(week);
    }*/

    /**
     * 根据年、月、月的周数获取一周的日期集合
     *
     * @param year
     * @param month
     * @param weekOfMonth
     * @return
     */
    public static List<String> getDayOfWeekList(int year, int month, int weekOfMonth) {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        //设置一周的第一天为星期一
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.WEEK_OF_MONTH, weekOfMonth);

        for (int i = 2; i <= 7; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i);
            dayList.add(DATE_FORMAT_YYYY_MM_DD.format(calendar.getTime()));
        }
        //设置星期天的日期
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        dayList.add(DATE_FORMAT_YYYY_MM_DD.format(calendar.getTime()));
        return dayList;
    }

    private static final int YESTERDY = -1;
    private static final int TODAY = 0;
    private static final int TOMORROW = 1;
    private static final int OTHER_DAY = 10000;

    /**
     * 判断日期
     *
     * @param date
     * @return -1:昨天   0:今天    1:明天    10000:其他
     */
    public static int judgmentDay(Date date) {
        Calendar pre = Calendar.getInstance();
        pre.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            switch (diffDay) {
                case YESTERDY:
                    return YESTERDY;
                case TODAY:
                    return TODAY;
                case TOMORROW:
                    return TOMORROW;
            }
        }
        return OTHER_DAY;
    }


    /**
     * 是否小于当天日期
     *
     * @param date
     * @return
     */
    public static boolean isLessTomorrow(Date date) {
        Calendar pre = Calendar.getInstance();
        pre.setTimeInMillis(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay >= 1) {
                return false;
            }
        }
        return true;
    }


    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("^(\\d{4}-\\d{2})$");

    /**
     * 检验集合中数据是否符合格式: yyyy-MM
     *
     * @param dateTimeList
     * @return
     */
    public static boolean validDateTime(List<String> dateTimeList) {
        if (dateTimeList != null) {
            for (String dateTime : dateTimeList) {
                if (!DATE_TIME_PATTERN.matcher(dateTime).matches()) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 判断是否是昨天
     *
     * @param timestamp
     * @return
     */
    public static boolean isYesterday(long timestamp) {
        Calendar c = Calendar.getInstance();
        clearCalendar(c, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
        c.add(Calendar.DAY_OF_MONTH, -1);
        long firstOfDay = c.getTimeInMillis(); // 昨天最早时间

        c.setTimeInMillis(timestamp);
        clearCalendar(c, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND); // 指定时间戳当天最早时间

        return firstOfDay == c.getTimeInMillis();
    }
    private static void clearCalendar(Calendar c, int... fields) {
        for (int f : fields) {
            c.set(f, 0);
        }
    }
}
