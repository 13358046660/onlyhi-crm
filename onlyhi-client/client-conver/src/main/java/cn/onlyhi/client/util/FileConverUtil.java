package cn.onlyhi.client.util;

import cn.onlyhi.common.constants.Constants;
import cn.onlyhi.common.util.ClientUtil;
import cn.onlyhi.common.util.CmdExecResult;
import cn.onlyhi.common.util.OSUtil;
import cn.onlyhi.common.util.OfficeToPdfUtil;
import cn.onlyhi.common.util.PdfToImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.onlyhi.common.util.ClientUtil.getSofficeCmd;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/21.
 */
public class FileConverUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileConverUtil.class);

    public static class ConverResult {
        private int converStatus;
        private String originalUrl;
        private String pdfUrl;  //若文件为图片，则为图片url
        private int pageSize;
        private int imageWidth;
        private int imageHeight;
        private List<Image> imageList;

        public static class Image {
            private String url;
            private String name;
            private String md5; //图片内容MD5加密后的值

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMd5() {
                return md5;
            }

            public void setMd5(String md5) {
                this.md5 = md5;
            }
        }

        public int getConverStatus() {
            return converStatus;
        }

        public void setConverStatus(int converStatus) {
            this.converStatus = converStatus;
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }

        public String getPdfUrl() {
            return pdfUrl;
        }

        public void setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<Image> getImageList() {
            return imageList;
        }

        public void setImageList(List<Image> imageList) {
            this.imageList = imageList;
        }
    }

    /**
     * @param file
     * @param fileType 1:image 2:pdf   3:office
     * @return
     */
    public static ConverResult conver(File file, int fileType) {
        ConverResult converResult = new ConverResult();
        File pdfFile = null;
        if (fileType == 1) {
            String path = file.getPath();
            //获取图片的宽高
            BufferedImage bufferedImage = null;
            try {
                List<ConverResult.Image> imageList = new ArrayList<>();
                bufferedImage = ImageIO.read(file);
                int imageWidth = bufferedImage.getWidth();
                int imageHeight = bufferedImage.getHeight();
                ConverResult.Image image = new ConverResult.Image();
                image.setUrl(path);
                image.setName(file.getName());
                String imageMd5 = ClientUtil.md5(file);
                image.setMd5(imageMd5);
                imageList.add(image);

                converResult.setConverStatus(2);
                converResult.setImageHeight(imageHeight);
                converResult.setImageWidth(imageWidth);
                converResult.setPageSize(1);
                converResult.setPdfUrl(path);
                converResult.setImageList(imageList);

            } catch (IOException e) {
                converResult.setConverStatus(3);
                LOGGER.info("读取图片异常:{}", e);
                LOGGER.error(e.getMessage());
            }
        } else if (fileType == 2) { //pdf转图片
            pdfFile = file;
        } else if (fileType == 3) { //office
            File inputFile = file;
            File outDir = file.getParentFile();
            String cmdPath = "";
            if (OSUtil.isWindows()) {    //本地开发环境
                cmdPath = "soffice";    //需安装LibreOffice并配置环境变量
            } else {
                cmdPath = getSofficeCmd();
            }
            try {
                CmdExecResult execResult = OfficeToPdfUtil.conver(inputFile, outDir, cmdPath);
                if (execResult.isExecStatus()) {
                    LOGGER.info("------------- officeToPdf success -------------------");
                    String inputFileName = inputFile.getName();
                    String pdfName = inputFileName.substring(0, inputFileName.lastIndexOf(".")) + ".pdf";
                    pdfFile = new File(outDir, pdfName);
                } else {
                    converResult.setConverStatus(3);
                    LOGGER.info("------------- officeToPdf failed -------------------");
                }
            } catch (Exception e) {
                converResult.setConverStatus(3);
                LOGGER.info("OfficeToPdf异常:", e);
                LOGGER.error(e.getMessage());
            }
        } else {
            converResult.setConverStatus(3);
            LOGGER.info("未知类型文件:{}" + file.getPath());
        }
        if (pdfFile != null && pdfFile.exists()) {
            String pdfFilePath = pdfFile.getPath();
            String imageSaveDirPath = pdfFilePath.substring(0, pdfFilePath.lastIndexOf("."));
            File imageSaveDir = new File(imageSaveDirPath);
            if (!imageSaveDir.exists()) {
                imageSaveDir.mkdirs();
            }
            try {
                PdfToImageUtil.ConverResult convertOne2One = PdfToImageUtil.convertOne2One(pdfFile, imageSaveDir);
                int pageSize = convertOne2One.getPageSize();
                int imageHeight = convertOne2One.getImageHeight();
                int imageWidth = convertOne2One.getImageWidth();
                List<String> imageNameList = convertOne2One.getImageNameList();
                List<ConverResult.Image> imageList = new ArrayList<>();
                ConverResult.Image image = new ConverResult.Image();
                for (String imageName : imageNameList) {
                    String imagePath = imageSaveDirPath + Constants.FILESEPARATOR + imageName;
                    image.setUrl(imagePath);
                    image.setName(imageName);
                    String imageMd5 = ClientUtil.md5(new File(imageSaveDirPath, imageName));
                    image.setMd5(imageMd5);
                    imageList.add(image);
                }

                converResult.setConverStatus(2);
                converResult.setImageHeight(imageHeight);
                converResult.setImageWidth(imageWidth);
                converResult.setPageSize(pageSize);
                converResult.setPdfUrl(pdfFilePath);
                converResult.setImageList(imageList);
            } catch (Exception e) {
                converResult.setConverStatus(3);
                LOGGER.info("PdfToImage异常:", e);
                LOGGER.error(e.getMessage());
            }
        } else {
            converResult.setConverStatus(3);
            LOGGER.info("pdfFile={}不存在", pdfFile);
        }
        return converResult;
    }


}
