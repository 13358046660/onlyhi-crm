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

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 讲师中心上传
 * @description： @author： denny @create： 2018-03-08 14:38
 **/

public class OssTeacherUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(OssTeacherUtils.class);
	/**线上*/
    private static String onLineEndPoint = "image.onlyhi.cn";
	/**测试*/
    private static String testEndPoint = "image.onlyeduhi.cn";
    private static String accessKeyId = "LTAIuZJID8X9AyIl";
    private static String accessKeySecret = "2c7ykyjqy6VqpDMcXby39jaiLhpw7N";
	/**线上的bucketName对应OSS 界面左侧名称*/
    private static String onLineBucketName = "onlyhi";
	/**测试的bucketName对应OSS 界面左侧名称*/
	private static String testBucketName = "testcourseware";
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

		// 获取文件流以及文件的属性
		InputStream is;
		try {
			is = new FileInputStream(file);
			String fileName = file.getName();
			Long fileSize = file.length();

			// 创建上传Object的Metadata
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(is.available());

			metadata.setCacheControl("no-cache");
			metadata.setHeader("Pragma", "no-cache");
			metadata.setContentEncoding("utf-8");
			metadata.setContentType(getContentType(fileName));
			metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
			// 上传文件
			PutObjectResult putResult = ossClient.putObject(onLineBucketName, key, is, metadata);

			// 解析结果
			String resultStr = putResult.getETag();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public static void uploadFileInputStream(OSSClient ossClient, InputStream is, String fileName, Long fileSize,
			String key) {

		// 获取文件流以及文件的属性
		try {
			// 创建上传Object的Metadata
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(is.available());

			metadata.setCacheControl("no-cache");
			metadata.setHeader("Pragma", "no-cache");
			metadata.setContentEncoding("utf-8");
			metadata.setContentType(getContentType(fileName));
			metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
			// 上传文件
			PutObjectResult putResult = ossClient.putObject(onLineBucketName, key, is, metadata);

			// 解析结果
			String resultStr = putResult.getETag();
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	// 下载文件
	@SuppressWarnings("unused")
	private static void downloadFile(OSSClient client, String bucketName, String key, String filename)
			throws OSSException, ClientException {
		client.getObject(new GetObjectRequest(bucketName, key), new File(filename));
	}

	/**
	 * 生成文件的url访问路径
	 * 
	 * @param ossClient
	 *            {@link OSSClient}
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
	 * @param fileName
	 *            文件名
	 * @return 文件的contentType
	 */
	public static final String getContentType(String fileName) {
		System.out.println("fileName:" + fileName);
		String fileExtension = fileName.substring(fileName.lastIndexOf("."));
		System.out.println("fileExtension:" + fileExtension);
		if (".bmp".equalsIgnoreCase(fileExtension)){
			return "image/bmp";
		}
		if (".gif".equalsIgnoreCase(fileExtension)){
			return "image/gif";
		}
		if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)
				|| ".png".equalsIgnoreCase(fileExtension)){
			return "image/jpeg";
		}
		if (".html".equalsIgnoreCase(fileExtension)){
			return "text/html";
		}
		if (".txt".equalsIgnoreCase(fileExtension)){
			return "text/plain";
		}
		if (".vsd".equalsIgnoreCase(fileExtension)){
			return "application/vnd.visio";
		}
		if (".ppt".equalsIgnoreCase(fileExtension) || ".pptx".equalsIgnoreCase(fileExtension)){
			return "application/vnd.ms-powerpoint";
		}
		if (".doc".equalsIgnoreCase(fileExtension) || ".docx".equalsIgnoreCase(fileExtension)){
			return "application/msword";
		}
		if (".xml".equalsIgnoreCase(fileExtension)){
			return "text/xml";
		}
		if (".apk".equalsIgnoreCase(fileExtension)){
			return "application/vnd.android.package-archive";
		}
		if (".exe".equalsIgnoreCase(fileExtension)){
			return "application/octet-stream";
		}
		if (".rar".equalsIgnoreCase(fileExtension)){
			return "application/x-rar-compressed";
		}
		if (".zip".equalsIgnoreCase(fileExtension)){
			return "application/zip";
		}
		if (".pdf".equalsIgnoreCase(fileExtension)){
			return "application/pdf";
		}
		if (".png".equalsIgnoreCase(fileExtension)){
			return "application/x-png";
		}
		return "text/html";
	}

	/**
	 * 获取文件夹下的所有文件信息
	 * 
	 * @param fileAddress
	 *            文件夹
	 */
	public static List<File> getFileName(String fileAddress) {
		File f = new File(fileAddress);
		if (!f.exists()) {
			// System.out.println(fileAddress + " not exists");
			return files;
		}

		File fa[] = f.listFiles();
		for (int i = 0; i < fa.length; i++) {
			File fs = fa[i];
			if (fs.isDirectory()) {
				// System.out.println(fs.getName() + " [目录]start:");
				getFileName(fs.getPath());
				// System.out.println(fs.getName() + " [目录]End:");
			} else {
				// System.out.println(fs.getName());
				files.add(fs);
			}
		}
		return files;
	}
/*	public static void main(String[] args) {
		try {
			OSSClient client = OssTeacherUtils.generateOssClient();
			//courseware/20180820/7d43ab9aa96840b9b8444b58bf95ae88/1 - 副本.jpg
			//uploadPath/courseware/4/20180808125822745.pdf
			String url = generateFileUrl(client, "courseware/2018-08-20/7d43ab9aa96840b9b8444b58bf95ae88/1 - 副本.jpg");
			System.out.println(url.toString());
			client.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}

