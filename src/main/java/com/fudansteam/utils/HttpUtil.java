package com.fudansteam.utils;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author : 箱子
 * Description : description
 * Created by 箱子 on 2021-03-21 00:54:35
 * Copyright 2021 HDU_IES. All rights reserved.
 */
public class HttpUtil {
    
    public static String sendGet(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        HttpGet httpGet = null;
        String body;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            List<String> mapList = new ArrayList<>();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    mapList.add(entry.getKey() + "=" + entry.getValue());
                }
            }
            if (mapList.size() != 0) {
                url = url + "?";
                String paramsStr = StringUtils.join(mapList, "&");
                url = url + paramsStr;
            }
            httpGet = new HttpGet(url);
            httpGet.setHeader("Content-type", "application/json; charset=utf-8");
            httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpResponse response = httpClient.execute(httpGet);
            body = EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return body;
    }
    
    public static Map<String, String> urlParser(String url) {
        Map<String, String> paramsMap = new HashMap<>();
        String strUrl;
        String strUrlParams;
        if (url.contains("?")) {
            String[] strUrlPatten = url.split("\\?");
            strUrl = strUrlPatten[0];
            strUrlParams = strUrlPatten[1];
        } else {
            strUrl = url;
            strUrlParams = url;
        }
        paramsMap.put("URL", strUrl);
        String[] params;
        if (strUrlParams.contains("&")) {
            params = strUrlParams.split("&");
        } else {
            params = new String[]{strUrlParams};
        }
        for (String p : params) {
            if (p.contains("=")) {
                String[] param = p.split("=");
                if (param.length == 1) {
                    paramsMap.put(param[0], "");
                } else {
                    String key = param[0];
                    String value = param[1];
                    paramsMap.put(key, value);
                }
            } else {
                paramsMap.put("errorParam", p);
            }
        }
        return paramsMap;
    }
    
    public static String sendPostForm(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        HttpPost httpPost = null;
        String body;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            List<NameValuePair> pairs = new ArrayList<>();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            body = EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
        return body;
    }
    
    public static ByteArrayInputStream sendPost(String url, Map<String, String> params) {
        InputStream inputStream;
        ByteArrayInputStream byteArrayInputStream = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-type", "application/json; charset=utf-8");
        httppost.setHeader("Accept", "application/json");
        try {
            StringEntity s = new StringEntity(new Gson().toJson(params), StandardCharsets.UTF_8);
            s.setContentEncoding("UTF-8");
            httppost.setEntity(s);
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                inputStream.close();
                byteArrayInputStream = new ByteArrayInputStream(outStream.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayInputStream;
    }
    
}
