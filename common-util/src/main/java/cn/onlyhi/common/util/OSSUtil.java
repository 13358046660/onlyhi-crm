package cn.onlyhi.common.util;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import static cn.onlyhi.common.constants.Constants.accessKeyId;
import static cn.onlyhi.common.constants.Constants.accessKeySecret;
import static cn.onlyhi.common.constants.Constants.endpoint;

public class OSSUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OSSUtil.class);

    /*private String endpoint = "oss-cn-shanghai.aliyuncs.com";
    private String accessKeyId = "LTAIuZJID8X9AyIl";
    private String accessKeySecret = "2c7ykyjqy6VqpDMcXby39jaiLhpw7N";*/
    private String bucketName;
    private String domain;
    private OSSClient ossClient;
    /**
     * URL默认过期时间为当前时间24小时后
     */
    private Date expiration = new Date(System.currentTimeMillis() + 3600 * 24 * 1000);
    private CredentialsProvider credsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
    private ClientConfiguration config = new ClientConfiguration();

    public OSSUtil(String domain, String bucketName) {
        this.domain = domain;
        this.bucketName = bucketName;
        this.ossClient = new OSSClient(endpoint, credsProvider, config);
    }

    /**
     * 关闭client
     */
    public void closeOssClient() {
        ossClient.shutdown();
    }

    /**
     * 字节上传，默认过期时间：当前时间24小时后
     *
     * @param bytes
     * @param key
     * @return 文件url
     */
    public String upload(byte[] bytes, String key) {
        return upload(bytes, key, expiration);
    }

    /**
     * 字节上传
     *
     * @param bytes
     * @param key
     * @param expiration 过期时间
     * @return url
     */
    public String upload(byte[] bytes, String key, Date expiration) {
        ossClient.putObject(bucketName, key, new ByteArrayInputStream(bytes));
        return generatePresignedUrl(key, expiration);
    }

    /**
     * 数据流上传，默认过期时间：当前时间24小时后
     *
     * @param inputStream
     * @param key
     * @return 文件url
     */
    public String upload(InputStream inputStream, String key) {
        return upload(inputStream, key, expiration);
    }

    public String upload(InputStream inputStream, String key, ObjectMetadata objectMetadata) {
        return upload(inputStream, key, expiration, objectMetadata);
    }

    /**
     * 数据流上传
     *
     * @param inputStream
     * @param key
     * @param expiration  过期时间
     * @return url
     */
    public String upload(InputStream inputStream, String key, Date expiration) {
        ossClient.putObject(bucketName, key, inputStream);
        return generatePresignedUrl(key, expiration);
    }

    public String upload(InputStream inputStream, String key, Date expiration, ObjectMetadata objectMetadata) {
        ossClient.putObject(bucketName, key, inputStream, objectMetadata);
        return generatePresignedUrl(key, expiration);
    }

    /**
     * 文件上传，默认过期时间：当前时间24小时后
     *
     * @param file
     * @param key
     * @return 文件url
     */
    public String upload(File file, String key) {
        return upload(file, key, expiration);
    }

    /**
     * 文件上传
     *
     * @param file
     * @param key
     * @param expiration 过期时间
     * @return
     */
    public String upload(File file, String key, Date expiration) {
        ossClient.putObject(bucketName, key, file);
        return generatePresignedUrl(key, expiration);
    }

    /**
     * 设置oss-key的过期时间并返回url
     *
     * @param key
     * @param expiration
     * @return
     */
    private String generatePresignedUrl(String key, Date expiration) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        String fileUrl = url.toString();
        fileUrl = fileUrl.replace(bucketName + "." + endpoint, domain).replace("http", "https");
        LOGGER.info("key:{},fileUrl:{}", key, fileUrl);
        return fileUrl;
    }

    /**
     * 获取oss-key的url，此url的过期时间为24小时
     *
     * @param key
     * @return
     */
    public String generatePresignedUrl(String key) {
        return generatePresignedUrl(key, expiration);
    }

    /*public static void main(String[] args) throws Exception {
        String domain = "image.onlyeduhi.cn";
        String bucketName = "onlyhitest";
        String filePath = "D:\\testoffice2pdf\\转换有问题的\\1.24 晚.pptx";
        File file = new File(filePath);
        *//*for (int i = 0; i < 5; i++) {
            Thread.sleep(1000 * 5);
            OSSUtil ossUtil = new OSSUtil(domain, bucketName);
            String uploadUrl = ossUtil.upload(file, file.getName());
            System.out.println(i + ":" + uploadUrl);
        }*//*
        OSSUtil ossUtil = new OSSUtil(domain, bucketName);
        String dateStr = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        String key = "teacher" + "/" + dateStr + "/" + getDateTimeFileName(file.getName());
        String uploadUrl = ossUtil.upload(file, key);
        System.out.println(uploadUrl);
    }*/

}
