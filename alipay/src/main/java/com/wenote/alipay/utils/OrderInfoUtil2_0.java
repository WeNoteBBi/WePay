package com.wenote.alipay.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class OrderInfoUtil2_0 {

    /**
     * 构造授权参数列表
     *
     * @param pid
     * @param app_id
     * @param target_id
     * @return
     */
    public static LinkedHashMap<String, String> buildAuthInfoMap(String pid, String app_id, String target_id, boolean rsa2) {
        LinkedHashMap<String, String> keyValues = new LinkedHashMap<String, String>();

        // 商户签约拿到的app_id，如：2013081700024223
        keyValues.put("app_id", app_id);

        // 商户签约拿到的pid，如：2088102123816631
        keyValues.put("pid", pid);

        // 服务接口名称， 固定值
        keyValues.put("apiname", "com.alipay.account.auth");

        // 商户类型标识， 固定值
        keyValues.put("app_name", "mc");

        // 业务类型， 固定值
        keyValues.put("biz_type", "openservice");

        // 产品码， 固定值
        keyValues.put("product_id", "APP_FAST_LOGIN");

        // 授权范围， 固定值
        keyValues.put("scope", "kuaijie");

        // 商户唯一标识，如：kkkkk091125
        keyValues.put("target_id", target_id);

        // 授权类型， 固定值
        keyValues.put("auth_type", "AUTHACCOUNT");

        // 签名类型
        keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");

        return keyValues;
    }

    /**
     * 构造支付订单参数列表
     * @param app_id
     * @param payInfo
     * @return
     */
    public static Map<String, String> buildOrderParamMap(String app_id, boolean rsa2, String payInfo) {
        String params[] = payInfo.split("&");
        String appid[] = params[1].split("=");
        String biz_content[] = params[2].split("=");
        String charset[] = params[3].split("=");
        String format[] = params[4].split("=");
        String method[] = params[5].split("=");
        String notify_url[] = params[6].split("=");
        String sign[] = params[7].split("=");
        String sign_type[] = params[8].split("=");
        String timestamp[] = params[9].split("=");
        String version[] = params[10].split("=");
        Map<String, String> keyValues = new HashMap<String, String>();

        keyValues.put("app_id", app_id);

        keyValues.put("biz_content", biz_content[1]);

        keyValues.put("charset", "utf-8");

        keyValues.put("notify_url", notify_url[1]);

        keyValues.put("method", method[1]);

        keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");

        keyValues.put("timestamp", timestamp[1]);

        keyValues.put("version", version[1]);

//		Map<String, String> keyValues = new HashMap<String, String>();
//
//		keyValues.put("app_id", app_id);
//
//		keyValues.put("biz_content", "{\"body\":\"羊城通宝9001770400充值0.01元\",\"out_trade_no\":\"35141004201807230932488739818001\",\"passback_params\":\"channel_code%3D70000016%26user_id%3D9001770400%26product_source%3D1004%26card_num%3D0\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"subject\":\"羊城通宝9001770400充值0.01元\",\"timeout_express\":\"2h\",\"total_amount\":\"0.01\"}");
//
//		keyValues.put("charset", "utf-8");
//
//		keyValues.put("method", "alipay.trade.app.pay");
//
//		keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");
//
//		keyValues.put("timestamp", "2018-07-23 09:32:50");
//
//		keyValues.put("version", "1.0");

        return keyValues;
    }

    /**
     * 构造支付订单参数信息
     *
     * @param map
     * 支付订单参数
     * @return
     */
    public static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * 对支付参数信息进行签名
     *
     * @param map
     *            待签名授权信息
     *
     * @return
     */
    public static String getSign(Map<String, String> map, String rsaKey, boolean rsa2) {
        List<String> keys = new ArrayList<String>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, false));

        String oriSign = SignUtils.sign(authInfo.toString(), rsaKey, rsa2);
        String encodedSign = "";

        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "sign=" + encodedSign;
    }

    /**
     * 要求外部订单号必须唯一。
     * @return
     */
    private static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

}
