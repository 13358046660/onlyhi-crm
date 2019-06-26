package cn.onlyhi.common.util;

import cn.onlyhi.common.constants.Constants;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static cn.onlyhi.common.constants.Constants.COURSEWARE;
import static cn.onlyhi.common.constants.Constants.FILESEPARATOR;
import static cn.onlyhi.common.constants.Constants.RECORD_DIR;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/21.
 */
public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static final String DATEPATTERN = "-";
    public static final String TIMEPATTERN = ":";
    public static final String ORIGINALDIR = "original";
    public static final String PDFDIR = "pdf";
    public static final String IMAGEDIR = "image";
    public static final String FILELINK = "_";
    private String uuid;
    private Integer id;

    public FileUtils() {
    }
    public FileUtils(String uuid,Integer id) {
        this.uuid = uuid;
        this.id=id;
    }

    // 缓存文件头信息-文件头信息
    public static final HashMap<String, String> mFileTypes = new HashMap<>();

    static {
        mFileTypes.put("D0CF11E0", "doc");  //ppt、xls也是
        mFileTypes.put("25504446", "pdf");
        mFileTypes.put("504B0304", "docx"); //pptx、xlsx也是
        mFileTypes.put("FFD8FF", "jpg"); //jpeg也是 // FFD8FFxx都是jpg，比如 FFD8FFE0、FFD8FFE1
        mFileTypes.put("89504E47", "png");
    }

    /**
     * 将文件名改为时间戳
     *
     * @param fileName
     * @return
     */
    public static String getDateTimeFileName(String fileName) {
        String timeStr = String.valueOf(System.currentTimeMillis());
        int i = fileName.lastIndexOf(".");
        if (i == -1) {
            return timeStr;
        } else {
            return timeStr + fileName.substring(i);
        }
    }

    /**
     * 文件名后追加日期时间名
     *
     * @param fileName xxx.pdf
     * @return xxx_201711091455.pdf
     */
    public static String getDateFileName(String fileName) {
        String dateStr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        int i = fileName.lastIndexOf(".");
        if (i == -1) {
            return fileName + "_" + dateStr;
        } else {
            return fileName.substring(0, i) + "_" + dateStr + fileName.substring(i);
        }
    }

    /**
     * 返回年月日，以pattern连接
     *
     * @param calendar
     * @param pattern
     * @return
     */
    public static String getFormatDate(Calendar calendar, String pattern) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + pattern + formatInt(month) + pattern + formatInt(day);
    }

    /**
     * 返回时间hm，以pattern连接
     *
     * @param calendar
     * @param pattern
     * @return
     */
    public static String getFormatTime(Calendar calendar, String pattern) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return formatInt(hour) + pattern + formatInt(minute);
    }

    private static String formatInt(int parameter) {
        return (parameter > 10 ? parameter : "0" + parameter).toString();
    }

    private static final int SUFFIX = Math.abs("onlyhi".hashCode());

    /**
     * 获取录制Id
     *
     * @param uuid
     * @return
     */
   public static int getRecordId(String uuid) {
        int recordId = Math.abs((uuid.concat("record")).hashCode());
        return recordId;
    }
    public int getRecordIdNew(String uuid,Integer id) {
        int recordId = hashCode(uuid,id);
        return recordId;
    }
    //重写hashCode方法
    public int hashCode(String uuid,Integer id) {
        final int prime = 31;
        int result = 1;
        //result= result+age
        result = prime*1 + result;
        //result=result+name.hashCode()
        //name=null返回0，否则返回name.hashCode().
        result = prime*result + ((uuid == null)?0 : uuid.hashCode())+((id == null)?0 : id.hashCode());
        return result;
    }
    //重写equals方法
    @Override
    public boolean equals(Object obj) {
        //提高效率
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        //提高代码健壮性,不是同一个类型就直接返回false,省得向下转型了
        if(this.getClass() != obj.getClass()) {
            return false;
        }
        //向下转型
        FileUtils p = (FileUtils)obj;

        if(this.uuid == null) {
            if(p.uuid != null) {
                return false;
            }

        }
        else if(!this.uuid.equals(p.uuid)) {
            return false;
        }
        return true;
    }
    /**
     * 获取通信频道Id
     *
     * @param uuid
     * @return
     */
    public static String getCommChannelId(String uuid) {
        return uuid + String.valueOf(SUFFIX).substring(0, 2);
    }

    /**
     * 获取信令频道Id
     *
     * @param uuid
     * @return
     */
    public static String getSignallingChannelId(String uuid) {
        return uuid + SUFFIX;
    }

    /**
     * 获取频道家长Id
     *
     * @param uuid
     * @return
     */
    @Deprecated
    public static int getPatriarchChannelId(String uuid) {
        int patriarchChannelId = Math.abs((uuid + "patriarch").hashCode());
        return patriarchChannelId;
    }

    /**
     * 获取频道教师Id
     *
     * @param uuid
     * @return
     */
    @Deprecated
    public static int getTeacherChannelId(String uuid) {
        int teacherChannelId = Math.abs((uuid + "teacher").hashCode());
        return teacherChannelId;
    }

    /**
     * 获取频道学生Id
     *
     * @param uuid
     * @return
     */
    @Deprecated
    public static int getStudentChannelId(String uuid) {
        int studentChannelId = Math.abs((uuid + "student").hashCode());
        return studentChannelId;
    }

    /**
     * 获取课件上传路径
     *
     * @param request
     * @return
     */
    public static String getUploadFilePath(HttpServletRequest request) {
        String uploadRootPath = getUploadRootPath(request);
        return uploadRootPath + FILESEPARATOR + COURSEWARE;
    }

    /**
     * 获取录制文件保存目录
     *
     * @param request
     * @return
     */
    public static String getRecordDir(HttpServletRequest request) {
        String uploadRootPath = getUploadRootPath(request);
        return uploadRootPath + FILESEPARATOR + RECORD_DIR;
    }

    /**
     * 获取上传根路径
     *
     * @param request
     * @return
     */
    public static String getUploadRootPath(HttpServletRequest request) {
        //获取在Web服务器上的 绝对路径
        String projectPath = request.getSession().getServletContext().getRealPath("");
        LOGGER.info("projectPath={}", projectPath);
        String tempPath = projectPath.substring(0, projectPath.lastIndexOf(FILESEPARATOR));
        String uploadRootPath = tempPath.substring(0, tempPath.lastIndexOf(FILESEPARATOR)) + FILESEPARATOR + Constants.UPLOAD_ROOT;
        LOGGER.info("uploadRootPath={}", uploadRootPath);
        return uploadRootPath;
    }


    /**
     * 获取课件上传路径
     *
     * @param request
     * @return
     */
    public static String getUploadFilePath2(HttpServletRequest request) {
        String uploadRootPath = getUploadRootPath2(request);
        return uploadRootPath + FILESEPARATOR + COURSEWARE;
    }

    /**
     * 获取上传根路径
     *
     * @param request
     * @return
     */
    public static String getUploadRootPath2(HttpServletRequest request) {
        //获取在Web服务器上的 绝对路径
        String projectPath = request.getSession().getServletContext().getRealPath("");
        LOGGER.info("projectPath={}", projectPath);
        String tempPath = projectPath.substring(0, projectPath.lastIndexOf(FILESEPARATOR));
        String uploadRootPath = tempPath.substring(0, tempPath.lastIndexOf(FILESEPARATOR)) + FILESEPARATOR + Constants.UPLOAD_ROOT2;
        LOGGER.info("uploadRootPath={}", uploadRootPath);
        return uploadRootPath;
    }

    public static String getFileSavePath(HttpServletRequest request, String uploadFilePath) {
        String serverIP = request.getLocalAddr();
        LOGGER.info("serverIP={}", serverIP);//172.17.0.3
        if ("172.17.0.3".equalsIgnoreCase(serverIP)) {
            uploadFilePath = uploadFilePath.replace("/usr/local/tomcat", "/docker/tomcat/8089");
        }
        return uploadFilePath;
    }


    /**
     * 将要读取文件头信息的文件的byte数组转换成string类型表示
     *
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    /**
     * 根据文件路径获取文件头信息
     *
     * @param file 文件
     * @return 文件头信息
     */
    private static String getFileHeader(File file) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(file);
            byte[] b = new byte[4];
            /*
             * int read() 从此输入流中读取一个数据字节。 int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             */
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return value;
    }

    private static String getFileHeader(InputStream is) {
        String value = null;
        try {
            byte[] b = new byte[4];
            /*
             * int read() 从此输入流中读取一个数据字节。 int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             */
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return value;
    }

    /**
     * 判断文件是否符合指定类型
     *
     * @param file
     * @return
     */
    public static boolean matchFileType(File file) {
        String fileHeader = getFileHeader(file);
        String value = mFileTypes.get(fileHeader);
        if (value == null) {
            return false;
        }
        return true;
    }

    /**
     * 根据文件头数据判断文件类型
     * @param is
     * @param suffix 文件后缀名
     * @return
     */
    public static boolean matchFileType(InputStream is, String suffix) {
        if ("doc".equalsIgnoreCase(suffix) || "ppt".equalsIgnoreCase(suffix)) {
            suffix = "doc";
        } else if ("docx".equalsIgnoreCase(suffix) || "pptx".equalsIgnoreCase(suffix)) {
            suffix = "docx";
        } else {
            suffix = suffix.toLowerCase();
        }
        String fileHeader = getFileHeader(is);
        String value = mFileTypes.get(fileHeader);
        //LOGGER.info("matchFileType的value==" + value);
        if ("jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix)) {
            value = mFileTypes.get(fileHeader.substring(0, 6));
        }
        //fileHeader表示文件头，如504b0304140006000800表示docx文件
        //LOGGER.info("fileHeader:" + fileHeader);
        if (suffix.equalsIgnoreCase(value) || "jpg".equalsIgnoreCase(value) || "png".equalsIgnoreCase(value)) {
            return true;
        }
        return false;
    }

    /**
     * 读取指定文件的内容
     *
     * @param files
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFileToString(File[] files, String fileName) throws IOException {
        String content = null;
        int fileSize = files.length;
        for (int i = 0; i < fileSize; i++) {
            File file = files[i];
            if (fileName.equalsIgnoreCase(file.getName())) {
                content = org.apache.commons.io.FileUtils.readFileToString(file);
                break;
            }
        }
        return content;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dirFile 被删除目录
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(File dirFile) {
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                // 删除子目录
                flag = deleteDirectory(files[i]);
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        return dirFile.delete();
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

}
