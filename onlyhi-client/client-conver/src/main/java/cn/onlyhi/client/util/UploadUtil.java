//package cn.onlyhi.client.util;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedReader;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.Iterator;
//import java.util.Map;
//
//public class UploadUtil {
//    private static final Logger LOGGER = LoggerFactory.getLogger(UploadUtil.class);
//
//    public static String uploadMultipartFile(String urlStr, Map<String, String> textMap, Map<String, MultipartFile> fileMap) {
//        String res = "";
//        HttpURLConnection conn = null;
//        String BOUNDARY = "---------------------------123821742118716";
//        try {
//            URL url = new URL(urlStr);
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(50000);
//            conn.setReadTimeout(300000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
//            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
//
//            OutputStream out = new DataOutputStream(conn.getOutputStream());
//            // text
//            if (textMap != null) {
//                StringBuffer strBuf = new StringBuffer();
//                Iterator iter = textMap.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry entry = (Map.Entry) iter.next();
//                    String inputName = (String) entry.getKey();
//                    String inputValue = (String) entry.getValue();
//                    if (inputValue == null) {
//                        continue;
//                    }
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
//                    strBuf.append(inputValue);
//                }
//                out.write(strBuf.toString().getBytes());
//            }
//
//            // file
//            if (fileMap != null) {
//                Iterator iter = fileMap.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry entry = (Map.Entry) iter.next();
//                    String inputName = (String) entry.getKey();
//                    MultipartFile file = (MultipartFile) entry.getValue();
//                    // String inputValue = (String) entry.getValue();
//                    if (file == null) {
//                        continue;
//                    }
//                    // File file = new File(inputValue);
//                    String filename = file.getOriginalFilename();
//                    String contentType = file.getContentType();
//                    if (filename.endsWith(".png")) {
//                        contentType = "image/png";
//                    }
//                    if (contentType == null || contentType.equals("")) {
//                        contentType = "application/octet-stream";
//                    }
//
//                    StringBuffer strBuf = new StringBuffer();
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
//                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
//
//                    out.write(strBuf.toString().getBytes());
//
//                    DataInputStream in = new DataInputStream(file.getInputStream());
//                    int bytes = 0;
//                    byte[] bufferOut = new byte[1024];
//                    while ((bytes = in.read(bufferOut)) != -1) {
//                        out.write(bufferOut, 0, bytes);
//                    }
//                    in.close();
//                }
//            }
//            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
//            out.write(endData);
//            out.flush();
//            out.close();
//
//            StringBuffer strBuf = new StringBuffer();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                strBuf.append(line).append("\n");
//            }
//            res = strBuf.toString();
//            reader.close();
//        } catch (Exception e) {
//            LOGGER.error("发送POST请求出错！urlStr={}", urlStr);
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//        return res;
//    }
//
//    public static String uploadFile(String urlStr, Map<String, String> textMap, Map<String, File> fileMap){
//        String res = "";
//        HttpURLConnection conn = null;
//        String BOUNDARY = "---------------------------123821742118716";
//        try {
//            URL url = new URL(urlStr);
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(50000);
//            conn.setReadTimeout(300000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
//            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
//
//            OutputStream out = new DataOutputStream(conn.getOutputStream());
//            // text
//            if (textMap != null) {
//                StringBuffer strBuf = new StringBuffer();
//                Iterator iter = textMap.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry entry = (Map.Entry) iter.next();
//                    String inputName = (String) entry.getKey();
//                    String inputValue = (String) entry.getValue();
//                    if (inputValue == null) {
//                        continue;
//                    }
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
//                    strBuf.append(inputValue);
////                    strBuf.append(new String(inputValue.getBytes(),"utf-8"));
//                }
//                out.write(strBuf.toString().getBytes("utf-8"));
//            }
//
//            // file
//            if (fileMap != null) {
//                Iterator iter = fileMap.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry entry = (Map.Entry) iter.next();
//                    String inputName = (String) entry.getKey();
//                    File file = (File) entry.getValue();
//                    if (file == null) {
//                        continue;
//                    }
//
//                    String contentType = "application/octet-stream";
//                    StringBuffer strBuf = new StringBuffer();
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + file.getName() + "\"\r\n");
//                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
//
//                    out.write(strBuf.toString().getBytes("utf-8"));
//
//                    DataInputStream in = new DataInputStream(new FileInputStream(file));
//                    int bytes = 0;
//                    byte[] bufferOut = new byte[1024];
//                    while ((bytes = in.read(bufferOut)) != -1) {
//                        out.write(bufferOut, 0, bytes);
//                    }
//                    in.close();
//                }
//            }
//            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
//            out.write(endData);
//            out.flush();
//            out.close();
//
//            StringBuffer strBuf = new StringBuffer();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                strBuf.append(line).append("\n");
//            }
//            res = strBuf.toString();
//            reader.close();
//        } catch (Exception e) {
//            LOGGER.error("发送POST请求出错！urlStr={}", urlStr);
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//        return res;
//    }
//}
