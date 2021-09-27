package com.jd.bluedragon.core.aces;

import com.jd.security.tdeclient.TDEClient;
import org.junit.Test;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.aces
 * @ClassName: TdeServiceTest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/9/23 15:41
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class TdeServiceTest {

    private static final String token = "eyJzaWciOiJaYXJPN09MU3hLc0JDQXcvaUVvc1MyMTEzR3VNREtwMmQ0Nkk1K29CY1hXZUNqVXdUd200bFc3R2FqL211WU1uQllWYWFibzZpVVphUFVOcWswRVczalZ3TjQrbzkyQ1VFWXdWSTJ3eEZwQ3UxZGhnUE5OUDhtSHBCRTdGaHNRSjh6YnFVZGVudUVKUzNBZGVMWk9kTlhncTR6L0ZnRFZQWDcyeUNRTkIzblpRb3B5eDk2d2gzSThhNHdoSWJ1SVE2WDJBN01rcUpoaFRubnZGclA2TFhPRTVGY045Y2NWZ1Ywc21UWnMzSHdWcHJZbm5MNnVuVGlRMG42cFloOHAwbnpBZWlUVFZud0oxWUdKelBDZW9rOUI3c3AralpMUjBZQ3JyUmVtRmJERWVYYXUzb3lEK3BJR2tUbmxqRUg2MGZBQXVYSzkyMFpZS1VDMEJObmNkcXc9PSIsImRhdGEiOnsiYWN0IjoiY3JlYXRlIiwiZWZmZWN0aXZlIjoxNjMyMjQwMDAwMDAwLCJleHBpcmVkIjoxNjk1MzEyMDAwMDAwLCJpZCI6Ik9XTTBZek5sWm1NdFpEaG1aaTAwWVRRNExXRXdOemd0TWpnek5XVmhNMk0yTTJJMiIsImtleSI6IjkvSE1scGdKRlJsMnlqY1pKelU4OHJTdTZtNnJyV0lwYjBMbGlZNEs2aVk9Iiwic2VydmljZSI6ImRtc19ldG1zX2JjMmNuUmlzIiwic3R5cGUiOjJ9LCJleHRlcm5hbERhdGEiOnsiem9uZSI6IkNOLTAifX0=";

    private String encrypted = "AARCpxhmmSbt/6FvQVUb8crySY9YAtdbhB86O/Qz+EweVbQ39Mw=";
    private String input = "test";

    @Test
    public void test() throws Exception {
        // TDEClient客户端
        TDEClient client = null;

        // 请替换为自己从ACES-WEB平台获取的Token
        String base64_token = token;
        // 环境标识，线上环境设为true，测试环境设为false
        // 测试环境还需配置系统hosts：[10.170.180.106 beta.kms.tde.jd.local] 或 [11.50.60.26 beta.kms.tde.jd.local]
        boolean isProd = false;

        try {
            // #1-初始化TDEClient客户端
            client = TDEClient.getInstance(base64_token, isProd);
            // #2-调用字符串可逆加密API
//            String encrypted = client.encryptString(input);
            // #3-校验密文是否可以被解密
            if (client.isDecryptable(encrypted) == TDEClient.CipherStatus.Decryptable) {
                // #4-调用字符串解密API
                String decrypted = client.decryptString(encrypted);
                if (input.equals(decrypted)) {
                    System.out.println("Test case passed.");
                } else {
                    System.err.println("Test case failed.");
                }
            } else {
                System.err.println("Test case failed.");
            }
            // #5-BDP SDK调用不可逆加密API，计算sha256索引
            // 此API需事先申请大数据业务的解密权限，请咚咚联系[xnaces]获取大数据业务标识码并审批
            String index = client.bdpIndex("18846448756");
            if (index.equals("k+NA/ctKU9IbIhZkzESVvDhqHLP3U361eqqju3Yrruw=")) {
                System.out.println("BDP index calculation test case passed.");
            } else {
                System.err.println("BDP index calculation test case failed.");
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

}