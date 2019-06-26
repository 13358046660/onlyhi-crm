package cn.onlyhi.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.onlyhi.common.constants.Constants.DRAWMODE_01;
import static cn.onlyhi.common.constants.Constants.DRAWMODE_02;
import static cn.onlyhi.common.constants.Constants.DRAWMODE_03;
import static cn.onlyhi.common.constants.Constants.DRAWMODE_04;
import static cn.onlyhi.common.constants.Constants.DRAWMODE_05;
import static cn.onlyhi.common.constants.Constants.DRAWMODE_07;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/10/26.
 */
public class TrackUtil {
    //wrapTrackData方法1
    public static String[] wrapTrackData(String x, String y, String time, String color, String brushSize, String drawMode, String boardNo, String textContent) {
        return wrapTrackData(x, y, time, color, brushSize, drawMode2Eraser(drawMode), drawMode2Polygon(drawMode), "0", drawMode2Text(drawMode), boardNo, null, null, textContent, null);
    }
    //wrapTrackData方法2
    public static String[] wrapTrackData(String x, String y, String time, String color, String brushSize, String drawMode, String boardNo, String l, String w) {
        return wrapTrackData(x, y, time, color, brushSize, drawMode2Eraser(drawMode), drawMode2Polygon(drawMode), "0", drawMode2Text(drawMode), boardNo, l, w, null, null);
    }
    //wrapTrackData方法3
    public static String[] wrapTrackData(String time, String clearBoard, String boardNo, String clearAll) {
        return wrapTrackData(null, null, time, null, null, "0", "0", clearBoard, "0", boardNo, null, null, null, clearAll);
    }
    //wrapTrackData方法4
    public static String[] wrapTrackData(String time, String clearBoard, String boardNo) {
        return wrapTrackData(null, null, time, null, null, "0", "0", clearBoard, "0", boardNo);
    }
    //wrapTrackData方法5
    public static String[] wrapTrackData(String x, String y, String time, String color, String brushSize, String drawMode, String boardNo) {
        return wrapTrackData(x, y, time, color, brushSize, drawMode2Eraser(drawMode), drawMode2Polygon(drawMode), "0", drawMode2Text(drawMode), boardNo);
    }
    //wrapTrackData方法6
    public static String[] wrapTrackData(String time, String boardNo) {
        return wrapTrackData(null, null, time, null, null, "0", "0", "0", "0", boardNo);
    }
    //wrapTrackData方法7
    public static String[] wrapTrackData(String time, String clearBoard, String boardNo, String rectangularSolid, String roundSolid) {
        return wrapTrackData4(null, null, time, null, null, "0", "0", clearBoard, "0", boardNo,rectangularSolid,roundSolid);
    }
    //new2 保留<wrapTrackData方法2>，新增此方法用于返回页面是否实心画图参数
    public static String[] wrapTrackDataNew2(String x, String y, String time, String color, String brushSize, String drawMode, String boardNo, String l, String w, String rectangularSolid, String roundSolid) {
        return wrapTrackData2(x, y, time, color, brushSize, drawMode2Eraser(drawMode), drawMode2Polygon(drawMode), "0", drawMode2Text(drawMode), boardNo, l, w, null, null, rectangularSolid, roundSolid);
    }
    /**
     * 修改此方法用于返回页面是否实心画图参数指定null，保持页面收到数组个数一样
     *
     * @param x          X轴坐标
     * @param y          Y轴坐标
     * @param time       时间 ms
     * @param color      颜色 {0,0,0}或文字的颜色
     * @param brushSize  画笔大小或文字的大小
     * @param eraser     橡皮（0：未使用，1：使用）
     * @param polygon    多边形（0：无，1：圆，2：方）
     * @param clearBoard 清空白板（0：未清空，1：清空）
     * @param text       文字（0：无，1：打字）
     * @param boardNo    h5白板编号
     * @return
     */
    private static String[] wrapTrackData(String x, String y, String time, String color, String brushSize, String eraser, String polygon, String clearBoard, String text, String boardNo) {
        return wrapTrackData(x, y, time, color, brushSize, eraser, polygon, clearBoard, text, boardNo, null, null, null, null);
    }
    private static String[] wrapTrackData4(String x, String y, String time, String color, String brushSize, String eraser, String polygon, String clearBoard, String text, String boardNo, String rectangularSolid, String roundSolid) {
        return wrapTrackData2(x, y, time, color, brushSize, eraser, polygon, clearBoard, text, boardNo, null, null, null, null,rectangularSolid,roundSolid);
    }

    /**
     * @param x           X轴坐标
     * @param y           Y轴坐标
     * @param time        时间 ms
     * @param color       颜色 {0,0,0} 或文字的颜色
     * @param brushSize   画笔大小  或文字的大小
     * @param eraser      橡皮（0：未使用，1：使用）
     * @param polygon     多边形（0：无，1：圆，2：方）
     * @param clearBoard  清空白板（0：未清空，1：清空）
     * @param text        文字（0：无，1：打字）
     * @param boardNo     h5白板编号
     * @param l           矩形的长或椭圆的长半径
     * @param w           矩形的宽或椭圆的短半径
     * @param textContent 文字内容
     * @param clearAll    是否清除全部白板信息（0：不清，1：全清）
     * @return
     */
    private static String[] wrapTrackData(String x, String y, String time, String color, String brushSize, String eraser, String polygon, String clearBoard, String text, String boardNo, String l, String w, String textContent, String clearAll) {
        String[] param = new String[15];
        if (StringUtils.isNotEmpty(x)) {
            param[0] = x;
        }
        if (StringUtils.isNotEmpty(y)) {
            param[1] = y;
        }
        if (StringUtils.isNotEmpty(time)) {
            param[2] = time;
        }
        if (StringUtils.isNotEmpty(color)) {
            param[3] = color;
        }
        if (StringUtils.isNotEmpty(brushSize)) {
            param[4] = brushSize;
        }
        if (StringUtils.isNotEmpty(eraser)) {
            param[5] = eraser;
        }
        if (StringUtils.isNotEmpty(polygon)) {
            param[6] = polygon;
        }
        if (StringUtils.isNotEmpty(clearBoard)) {
            param[7] = clearBoard;
        }
        if (StringUtils.isNotEmpty(text)) {
            param[8] = text;
        }
        if (StringUtils.isNotEmpty(boardNo)) {
            param[9] = boardNo;
        }
        if (StringUtils.isNotEmpty(l)) {
            param[10] = l;
        }
        if (StringUtils.isNotEmpty(w)) {
            param[11] = w;
        }
        if (textContent != null) {
            param[12] = textContent;
        }
        if (clearAll != null) {
            param[13] = clearAll;
        }
        param[14] = null;
        return param;
    }

    //新增此方法，返回页面两个属性，实心圆，实心矩形
    private static String[] wrapTrackData2(String x, String y, String time, String color, String brushSize, String eraser, String polygon, String clearBoard, String text, String boardNo, String l, String w, String textContent, String clearAll, String rectangularSolid, String roundSolid) {
        String[] param = new String[15];
        if (StringUtils.isNotEmpty(x)) {
            param[0] = x;
        }
        if (StringUtils.isNotEmpty(y)) {
            param[1] = y;
        }
        if (StringUtils.isNotEmpty(time)) {
            param[2] = time;
        }
        if (StringUtils.isNotEmpty(color)) {
            param[3] = color;
        }
        if (StringUtils.isNotEmpty(brushSize)) {
            param[4] = brushSize;
        }
        if (StringUtils.isNotEmpty(eraser)) {
            param[5] = eraser;
        }
        if (StringUtils.isNotEmpty(polygon)) {
            param[6] = polygon;
        }
        if (StringUtils.isNotEmpty(clearBoard)) {
            param[7] = clearBoard;
        }
        if (StringUtils.isNotEmpty(text)) {
            param[8] = text;
        }
        if (StringUtils.isNotEmpty(boardNo)) {
            param[9] = boardNo;
        }
        if (StringUtils.isNotEmpty(l)) {
            param[10] = l;
        }
        if (StringUtils.isNotEmpty(w)) {
            param[11] = w;
        }
        if (textContent != null) {
            param[12] = textContent;
        }
        if (clearAll != null) {
            param[13] = clearAll;
        }
        //0是空心，1是实心
        if (rectangularSolid =="true" || roundSolid =="true") {
            param[14] = "1";
        } else {
            param[14] = "0";
        }
        return param;
    }

    /**
     * drawMode表示是否使用文字
     *
     * @param drawMode
     * @return
     */
    private static String drawMode2Text(String drawMode) {
        String text = null;
        if (DRAWMODE_05.equals(drawMode)) {
            text = "1";
        } else {
            text = "0";
        }
        return text;
    }

    /**
     * drawMode表示是否使用橡皮
     *
     * @param drawMode
     * @return
     */
    private static String drawMode2Eraser(String drawMode) {
        String eraser = null;
        if (DRAWMODE_07.equals(drawMode)) {
            eraser = "1";
        } else {
            eraser = "0";
        }
        return eraser;
    }

    /**
     * drawMode表示多边形值
     *
     * @param drawMode
     * @return
     */
    private static String drawMode2Polygon(String drawMode) {
        String polygon = null;
        if (DRAWMODE_03.equals(drawMode)) { //画矩形
            polygon = "2";
        } else if (DRAWMODE_04.equals(drawMode)) { //画圆
            polygon = "1";
        } else if (DRAWMODE_01.equals(drawMode) || DRAWMODE_02.equals(drawMode)) {    //画点和线
            polygon = "3";
        } else {
            polygon = "0";
        }
        return polygon;
    }
}
