package cn.onlyhi.client.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author wqz
 * <p>
 * Created by wqz on 2017/10/26.
 */
public class TrackData implements Serializable {
    private static final long serialVersionUID = 4816472291707649666L;
    private String voiceUrl;
    private String flacVoiceUrl;
    private int duration;
    private int pages;
    private List<String> imageUrl;
    private List<String[]> boardWHList;
    private List<String[]> drawList;
    /**mp4视频url*/
    private String mp4Url;
    /**mp4视频时长，单位毫秒*/
    private Integer videoDuration;
    /**课件id用于课程与课件关联，表中无直接关联*/
    private String coursewareUuid;
    /**课程id用于课程与课件关联，表中无直接关联*/
    private String courseUuid;
    /**获取有效的进出时间段*/
    private Map<Long, Long> commonPeriod;

    public Map<Long, Long> getCommonPeriod() {
        return commonPeriod;
    }

    public void setCommonPeriod(Map<Long, Long> commonPeriod) {
        this.commonPeriod = commonPeriod;
    }

    public String getCoursewareUuid() {
        return coursewareUuid;
    }

    public void setCoursewareUuid(String coursewareUuid) {
        this.coursewareUuid = coursewareUuid;
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public Integer getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Integer videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getMp4Url() {
        return mp4Url;
    }

    public void setMp4Url(String mp4Url) {
        this.mp4Url = mp4Url;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getFlacVoiceUrl() {
        return flacVoiceUrl;
    }

    public void setFlacVoiceUrl(String flacVoiceUrl) {
        this.flacVoiceUrl = flacVoiceUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String[]> getBoardWHList() {
        return boardWHList;
    }

    public void setBoardWHList(List<String[]> boardWHList) {
        this.boardWHList = boardWHList;
    }

    public List<String[]> getDrawList() {
        return drawList;
    }

    public void setDrawList(List<String[]> drawList) {
        this.drawList = drawList;
    }

    @Override
    public String toString() {
        return "TrackData{" +
                "voiceUrl='" + voiceUrl + '\'' +
                ", flacVoiceUrl='" + flacVoiceUrl + '\'' +
                ", duration=" + duration +
                ", pages=" + pages +
                ", imageUrl=" + imageUrl +
                ", boardWHList=" + boardWHList +
                ", drawList=" + drawList +
                ", mp4Url='" + mp4Url + '\'' +
                ", videoDuration=" + videoDuration +
                ", coursewareUuid='" + coursewareUuid + '\'' +
                ", courseUuid='" + courseUuid + '\'' +
                ", commonPeriod=" + commonPeriod +
                '}';
    }
}
