package cn.onlyhi.client.baidustaging;

import cn.onlyhi.common.util.ChuangLanSmsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 发送http请求
 * 
 * @author: 周爽
 * @date: 2016-11-30
 */
public class HttpRequestUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtil.class);
	/**
	 * 发送http请求
	 * 
	 * @author 周爽
	 * @date : 2016-11-30
	 * @param url
	 * @param method
	 * @param param
	 * @return
	 * @return: String
	 */
	public static String sendRequest(String url, String method, String param) throws Exception {
		byte[] inData = sendRequestGetBytes(url, method, param);
		if (inData != null) {
			return new String(inData);
		}
		return null;
	}

	public static byte[] sendRequestGetBytes(String url, String method, String param) throws Exception {

		if (StringUtils.isBlank(url)) {
			throw new Exception("URL不能为空!");
		}

		if (StringUtils.isBlank(method)) {
			throw new Exception("URL不能为空!");
		} else {
			if (!(method.equalsIgnoreCase("get") || method.equalsIgnoreCase("post"))) {
				throw new Exception("method只能位post或get");
			}
		}

		HttpURLConnection conn = null;
		OutputStream out = null;
		InputStream in = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();

			method = method.toUpperCase();
			conn.setRequestMethod(method);
			conn.setReadTimeout(30 * 1000);
			conn.setDoInput(true);

			if ("POST".equals(method)) {

				if (StringUtils.isBlank(param)) {
					throw new Exception("请求参数param不能为空!");
				}

				conn.setDoOutput(true);
				out = conn.getOutputStream();
				byte[] outData = param.getBytes();
				out.write(outData);
			}

			in = conn.getInputStream();

			byte[] tmp = new byte[1024];

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int len = 0;
			while (-1 != (len = in.read(tmp))) {
				buffer.write(tmp, 0, len);
			}

			buffer.flush();
			byte[] inData = buffer.toByteArray();

			return inData;
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (null != out) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}

			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
			if (null != conn) {
				conn.disconnect();
			}
		}
		return null;
	}

	/**
     * 百度金融
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LOGGER.error("发送 POST 请求出现异常！"+e.getMessage());
		}
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
	 * 发起http请求获取返回结果
	 * JSON 解析
	 * @param requestUrl 请求地址
	 * @return
	 */
	public static String httpRequest(String requestUrl) {
		StringBuffer buffer = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

			httpUrlConn.setDoOutput(false);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);

			httpUrlConn.setRequestMethod("GET");
			httpUrlConn.connect();

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();

		} catch (Exception e) {
		}
		return buffer.toString();
	}

	
	/** 
     * 发送http请求取得返回的输入流 
     *  XML 解析
     * @param requestUrl 请求地址 
     * @return InputStream 
     */  
    public static InputStream IhttpRequest(String requestUrl) {  
        InputStream inputStream = null;  
        try {  
            URL url = new URL(requestUrl);  
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();  
            httpUrlConn.setDoInput(true);  
            httpUrlConn.setRequestMethod("GET");  
            httpUrlConn.connect();  
            // 获得返回的输入流  
            inputStream = httpUrlConn.getInputStream();  
        } catch (Exception e) {  
			LOGGER.error(e.getMessage());
        }  
        return inputStream;  
    }  
	/**
	 * utf编码
	 * 
	 * @param source
	 * @return
	 */
	public static String urlEncodeUTF8(String source) {
		String result = source;
		try {
			result = URLEncoder.encode(source, "utf-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}
	/**
	 * 获得项目路径
	 */
	public static String getLocation(HttpServletRequest request){
		String path = request.getContextPath();
		path = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/static";
		return path;
	}
	
	 /**
    *
    * @param strUrl 请求地址
    * @param params 请求参数
    * @param method 请求方法
    * @return  网络请求字符串
    * @throws Exception
    */
	public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
 
   public static String net(String strUrl, Map<String, Object> params,String method) throws Exception {
       HttpURLConnection conn = null;
       BufferedReader reader = null;
       String rs = null;
       try {
           StringBuffer sb = new StringBuffer();
           if(method==null || method.equals("GET")){
               strUrl = strUrl+"?"+urlencode(params);
           }
           URL url = new URL(strUrl);
           conn = (HttpURLConnection) url.openConnection();
           if(method==null || method.equals("GET")){
               conn.setRequestMethod("GET");
           }else{
               conn.setRequestMethod("POST");
               conn.setDoOutput(true);
           }
           conn.setRequestProperty("User-agent", userAgent);
           conn.setUseCaches(false);
           conn.setConnectTimeout(DEF_CONN_TIMEOUT);
           conn.setReadTimeout(DEF_READ_TIMEOUT);
           conn.setInstanceFollowRedirects(false);
           conn.connect();
           if (params!= null && method.equals("POST")) {
               try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                   out.writeBytes(urlencode(params));
               }
           }
           InputStream is = conn.getInputStream();
           reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
           String strRead = null;
           while ((strRead = reader.readLine()) != null) {
               sb.append(strRead);
           }
           rs = sb.toString();
       } catch (IOException e) {
		   LOGGER.error(e.getMessage());
       } finally {
           if (reader != null) {
               reader.close();
           }
           if (conn != null) {
               conn.disconnect();
           }
       }
       return rs;
   }
   
 //将map型转为请求参数型
   public static String urlencode(Map<String, ?> data) {
       StringBuilder sb = new StringBuilder();
       for (Map.Entry<String, ?> i : data.entrySet()) {
           try {
               sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
           } catch (UnsupportedEncodingException e) {
               LOGGER.error(e.getMessage());
           }
       }
       return sb.toString();
   }
}
