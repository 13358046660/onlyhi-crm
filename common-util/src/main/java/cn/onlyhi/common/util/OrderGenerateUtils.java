package cn.onlyhi.common.util;

import org.joda.time.DateTime;

/**
 * 订单号生成规则
 *
 * @author csy
 * @date 2017年3月10日
 */
public class OrderGenerateUtils {

    private static Object locker = new Object();

    private static int sn = 100000;

    private static int childInitNo = 1;

    // 防止创建类的实例
    private OrderGenerateUtils() {
    }

    public static String generateOrderNo() {
        synchronized (locker) {
            if (sn == 999)
                sn = 100;
            else
                sn++;
            return DateTime.now().toString("yyyyMMddHHmmss") + String.valueOf(sn);
        }
    }

    public static String generateChildOrderNo(String parentOrderNo) {
        synchronized (locker) {
            if (childInitNo == 99)
                childInitNo = 1;
            else
                childInitNo++;
            return "0" + parentOrderNo + String.valueOf(childInitNo);
        }
    }

/*	    public static void main(String[] args) {
	    	System.out.println(OrderGenerateUtils.generateOrderNo());
		}*/
}
