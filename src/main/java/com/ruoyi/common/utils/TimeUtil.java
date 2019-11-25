package com.ruoyi.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
    /***
     * 两时间差值判断，大于则进行警戒判断，否则不进行
     */
    public static final long DEL_TIME = 3600000L;

    /**
     * 获取两个时间差 3600000 为一个小时
     *
     * @param now
     * @param date
     */
    public static long getTimeDifference(Date now, Date date) {
        //格式日期格式，在此我用的是"2018-01-24 19:49:50"这种格式
        //可以更改为自己使用的格式，例如：yyyy/MM/dd HH:mm:ss 。。。
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long l = now.getTime() - date.getTime();       //获取时间差
            return l;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1L;
    }

    /**
     * 获取当前系统的小时
     *
     * @return
     */
    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static Date getSystemDate() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format1.parse(format.format(new Date()));
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getEndHour(Date date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:59:59");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format1.parse(format.format(date));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算开始结束时间小时差值数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 结果
     */
    public static float getDateDel(Date startDate, Date endDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long diff = endDate.getTime() - startDate.getTime();
        long day = diff / nd;
        long hour = diff % nd / nh;
        long min = diff % nd % nh / nm;
        float t = (float) day * 24;
        float m = (float) min / 60;
        return t + hour + m;
    }

    /**
     * 获取上一个小时的最后一秒钟的时间 <br>
     * 显示格式为 Thu XXX XX XX:59:59 CST XXXX
     *
     * @return
     */
    public static Date getLastSecondOfLastHour() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, 0); //设置分钟为0,
        c.set(Calendar.SECOND, -1); // 秒钟减1
        c.set(Calendar.MILLISECOND, 0); //毫秒值为0
        return c.getTime();
    }

    /**
     * 获取上一个小时的整点的时间 <br>
     * 显示格式为 Thu XXX XX XX:00:00 CST XXXX
     *
     * @return
     */
    public static Date getLastHour() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, -1);
        c.set(Calendar.MINUTE, 0); //设置分钟为0,
        c.set(Calendar.SECOND, 0); // 秒钟为0
        c.set(Calendar.MILLISECOND, 0); //毫秒值为0
        return c.getTime();
    }

    /**
     * 获取当前年月日
     * Thu XXX XX 00:00:00 CST XXXX
     *
     * @return 结果
     */
    public static Date getNYR() {
        Calendar cal = Calendar.getInstance();
        cal.get(Calendar.DAY_OF_MONTH);
        cal.get(Calendar.MONTH);
        cal.get(Calendar.YEAR);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0); //设置分钟为0,
        cal.set(Calendar.SECOND, 0); // 秒钟为0
        cal.set(Calendar.MILLISECOND, 0); //毫秒值为0
        return cal.getTime();
    }

    public static void main(String[] args) throws Exception {
        String startTime = "2018-10-20 08:30:00";
        String endTime = "2019-10-25 08:45:00";
        Date dateTime = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime);
        Date date = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime);
        float dateDel = getDateDel(dateTime, date);
        System.out.println(dateDel);
    }


}
