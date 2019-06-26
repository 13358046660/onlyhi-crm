package cn.onlyhi.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.LoggerFactory;
/**
 * @description：
 * @author： denny
 * @create： 2018-03-08 14:38
 **/
public class OssUtils {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OssUtils.class);
    /**
     * 测试的
     */
    private static String testEndPoint = "audio.onlyeduhi.cn";
    /**
     * 生产的
     */
    private static String onLineEndPoint = "audio.onlyhi.cn";
    private static String accessKeyId = "LTAIuZJID8X9AyIl";
    private static String accessKeySecret = "2c7ykyjqy6VqpDMcXby39jaiLhpw7N";
    /**
     * 生产的
     */
    private static String onLineBucketName = "onlyhiclient";
    /**
     * 测试的
     */
    private static String testBucketName = "clienttest";
    private static List<File> files = new ArrayList<>();

    /**
     * 生成ossclient客户端
     *
     * @return {@link OSSClient}
     */
    public static OSSClient generateOssClient() {
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(onLineEndPoint, accessKeyId, accessKeySecret);
        return ossClient;
    }

    public static void uploadFile(OSSClient ossClient, File file, String key) {

        //获取文件流以及文件的属性
        InputStream is;
        try {
            is = new FileInputStream(file);
            String fileName = file.getName();
            Long fileSize = file.length();

            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());

            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(fileName));
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件
            PutObjectResult putResult = ossClient.putObject(onLineBucketName, key, is, metadata);

            //解析结果
            String resultStr = putResult.getETag();
            System.out.println("resultStr:" + resultStr);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 方式一：上传文件流
     *
     * @return {@link OSSClient}
     */
    public static void uploadFileInputStream(OSSClient ossClient, InputStream is, String fileName, Long fileSize, String key, File file) {

        //获取文件流以及文件的属性
        try {
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());

            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(fileName));
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件uploadPath/recordDir/20190313/09cb0c81-81c9-40a6-8e2f-e984aec1d9ff_0_20190313003840515.m4a
            LOGGER.info("key:" + key);
            PutObjectResult putResult = ossClient.putObject(onLineBucketName, key, is, metadata);

            //解析结果
            String resultStr = putResult.getETag();
            if (resultStr != null) {
                //上传后删除当前文件
                boolean flag = file.delete();
                if (flag) {
                    LOGGER.info("已删除:" + file.getPath());
                    LOGGER.info("file:" + file);
                }
                LOGGER.info("resultStr:" + resultStr);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 方式二：文件上传
     *
     * @return {@link OSSClient}
     */
    public static void uploadFileNew(OSSClient ossClient, String fileName, Long fileSize, String key, File file) {
        try {
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length());
            metadata.setCacheControl("no-cache");
            //用于设定禁止浏览器从本地机的缓存中调阅页面内容，设定后一旦离开网页就无法从Cache中再调出
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(fileName));
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            // 上传文件。<yourLocalFile>由本地文件路径加文件名包括后缀组成，例如/users/local/myfile.txt。
            PutObjectResult putResult = ossClient.putObject(onLineBucketName, key, new File(file.getPath()));
            LOGGER.info("putResult:{}",putResult);
            //解析结果
            String resultStr = putResult.getETag();
            if (resultStr != null) {
                boolean flag = file.delete();
                if (flag) {
                System.out.println("uploadFile已删除:" + file.getPath());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

 /*   public static void main(String[] args) {
        OSSClient ossClient=new OSSClient(onLineEndPoint, accessKeyId, accessKeySecret);
        File file=new File("D:/各种日志/测试环境file/e4535b01-54d8-4410-8487-b4f971a2eb31.m4a");
        uploadFileNew(ossClient,"e4535b01-54d8-4410-8487-b4f971a2eb31.m4a",Long.valueOf("1631410"),"key",file);
    }*/

    /**
     * 下载文件
     *
     * @param client
     * @param key
     * @param filename
     * @
     */
    private static void downloadFile(OSSClient client, String bucketName,
                                     String key, String filename) throws OSSException, ClientException {
        client.getObject(new GetObjectRequest(bucketName, key), new File(
                filename));
    }

    /**
     * 生成文件的url访问路径
     *
     * @param ossClient {@link OSSClient}
     * @param key
     * @return {@link String}
     */
    public static String generateFileUrl(OSSClient ossClient, String key) {

        // 设置URL过期时间为24小时
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000 * 24);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(onLineBucketName, key, expiration);
        return url.toString();
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public static final String getContentType(String fileName) {
        System.out.println("fileName:" + fileName);
        String fileExtension = "";
        if (Objects.equals(-1, fileName.lastIndexOf("."))) {
            return "";
        } else {
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
            System.out.println("fileExtension:" + fileExtension);
            if (".bmp".equalsIgnoreCase(fileExtension)) {
                return "image/bmp";
            }
            if (".gif".equalsIgnoreCase(fileExtension)) {
                return "image/gif";
            }
            if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension) || ".png".equalsIgnoreCase(fileExtension)) {
                return "image/jpeg";
            }
            if (".html".equalsIgnoreCase(fileExtension)) {
                return "text/html";
            }
            if (".txt".equalsIgnoreCase(fileExtension)) {
                return "text/plain";
            }
            if (".vsd".equalsIgnoreCase(fileExtension)) {
                return "application/vnd.visio";
            }
            if (".ppt".equalsIgnoreCase(fileExtension) || ".pptx".equalsIgnoreCase(fileExtension)) {
                return "application/vnd.ms-powerpoint";
            }
            if (".doc".equalsIgnoreCase(fileExtension) || ".docx".equalsIgnoreCase(fileExtension)) {
                return "application/msword";
            }
            if (".xml".equalsIgnoreCase(fileExtension)) {
                return "text/xml";
            }
            if (".apk".equalsIgnoreCase(fileExtension)) {

                return "application/vnd.android.package-archive";
            }
            if (".exe".equalsIgnoreCase(fileExtension)) {
                return "application/octet-stream";
            }
            if (".rar".equalsIgnoreCase(fileExtension)) {
                return "application/x-rar-compressed";
            }
            if (".zip".equalsIgnoreCase(fileExtension)) {
                return "application/zip";
            }
            if (".pdf".equalsIgnoreCase(fileExtension)) {
                return "application/pdf";
            }
            if (".png".equalsIgnoreCase(fileExtension)) {
                return "application/x-png";
            }
       /*     if (".m4a".equalsIgnoreCase(fileExtension)) {
                return "video/mpeg4";
            }*/
            if (".m4a".equalsIgnoreCase(fileExtension)) {
                LOGGER.info(".m4a");
                return "audio/mp4a-latm";
            }
            if (".flac".equalsIgnoreCase(fileExtension)) {
                return "video/mpeg4";
            }
            if (".mp4".equalsIgnoreCase(fileExtension)) {
                return "video/mp4";
            }
         /*   if (".db".equalsIgnoreCase(fileExtension)) {
                LOGGER.info(".db");
                return "application/octet-stream";
            }*/
        }

        {
            return "text/html";
        }
    }

    /**
     * 获取文件夹下的所有文件信息
     *
     * @param fileAddress 文件夹
     */
    public static List<File> getFileName(String fileAddress) {
        File f = new File(fileAddress);
        if (!f.exists()) {
            LOGGER.info(fileAddress + " not exists");
            return files;
        }

        File[] fa = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                LOGGER.info(fs.getName() + " [目录]start:");
                getFileName(fs.getPath());
            } else {
                LOGGER.info(fs.getName());
                files.add(fs);
            }
        }
        return files;
    }

/*    public static void main(String[] args) {
        try {
            //String fileAddress = DateUtil.parseDateToStr(new Date());
            //String key = "app" + "/" + fileAddress + "/" + "app-debug.apk";
            //创建ossclient客户端
            OSSClient client = OssUtils.generateOssClient();
            //uploadFileInputStream(client, new FileInputStream("C://Users//Administrator//Desktop//app-debug.apk"), "app-debug.apk", new File("C://Users//Administrator//Desktop//app-debug.apk").length(), key);
            //生成文件可访问的url uploadPath/recordDir/20180714/07097f3e-64aa-4672-84d7-695314436679.m4a
            String url = generateFileUrl(client, "uploadPath/recordDir/20180714/07097f3e-64aa-4672-84d7-695314436679.m4a");
            System.out.println("url==" + url.toString());
            //关闭ossclient客户端
            client.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}

