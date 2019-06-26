package cn.onlyhi.client.push;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PushClient {

    private static Logger logger = LoggerFactory.getLogger(PushClient.class);

    // The user agent
    protected final String USER_AGENT = "Mozilla/5.0";

    // This object is used for sending the post request to Umeng
    protected HttpClient client = new DefaultHttpClient();

    // The host
    protected static final String host = "http://msg.umeng.com";

    // The post path
    protected static final String postPath = "/api/send";

    public boolean send(UmengNotification msg) throws Exception {
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        msg.setPredefinedKeyValue("timestamp", timestamp);
        String url = host + postPath;
        String postBody = msg.getPostBody();
        logger.info("postBody : " + postBody);
        String sign = DigestUtils.md5Hex(("POST" + url + postBody + msg.getAppMasterSecret()).getBytes("utf8"));
        url = url + "?sign=" + sign;
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", USER_AGENT);
        StringEntity se = new StringEntity(postBody, "UTF-8");
        post.setEntity(se);
        // Send the post request and get the response
        HttpResponse response = client.execute(post);
        int status = response.getStatusLine().getStatusCode();
        logger.info("Response Code : " + status);
        logger.info("response : {}", response);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        logger.info("result={}", result.toString());
        if (status == 200) {
            logger.info("Notification sent successfully.");
            return true;
        } else {
            logger.info("Failed to send the notification!");
            return false;
        }
    }

}
