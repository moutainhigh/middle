package com.njwd.utils;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口调用
 *
 * @author luoY
 * @since 2019-10-30
 */
@SuppressWarnings("all")
@Component
public class HttpUtils {
    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static RestTemplate CLIENT;
    @Resource
    private RestTemplate restTemplate;
    private static String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};
    /**
     * RestTemplate 表单提交
     */
    public static <T, T2> T restPost(String url, Map<String, String> headers, Map<String, T2> body, Class<T> cls) {
        HttpHeaders header = new HttpHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                header.add(entry.getKey(), entry.getValue());
            }
        }
        // 提交方式:表单提交
        header.set("Accept", "application/x-www-form-urlencoded;charset=UTF-8");
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (body != null && !body.isEmpty()) {
            for (Map.Entry<String, T2> entry : body.entrySet()) {
                if (entry.getValue() != null) {
                    params.add(entry.getKey(), entry.getValue() instanceof String ? (String) entry.getValue() : String.valueOf(entry.getValue()));
                }
            }
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, header);
        logger.info("发送httpPost请求：{}，参数：{}", url, JsonUtils.object2Str(JsonUtils.NON_NULL_MAPPER, requestEntity.getBody()));
        // 执行HTTP请求
        ResponseEntity<T> response = CLIENT.exchange(url, HttpMethod.POST, requestEntity, cls);
        // 输出结果
        logger.info(JsonUtils.object2Str(JsonUtils.NON_NULL_MAPPER, response.getBody()));
        return response.getBody();
    }

    /**
     * RestTemplate 表单提交 直接传递String请求体
     */
    public static <T, T2> T restPostWithBody(String url, Map<String, String> headers, String body, Class<T> cls) {
        HttpHeaders header = new HttpHeaders();
        header.set("Accept", "application/x-www-form-urlencoded;charset=UTF-8");
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                header.add(entry.getKey(), entry.getValue());
            }
        }
        // 提交方式:表单提交
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, header);
        logger.info("发送httpPost请求：{}，参数：{}", url, JsonUtils.object2Str(JsonUtils.NON_NULL_MAPPER, requestEntity.getBody()));
        // 执行HTTP请求
        ResponseEntity<T> response = CLIENT.exchange(url, HttpMethod.POST, requestEntity, cls);
        // 输出结果
        logger.info(JsonUtils.object2Str(JsonUtils.NON_NULL_MAPPER, response.getBody()));
        return response.getBody();
    }

    public static String restPostJson(String url, String jsonObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/x-www-form-urlencoded;charset=UTF-8");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(jsonObject, headers);
        logger.info("发送httpPost请求：{}，Json参数：{}", url, entity.getBody());
        String jsonResult = CLIENT.postForObject(url, entity, String.class);
        logger.info(jsonResult);
        return jsonResult;
    }

    //加入传header参数
    public static <T> T restGet(String url, Map<String, String> headers,Class<T> cls, Map<String, String> params) {
        StringBuilder concatUrl = new StringBuilder(url);
        if (params != null && params.size() != 0) {
            if (!url.contains("?")) {
                concatUrl.append("?");
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                concatUrl.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        HttpHeaders header = new HttpHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                header.add(entry.getKey(), entry.getValue());
            }
        }
        header.set("Accept", "application/x-www-form-urlencoded;charset=UTF-8");
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<T> entity = new HttpEntity<>(null, header);
        url = concatUrl.toString();
        logger.info("发送httpGet请求：{}", url);
        // 执行HTTP请求
        ResponseEntity<T> response = CLIENT.exchange(url, HttpMethod.GET, entity, cls);
        HttpHeaders headerssss = response.getHeaders();
        List<String> set_cookie = headerssss.get(HttpHeaders.SET_COOKIE);
        // 输出结果
        logger.info(JsonUtils.object2Str(JsonUtils.NON_NULL_MAPPER, response.getBody()));
        return response.getBody();
    }
    //获取cookies数据
    public static   <T> T restGetCookie(String url, Class<T> cls, Map<String, String> params) {
        StringBuilder concatUrl = new StringBuilder(url);
        if (params != null && params.size() != 0) {
            if (!url.contains("?")) {
                concatUrl.append("?");
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                concatUrl.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        HttpHeaders header = new HttpHeaders();
        header.set("Accept", "application/x-www-form-urlencoded;charset=UTF-8");
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<T> entity = new HttpEntity<>(null, header);
        url = concatUrl.toString();
        logger.info("发送httpGet请求：{}", url);
        // 执行HTTP请求
        ResponseEntity<T> response = CLIENT.exchange(url, HttpMethod.GET, entity, cls);
        HttpHeaders headerssss = response.getHeaders();
        List<String> set_cookie = headerssss.get(HttpHeaders.SET_COOKIE);
        StringBuffer tmpcookies = new StringBuffer();
        for (String c : set_cookie) {
            tmpcookies.append(c.toString() + ";");
        }
        logger.info("cookies = "+tmpcookies.toString());
        return (T) tmpcookies.toString();
    }
    public static <T> T restGetWithJson(String url, Map<String, String> header,Class<T> cls, String json) {
        HttpHeaders headers = new HttpHeaders();
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
//        headers.set("Accept", "application/x-www-form-urlencoded;charset=UTF-8");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> entity = new HttpEntity<>(null, headers);
        logger.info("发送httpGet请求：{}：json：{}", url, json);
        // 执行HTTP请求
        ResponseEntity<T> response = CLIENT.exchange(url, HttpMethod.GET, entity, cls, json);
        // 输出结果
        logger.info(JsonUtils.object2Str(JsonUtils.NON_NULL_MAPPER, response.getBody()));
        return response.getBody();
    }
    /**
     * 发送HttpGet请求
     * @param url
     * @return
     */
    public static String sendGet(String url,String tmpcookies) {
        //1.获得一个httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //2.生成一个get请求
        HttpGet httpget = new HttpGet(url);
        if(null != tmpcookies){
            httpget.setHeader("Connection","keep-alive");
            httpget.addHeader(new BasicHeader("Cookie", tmpcookies));
        }
        CloseableHttpResponse response = null;
        try {
            //3.执行get请求并返回结果
            response = httpclient.execute(httpget);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            //4.处理结果，这里将结果返回为字符串
            org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /*
    * @description: multipart form-data模式提交
    * @Param
    * @return
    * @author shenhuafu
    * @dte 2019/11/13 18:00
    */
    public static String restPost(String url, Map<String, String> req) {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        String respContent = null;
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        if (req != null && !req.isEmpty()) {
            for (Map.Entry<String, String> entry : req.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue(), ContentType.MULTIPART_FORM_DATA);
            }
        }
        org.apache.http.HttpEntity multipart = builder.build();
        HttpResponse resp = null;
        try {
            httpPost.setEntity(multipart);
            resp = client.execute(httpPost);
            org.apache.http.HttpEntity response = resp.getEntity();
            respContent = EntityUtils.toString(response, "UTF-8");
        } catch (Exception e) {
        }
        return   respContent;
    }
    @PostConstruct
    private void init() {
        CLIENT = restTemplate;
    }

    /**
     * 获取实际ip
     *
     * @param request request
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if ("127.0.0.1".equals(ipAddress)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet != null ? inet.getHostAddress() : null;
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // 一个ip长度:"***.***.***.***".length()==15
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    /**
     * 获取原header
     *
     * @param request request
     * @return headers
     */
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        return headers;
    }

    /**
     * 做出远程请求
     *
     * @param url
     *            连接地址
     * @param content
     *            请求内容
     * @return
     */
    public static String doRequest(String httpPath, String url, String content) {
        StringBuffer result = new StringBuffer();
        try {
            content = new String(content.getBytes("UTF-8"), "iso-8859-1");// 避免存在中文传输
            URL postUrl = new URL(httpPath + url);// URL
            HttpURLConnection connection = (HttpURLConnection) postUrl
                    .openConnection();// 建立连接
            connection.setDoOutput(true);// 可以输出
            connection.setDoInput(true);// 可输入
            connection.setRequestMethod("POST");// post方式
            connection.setUseCaches(false);// 不可用缓存
            connection.setInstanceFollowRedirects(true);//
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");// 其它请求属性
            connection.setRequestProperty("Accept",
                    "application/x-www-form-urlencoded;charset=UTF-8");// 其它请求属性
            connection.setRequestProperty("User-Agent",
                    "Apache-HttpClient/UNAVAILABLE (java 1.4)");// 其它请求属性
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());// 建立请求流
            out.writeBytes(content); // 请求发送
            out.close(); // 输出释放

            // 以下为获得请求结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
            String line = "";
            while ((line = reader.readLine()) != null) {// 循环读取缓存
                result.append(line);
            }
            reader.close();// 缓存释放
            connection.disconnect();// 连接释放

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
