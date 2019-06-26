package cn.onlyhi.file.controller;

import cn.onlyhi.client.dto.ClassRoomVO;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.SysEnum;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.util.OssUtils;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.file.request.UploadTrackFileRequest;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static cn.onlyhi.common.constants.Constants.*;
import static cn.onlyhi.common.util.CmdExecuteUtil.exec;

/**
 * 手动或定时任务更新classRoom表的oss key，url
 * 更新线上class_room的voice_url(m4a音频文件url),flac_voice_url(flac格式音频文件url),track_url(轨迹文件url)
 * 更新测试class_room的voice_url(m4a音频文件url),flac_voice_url(flac格式音频文件url),track_url(轨迹文件url),mp4_video_url(mp4视频文件下载路径  )
 * @Author wqz
 * <p>
 * Created by wqz on 2018/7/2.
 */
@RestController
@RequestMapping("/client/oss")
public class UploadOssController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadOssController.class);
    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private SysEnumService sysEnumService;
    @PostMapping("/updateUrl")
    //@LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001177, methodName = "ossUpload", description = " 手动或定时任务更新classRoom表的oss key，url")
    public ResponseEntity<Response> ossUpload(UploadTrackFileRequest request) throws Exception {
        //matchFile(RECORD_DIR_ROOT);
        updateOssKeyUrlOnline();
        return ResponseEntity.ok(Response.success());
    }

    public void matchFile(String recordDir) throws Exception {
        LOGGER.info("匹配文件名后缀.");
        File dir = new File(recordDir);
        if (!dir.exists()) {
            throw new Exception("dir is not exist,dirPath:" + recordDir);
        }
        File[] listFiles = dir.listFiles();
        LOGGER.info("listFiles:" + listFiles);
        LOGGER.info("/www/clientfile/uploadPath/recordDir 文件夹数:" + listFiles.length);
        for (File file : listFiles) {
            String filePath = file.getPath();
            ///www/clientfile/uploadPath/recordDir/20180702
            LOGGER.info("filePath:" + filePath);
            String uuid = "";
            if (file.isDirectory()) {
                File[] listFile = file.listFiles();
                String dataDir = file.getName();
                LOGGER.info("日期文件夹:" + dataDir);

                LOGGER.info("/www/clientfile/uploadPath/recordDir/日期，文件个数:" + listFile.length);
                for (File file1 : listFile) {
                    boolean isMp4 = false;
                    boolean isFlac = false;
                    boolean isM4a = false;
                    boolean isCompose = false;
                    //文件名
                    String fileName = file1.getName();
                    String namePath = file1.getPath();
                    LOGGER.info("当前日期下的文件名：" + fileName);
                    if (fileName.endsWith(".mp4")) {
                        LOGGER.info("mp4：" + fileName);
                        uuid = fileName.replace(".mp4", "");
                        LOGGER.info("uuid：" + uuid);
                        isMp4 = true;
                        int count = updateOssKeyUrl(uuid, isMp4, isFlac, isM4a, isCompose, fileName, namePath,dataDir);
                        if (count > 0) {
                            LOGGER.info("更新flac oss key：完成");
                        }
                    }
                    if (fileName.endsWith(".flac")) {
                        LOGGER.info("flac：" + fileName);
                        uuid = fileName.replace(".flac", "");
                        LOGGER.info("uuid：" + uuid);
                        isFlac = true;
                        int count = updateOssKeyUrl(uuid, isMp4, isFlac, isM4a, isCompose, fileName, namePath,dataDir);
                        if (count > 0) {
                            LOGGER.info("更新flac oss key：完成");
                        }
                    }
                    if (fileName.endsWith(".m4a")) {
                        LOGGER.info("m4a：" + fileName);
                        uuid = fileName.replace(".m4a", "");
                        isM4a = true;
                        int count = updateOssKeyUrl(uuid, isMp4, isFlac, isM4a, isCompose, fileName, namePath,dataDir);
                        if (count > 0) {
                            LOGGER.info("更新m4a oss key：完成");
                        }
                    }
                    if (fileName.endsWith("_compose")) {
                        LOGGER.info("_compose：" + fileName);
                        uuid = fileName.replace("_compose", "");
                        LOGGER.info("uuid：" + uuid);
                        isCompose = true;
                        int count = updateOssKeyUrl(uuid, isMp4, isFlac, isM4a, isCompose, fileName, namePath,dataDir);
                        if (count > 0) {
                            LOGGER.info("更新compose oss key：完成");
                        }
                    }
                }
            }
        }
    }
    //更新表的key，url
    private int updateOssKeyUrl(String uuid, boolean isMp4, boolean isFlac, boolean isM4a, boolean isCompose, String fileName, String filePath,String dataDir) throws Exception {
        LOGGER.info("更classRoom表");
        //存表的url 路径uploadPath/recordDir/20180630/
        String urlDir=UPLOAD_ROOT.concat("/").concat(RECORD_DIR).concat("/").concat(dataDir).concat("/");
        String mp4Key = urlDir.concat(fileName);
        if (StringUtils.isNotEmpty(mp4Key) && mp4Key.substring(0, 1).contains("/")) {
            mp4Key = mp4Key.substring(1, mp4Key.length());
        }

        String flacKey = urlDir.concat(fileName);
        if (StringUtils.isNotEmpty(flacKey) && flacKey.substring(0, 1).contains("/")) {
            flacKey = flacKey.substring(1, flacKey.length());
        }
        String m4aKey = urlDir.concat(fileName);
        if (StringUtils.isNotEmpty(m4aKey) && m4aKey.substring(0, 1).contains("/")) {
            m4aKey = m4aKey.substring(1, m4aKey.length());
        }
        String composeKey = urlDir.concat(fileName);
        if (StringUtils.isNotEmpty(composeKey) && composeKey.substring(0, 1).contains("/")) {
            composeKey = composeKey.substring(1, composeKey.length());
        }
        File file = new File(filePath);
        //创建OSSClient客户端
        OSSClient client = OssUtils.generateOssClient();
        // filePath /www/clientfile/uploadPath/recordDir/20180704/f6a267f4-9915-415d-a550-f1b5032f8fc3_compose
        InputStream in = new FileInputStream(filePath);
        if (StringUtils.isNotEmpty(filePath) && filePath.substring(0, 1).contains("/")) {
            // www/clientfile/uploadPath/recordDir/20180702/69bda80c9a9c4c67a824a8d5105e0115_compose
            filePath = filePath.substring(1, filePath.length());
        }
        //上传flac
        LOGGER.info("file.length()：" + file.length());
        String flacOssUrl = "";
        String m4aOssUrl = "";
        String composeOssUrl = "";
        String mp4OssUrl = "";
        if (isFlac) {
            //OssUtils.uploadFileInputStream(client, in, filePath, file.length(), flacKey);
            //生成可访问的url
            flacOssUrl = OssUtils.generateFileUrl(client, flacKey);
            LOGGER.info("flacOssUrl：" + flacOssUrl);
        } else if (isM4a) {
            //上传m4a
            //OssUtils.uploadFileInputStream(client, in, filePath, file.length(), m4aKey);
            m4aOssUrl = OssUtils.generateFileUrl(client, m4aKey);
            LOGGER.info("m4aOssUrl：" + m4aOssUrl);
        } else if (isCompose) {
            //上传compose
            //OssUtils.uploadFileInputStream(client, in, filePath, file.length(), composeKey);
            composeOssUrl = OssUtils.generateFileUrl(client, composeKey);
            LOGGER.info("composeOssUrl：" + composeOssUrl);
        } else if (isMp4) {
            //上传Mp4
            //OssUtils.uploadFileInputStream(client, in, filePath, file.length(), mp4Key);
            mp4OssUrl = OssUtils.generateFileUrl(client, mp4Key);
            LOGGER.info("mp4OssUrl：" + mp4OssUrl);
        }
        client.shutdown();

        int count = 0;
        if (isFlac) {
            LOGGER.info("存表传入的flacKey：" + flacKey);
            ClassRoom classRoom = new ClassRoom();
            classRoom.setCourseUuid(uuid);
            //classRoom.setFlacOssKey(flacKey);
            classRoom.setFlacVoiceUrl(flacOssUrl);
            count = classRoomService.updateByCourseUuid(classRoom);
        } else if (isM4a) {
            LOGGER.info("存表传入的m4aKey：" + m4aKey);
            ClassRoom classRoom = new ClassRoom();
            classRoom.setCourseUuid(uuid);
            //classRoom.setM4aOssKey(m4aKey);
            classRoom.setVoiceUrl(m4aOssUrl);
            count = classRoomService.updateByCourseUuid(classRoom);
        } else if (isCompose) {
            LOGGER.info("存表传入的composeKey：" + composeKey);
            ClassRoom classRoom = new ClassRoom();
            classRoom.setCourseUuid(uuid);
            //classRoom.setTrackOssKey(composeKey);
            classRoom.setTrackUrl(composeOssUrl);
            count = classRoomService.updateByCourseUuid(classRoom);
        } else if (isMp4) {
            LOGGER.info("存表传入的mp4Key：" + mp4Key);
            ClassRoom classRoom = new ClassRoom();
            classRoom.setCourseUuid(uuid);
            //classRoom.setTrackOssKey(composeKey);
            classRoom.setMp4VideoUrl(mp4OssUrl);
            count = classRoomService.updateByCourseUuid(classRoom);
        }
        return count;
    }
    //线上更新表某个时间点的key，url
    private void updateOssKeyUrlOnline() {
        //LOGGER.info("更classRoom表");
        //只更新某个时间点的,一次手动移动到oss上n天的音频
        String startDate = "";
        String endDate = "";
        List<SysEnum> list = sysEnumService.findByEnumType("39");
        //手动配置的时间段
        String date = "";
        for (SysEnum sysEnum : list) {
            //必须2018-07-17 00:00:00格式，表update_time是timestamp类型，否则BETWEEN .and.更新不了
            date = sysEnum.getEnumValue();
        }
        if (date.length() > 0) {
            startDate = date.substring(0, 19);
            if (date.contains("--")) {
                endDate = date.substring(21);
            }
        }
        //当前时间
        //由于线上目前之前已通过手动上传，定时任务请求的目录已没有无法确认要更新的课程id，trackUrl，voiceUrl，文件名，所以手动上传后sql按时间段查询
        //http://clienttest.haiketang.net/uploadPath/recordDir/20180126/175ebfa287474a04b2dedfd5b63512eb.m4a
        //http://clienttest.haiketang.net/uploadPath/recordDir/20180126/175ebfa287474a04b2dedfd5b63512eb_compose
        String urlDir = UPLOAD_ROOT.concat("/").concat(RECORD_DIR).concat("/");
        try {
            //创建OSSClient客户端
            OSSClient client = OssUtils.generateOssClient();
            ClassRoomVO vo = new ClassRoomVO();
            vo.setStartDate(startDate);
            vo.setEndDate(endDate);
            List<ClassRoomVO> roomList = classRoomService.selectByDate(vo);
            //LOGGER.info("roomList.size()=" + roomList.size());
            for (ClassRoomVO room : roomList) {
                int count = 0;
                String dataDir = "";
                String fileName = "";
                String composeKey = "";
                String composeOssUrl = "";
                String m4aKey = "";
                String m4aOssUrl = "";
                String composeDir="";
                String m4aDir="";

                //LOGGER.info("room.getTrackUrl()" + room.getTrackUrl());
                if (StringUtils.isNotEmpty(room.getTrackUrl())) {
                    fileName = room.getCourseUuid().concat("_compose");
                    dataDir = room.getCourseDate();

                    if (dataDir.contains("-")) {
                        dataDir = dataDir.replace("-", "");
                    }
                    //urlDir = "";
                    composeDir = urlDir.concat(dataDir).concat("/");
                    composeKey = composeDir.concat(fileName);
                    composeOssUrl = OssUtils.generateFileUrl(client, composeKey);

                    //LOGGER.info("composeKey=" + composeKey);
                    //LOGGER.info("composeOssUrl=" + composeOssUrl);

                    ClassRoomVO classRoom = new ClassRoomVO();
                    classRoom.setCourseUuid(room.getCourseUuid());
                    classRoom.setStartDate(startDate.toString());
                    classRoom.setEndDate(endDate.toString());
                    classRoom.setTrackUrl(composeOssUrl);
                    count = classRoomService.updateByDate(classRoom);
                    if (count == 0) {
                        LOGGER.info("更新trackUrl失败");
                    }
                }
                if (StringUtils.isNotEmpty(room.getVoiceUrl())) {
                    fileName = room.getCourseUuid().concat(".m4a");
                    dataDir = room.getCourseDate();
                    //LOGGER.info("m4a fileName" + fileName);
                    if (dataDir.contains("-")) {
                        dataDir = dataDir.replace("-", "");
                    }
                    //urlDir = "";
                    m4aDir = urlDir.concat(dataDir).concat("/");
                    //存表的url 路径uploadPath/recordDir/20180630/
                    m4aKey = m4aDir.concat(fileName);
                    m4aOssUrl = OssUtils.generateFileUrl(client, m4aKey);

                    //LOGGER.info("m4aKey：" + m4aKey);
                    //LOGGER.info("m4aOssUrl：" + m4aOssUrl);

                    ClassRoomVO classRoom = new ClassRoomVO();
                    classRoom.setCourseUuid(room.getCourseUuid());
                    classRoom.setVoiceUrl(m4aOssUrl);
                    classRoom.setStartDate(startDate.toString());
                    classRoom.setEndDate(endDate.toString());
                    count = classRoomService.updateByDate(classRoom);
                    if (count == 0) {
                        LOGGER.info("更新m4aOssUrl失败");
                    }
                }
            }
            client.shutdown();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
    @PostMapping("/testScript")
    public ResponseEntity<Response> testScript(@RequestBody UploadTrackFileRequest request) throws Exception {
        LOGGER.error("testScript"+request.getScriptUrl());
        try{
            exec(request.getScriptUrl(), false);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success());
    }
}
