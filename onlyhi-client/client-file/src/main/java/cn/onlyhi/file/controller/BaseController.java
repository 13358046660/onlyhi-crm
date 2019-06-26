package cn.onlyhi.file.controller;

import cn.onlyhi.client.po.Courseware;
import cn.onlyhi.client.po.CoursewareImage;
import cn.onlyhi.client.service.CoursewareImageService;
import cn.onlyhi.client.service.CoursewareService;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.util.ClientUtil;
import cn.onlyhi.common.util.CmdExecResult;
import cn.onlyhi.common.util.OSUtil;
import cn.onlyhi.common.util.OfficeToPdfUtil;
import cn.onlyhi.common.util.PdfToImageUtil;
import cn.onlyhi.common.util.UUIDUtil;
import cn.onlyhi.file.config.YmlMyConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.onlyhi.common.constants.Constants.FILESEPARATOR;
import static cn.onlyhi.common.constants.Constants.UPLOAD_ROOT;
import static cn.onlyhi.common.util.ClientUtil.getSofficeCmd;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/19.
 */
public class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected CoursewareService coursewareService;
    @Autowired
    protected YmlMyConfig ymlMyConfig;
    @Autowired
    protected CoursewareImageService coursewareImageService;

    /**
     * 文件名判断并重命名
     *
     * @param coursewareName
     * @param teacherUuid
     * @return
     */
    protected String getCoursewareName(String coursewareName, String teacherUuid) {
        Courseware courseware = coursewareService.findByCoursewareName(teacherUuid, coursewareName);
        if (courseware == null) {
            //LOGGER.info("getCoursewareName:{}", coursewareName);
            return coursewareName;
        }
        String fileName = coursewareName.substring(0, coursewareName.lastIndexOf("."));
        String suffix = coursewareName.substring(coursewareName.lastIndexOf(".") + 1);
        //查询时将findName中特殊符号进行转义
        String searchFileName = fileName.replace("'", "\\'").replace("(", "\\\\(").replace("[", "\\\\[");
        if(searchFileName.contains("?")){
            searchFileName=searchFileName.replace("?","");
        }
        //清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher searchFileNameNew = p.matcher(searchFileName);
        List<String> coursewareNameList = coursewareService.findByCoursewareName(searchFileNameNew.replaceAll("").trim(), suffix, teacherUuid);
        if (coursewareNameList == null || coursewareNameList.size() == 0) {
            return fileName + "(1)." + suffix;
        }
        int size = coursewareNameList.size();
        for (int i = 0; i <= size; i++) {
            String newCoursewareName = fileName + "(" + (i + 1) + ")." + suffix;
            if (!coursewareNameList.contains(newCoursewareName)) {
                //LOGGER.info("返回新命名：", newCoursewareName);
                return newCoursewareName;
            }
        }
        return null;
    }

    /**
     * 将课件进行转换并保存，返回转换状态
     *
     * @param saveFile       课件保存路径
     * @param coursewareUuid
     * @param isPdf
     * @return
     */
    protected int converAndSaveCourseware(File saveFile, String coursewareUuid, boolean isPdf) {
        String saveFilePath = saveFile.getPath();
        String coursewareUrl = ymlMyConfig.getUploadAddress() + saveFilePath.substring(saveFilePath.indexOf(UPLOAD_ROOT));
        String coursewareType = ClientEnum.CoursewareType.TEACHER.name();
        int converStatus = 3;
        String coursewarePreviewUrl = null;
        File pdfFile = null;
        if (isPdf) {
            pdfFile = saveFile;
        } else {
            File inputFile = saveFile;
            File outDir = saveFile.getParentFile();
            String cmdPath = "";
            if (OSUtil.isWindows()) {    //本地开发环境
                cmdPath = "soffice";    //需安装LibreOffice并配置环境变量
            } else {
                cmdPath = getSofficeCmd();
            }
            if (StringUtils.isBlank(cmdPath)) {
                return converStatus;
            }
            try {
                CmdExecResult execResult = OfficeToPdfUtil.conver(inputFile, outDir, cmdPath);
                if (execResult.isExecStatus()) {
                    LOGGER.info("------------- officeToPdf success -------------------");
                } else {
                    Courseware courseware = new Courseware();
                    courseware.setCoursewareUuid(coursewareUuid);
                    courseware.setConverStatus(converStatus);
                    coursewareService.update(courseware);
                    LOGGER.info("------------- officeToPdf failed -------------------");
                    return converStatus;
                }
                String inputFileName = inputFile.getName();
                String pdfName = inputFileName.substring(0, inputFileName.lastIndexOf(".")) + ".pdf";
                pdfFile = new File(outDir, pdfName);
            } catch (Exception e) {
                LOGGER.info("OfficeToPdf异常:", e);
                LOGGER.error(e.getMessage());
            }
        }
        PdfToImageUtil.ConverResult converResult = null;
        String imageSaveDirPath = null;
        if (pdfFile != null && pdfFile.exists()) {
            String pdfFilePath = pdfFile.getPath();
            coursewarePreviewUrl = ymlMyConfig.getUploadAddress() + pdfFilePath.substring(pdfFilePath.indexOf(UPLOAD_ROOT));
            imageSaveDirPath = pdfFilePath.substring(0, pdfFilePath.lastIndexOf("."));
            File imageSaveDir = new File(imageSaveDirPath);
            if (!imageSaveDir.exists()) {
                imageSaveDir.mkdirs();
            }
            try {
                converResult = PdfToImageUtil.convertOne2One(pdfFile, imageSaveDir);
            } catch (Exception e) {
                LOGGER.info("PdfToImage异常:", e);
                LOGGER.error(e.getMessage());
            }
        } else {
            LOGGER.info("pdfFile={}不存在", pdfFile);
        }

        Integer pageNum = null;
        List<CoursewareImage> coursewareImageList = new ArrayList<>();
        if (converResult != null) {
            pageNum = converResult.getPageSize();
            int imageHeight = converResult.getImageHeight();
            int imageWidth = converResult.getImageWidth();
            int imageLength=converResult.getImageLength();
            List<String> imageNameList = converResult.getImageNameList();
            String imageDirUrl = ymlMyConfig.getUploadAddress() + imageSaveDirPath.substring(imageSaveDirPath.indexOf(UPLOAD_ROOT)) + FILESEPARATOR;
            for (String imageName : imageNameList) {
                CoursewareImage coursewareImage = new CoursewareImage();
                coursewareImage.setCoursewareImageUuid(UUIDUtil.randomUUID2());
                coursewareImage.setCoursewareUuid(coursewareUuid);
                coursewareImage.setImageUrl(imageDirUrl + imageName);
                coursewareImage.setImageName(imageName);
                coursewareImage.setWidth(imageWidth);
                coursewareImage.setHeight(imageHeight);
                String imagePath = imageSaveDirPath + FILESEPARATOR + imageName;
                coursewareImage.setImagePath(imagePath);
                coursewareImage.setImageLength(imageLength);
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    try {
                        File imageSaveDir = new File(imageSaveDirPath);
                        int pageIndex = Integer.parseInt(imageName.substring(0, imageName.indexOf(".")));
                        PdfToImageUtil.convertOne(pdfFile, imageSaveDir, pageIndex);
                    } catch (Exception e) {
                        LOGGER.info("转换单个图片[" + imageName + "]失败", e);
                        LOGGER.error(e.getMessage());
                    }
                }
                String imageMd5 = ClientUtil.md5(imageFile);
                coursewareImage.setImageMd5(imageMd5);
                coursewareImageList.add(coursewareImage);
            }
        } else {
            LOGGER.info("pdfFile={}不存在", pdfFile);
        }
        Courseware courseware = new Courseware();
        courseware.setCoursewareUuid(coursewareUuid);
        courseware.setCoursewareUrl(coursewareUrl);
        courseware.setCoursewareType(coursewareType);
        courseware.setCoursewarePreviewUrl(coursewarePreviewUrl);
        courseware.setPageNum(pageNum);
        courseware.setCoursewarePath(saveFilePath);
        if (pdfFile != null && pdfFile.exists()) {
            courseware.setPdfName(pdfFile.getName());
        }
        if (coursewareImageList.size() > 0) {
            coursewareImageService.batchSave(coursewareImageList);
            converStatus = 2;
        }
        courseware.setConverStatus(converStatus);
        coursewareService.update(courseware);
        return converStatus;
    }
    /**
     * 原方法不变，新增此方法，如果不需要转码与上传上传分开需求，客户端切换url恢复原方法调用
     * 将课件进行转换并保存，返回转换状态
     * @param saveFile       课件保存路径
     * @param coursewareUuid
     * @param isPdf
     * @return
     */
    protected int converAndSaveCoursewareNew(File saveFile, String coursewareUuid, boolean isPdf,Integer isPageRequest,Integer isOriginalFile) {
        LOGGER.info("将课件进行转换并保存.");
        String saveFilePath = saveFile.getPath();
        String coursewareUrl = ymlMyConfig.getUploadAddress() + saveFilePath.substring(saveFilePath.indexOf(UPLOAD_ROOT));

        String coursewareType = ClientEnum.CoursewareType.TEACHER.name();
        int converStatus = 3;
        String coursewarePreviewUrl = null;
        File pdfFile = null;
        if (isPdf) {
            pdfFile = saveFile;
        } else {
            File inputFile = saveFile;
            File outDir = saveFile.getParentFile();
            String cmdPath = "";
            if (OSUtil.isWindows()) {    //本地开发环境
                cmdPath = "soffice";    //需安装LibreOffice并配置环境变量
            } else {
                cmdPath = getSofficeCmd();
            }
            if (StringUtils.isBlank(cmdPath)) {
                return converStatus;
            }
            try {
                CmdExecResult execResult = OfficeToPdfUtil.conver(inputFile, outDir, cmdPath);
                if (execResult.isExecStatus()) {
                    LOGGER.info("------------- officeToPdf success -------------------");
                } else {
                    Courseware courseware = new Courseware();
                    courseware.setCoursewareUuid(coursewareUuid);
                    courseware.setConverStatus(converStatus);
                    coursewareService.update(courseware);
                    LOGGER.info("------------- officeToPdf failed -------------------");
                    return converStatus;
                }
                String inputFileName = inputFile.getName();
                String pdfName = inputFileName.substring(0, inputFileName.lastIndexOf(".")) + ".pdf";
                pdfFile = new File(outDir, pdfName);
            } catch (Exception e) {
                LOGGER.info("OfficeToPdf异常:", e);
                LOGGER.error(e.getMessage());
            }
        }
        PdfToImageUtil.ConverResult converResult = null;
        String imageSaveDirPath = null;
        if (pdfFile != null && pdfFile.exists()) {
            String pdfFilePath = pdfFile.getPath();
            coursewarePreviewUrl = ymlMyConfig.getUploadAddress() + pdfFilePath.substring(pdfFilePath.indexOf(UPLOAD_ROOT));
            imageSaveDirPath = pdfFilePath.substring(0, pdfFilePath.lastIndexOf("."));
            File imageSaveDir = new File(imageSaveDirPath);
            if (!imageSaveDir.exists()) {
                imageSaveDir.mkdirs();
            }
            try {
                converResult = PdfToImageUtil.convertOne2One(pdfFile, imageSaveDir);
            } catch (Exception e) {
                LOGGER.info("PdfToImage异常:", e);
                LOGGER.error(e.getMessage());
            }
        } else {
            LOGGER.info("pdfFile={}不存在", pdfFile);
        }

        Integer pageNum = null;
        List<CoursewareImage> coursewareImageList = new ArrayList<>();
        LOGGER.info("converResult是否为空."+converResult);
        if (converResult != null) {
            pageNum = converResult.getPageSize();
            int imageHeight = converResult.getImageHeight();
            int imageWidth = converResult.getImageWidth();
            int imageLength=converResult.getImageLength();
            List<String> imageNameList = converResult.getImageNameList();
            String imageDirUrl = ymlMyConfig.getUploadAddress() + imageSaveDirPath.substring(imageSaveDirPath.indexOf(UPLOAD_ROOT)) + FILESEPARATOR;
            for (String imageName : imageNameList) {
                CoursewareImage coursewareImage = new CoursewareImage();
                coursewareImage.setCoursewareImageUuid(UUIDUtil.randomUUID2());
                coursewareImage.setCoursewareUuid(coursewareUuid);
                coursewareImage.setImageUrl(imageDirUrl + imageName);
                coursewareImage.setImageName(imageName);
                coursewareImage.setWidth(imageWidth);
                coursewareImage.setHeight(imageHeight);
                String imagePath = imageSaveDirPath + FILESEPARATOR + imageName;
                coursewareImage.setImagePath(imagePath);
                coursewareImage.setImageLength(imageLength);
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    try {
                        File imageSaveDir = new File(imageSaveDirPath);
                        int pageIndex = Integer.parseInt(imageName.substring(0, imageName.indexOf(".")));
                        PdfToImageUtil.convertOne(pdfFile, imageSaveDir, pageIndex);
                    } catch (Exception e) {
                        LOGGER.info("转换单个图片[" + imageName + "]失败", e);
                        LOGGER.error(e.getMessage());
                    }
                }
                String imageMd5 = ClientUtil.md5(imageFile);
                coursewareImage.setImageMd5(imageMd5);
                coursewareImageList.add(coursewareImage);
            }
        } else {
            LOGGER.info("pdfFile={}不存在", pdfFile);
        }
        Courseware courseware = new Courseware();
        courseware.setCoursewareUuid(coursewareUuid);
        //客户端上传转码不更新coursewareUrl，页面上传转码时更新coursewareUrl
        if(Objects.equals(isPageRequest,1)){
            courseware.setCoursewareUrl(coursewareUrl);
        }
        LOGGER.info("isOriginalFile:"+isOriginalFile);
        if(Objects.equals(isOriginalFile,0)){
            LOGGER.info("上传是源文件存入coursewareUrl.");
            courseware.setCoursewareUrl(coursewareUrl);
        }
        courseware.setCoursewareType(coursewareType);
        courseware.setCoursewarePreviewUrl(coursewarePreviewUrl);
        courseware.setPageNum(pageNum);
        courseware.setCoursewarePath(saveFilePath);
        if (pdfFile != null && pdfFile.exists()) {
            courseware.setPdfName(pdfFile.getName());
        }
        if (coursewareImageList.size() > 0) {
            int flag=coursewareImageService.batchSave(coursewareImageList);
            if(flag>0){
                LOGGER.info("pdf转img完成，存入coursewareImage表完成.");
            }
            converStatus = 2;
        }
        courseware.setConverStatus(converStatus);
        int count=coursewareService.update(courseware);
        if(count>0){
            LOGGER.info("更新courseware完成.");
        }
        LOGGER.info("将课件进行转换并保存，返回转换状态converStatus"+converStatus);
        return converStatus;
    }
}
