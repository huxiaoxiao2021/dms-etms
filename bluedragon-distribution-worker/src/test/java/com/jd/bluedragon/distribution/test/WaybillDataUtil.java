package com.jd.bluedragon.distribution.test;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;

import java.lang.reflect.Type;
import java.util.Date;

public class WaybillDataUtil {


    static Gson gson;

    static {

        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        gson = builder.create();

    }

    //仓配 运单
    static String waybillCANGPJson = "{\n" +
            "    \"resultCode\": 1,\n" +
            "    \"data\": {\n" +
            "        \"waybill\": {\n" +
            "            \"cid\": 1166240995931607040,\n" +
            "            \"eid\": 1166240995965161472,\n" +
            "            \"waybillCode\": \"JDVA00024194366\",\n" +
            "            \"vendorId\": \"36271732635\",\n" +
            "            \"arriveAreaId\": 6,\n" +
            "            \"arriveArea\": \"总公司\",\n" +
            "            \"consigner\": \"刘诗诗\",\n" +
            "            \"consignerMobile\": \"13745456767\",\n" +
            "            \"consignerAddress\": \"北京朝阳区三环到四环之间分拣中心自动化运单下单地址\",\n" +
            "            \"receiverName\": \"赵丽颖\",\n" +
            "            \"receiverMobile\": \"13899131888\",\n" +
            "            \"receiverAddress\": \"北京五环外大兴亦庄荣京东街朝林广场\",\n" +
            "            \"goodNumber\": 5,\n" +
            "            \"volumeFormula\": \"7*8*9\",\n" +
            "            \"goodVolume\": 555.56,\n" +
            "            \"goodWeight\": 14.86,\n" +
            "            \"requireTime\": 1566973817000,\n" +
            "            \"importantHint\": \"三方订单号imp：ESL4398056124588\",\n" +
            "            \"freight\": \"9.99\",\n" +
            "            \"codMoney\": \"31.8\",\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"yn\": 1,\n" +
            "            \"waybillType\": 10000,\n" +
            "            \"sendPay\": \"10000000000000000000000000010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"payment\": 1,\n" +
            "            \"provinceId\": 1,\n" +
            "            \"cityId\": 72,\n" +
            "            \"countryId\": 2799,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"oldSiteId\": 39,\n" +
            "            \"busiId\": 31201,\n" +
            "            \"busiName\": \"次晨达\",\n" +
            "            \"sendCityId\": 72,\n" +
            "            \"againWeight\": 6.88,\n" +
            "            \"transferStationId\": 0,\n" +
            "            \"distributeStoreId\": 21637,\n" +
            "            \"distributeType\": 70,\n" +
            "            \"parentOrderId\": \"222230233656214884019886\",\n" +
            "            \"firstTime\": 1566888495000,\n" +
            "            \"busiOrderCode\": \"ESL222641235746\",\n" +
            "            \"sourceCode\": \"1006\",\n" +
            "            \"waybillSign\": \"00001000110009000000000000202100000020030002005000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"senderCompany\": \"北京京东北辰冬装有限公司\"\n" +
            "        },\n" +
            "        \"waybillState\": {\n" +
            "            \"mId\": 1166240996015493120,\n" +
            "            \"waybillCode\": \"JDVA00024194366\",\n" +
            "            \"waybillState\": 1,\n" +
            "            \"signState\": 0,\n" +
            "            \"signHasReturn\": 0,\n" +
            "            \"waybillType\": 0,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"createSite\": 39,\n" +
            "            \"laterUser\": \"system\",\n" +
            "            \"yn\": 1,\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"waybillFlag\": 1,\n" +
            "            \"siteId\": 39,\n" +
            "            \"storeId\": 98,\n" +
            "            \"cky2\": 6,\n" +
            "            \"operateTime\": 1566888496000\n" +
            "        },\n" +
            "        \"packageList\": [\n" +
            "            {\n" +
            "                \"packageId\": 1166240996355231744,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-1-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996376203264,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-2-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996388786176,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-3-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996397174784,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-4-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996409757696,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-5-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            }\n" +
            "        ],\n" +
            "        \"goodsList\": [{\n" +
            "                \"goodId\": 1173497822029291520,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品1\",\n" +
            "                \"sku\": \"EMG4398059210869\",\n" +
            "                \"goodCount\": 5,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"goodId\": 1173497822029291521,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品2\",\n" +
            "                \"sku\": \"EMG4398059210945\",\n" +
            "                \"goodCount\": 10,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"goodId\": 1173497822029291522,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品3\",\n" +
            "                \"sku\": \"EMG4398059210947\",\n" +
            "                \"goodCount\": 15,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 1\n" +
            "            }]\n" +
            "    },\n" +
            "    \"message\": \"接口调用成功\"\n" +
            "}";

    //纯配 运单
    static String waybillCHUNPJson = "{\n" +
            "    \"resultCode\": 1,\n" +
            "    \"data\": {\n" +
            "        \"waybill\": {\n" +
            "            \"cid\": 1166240995931607040,\n" +
            "            \"eid\": 1166240995965161472,\n" +
            "            \"waybillCode\": \"JDVA00024194366\",\n" +
            "            \"vendorId\": \"36271732635\",\n" +
            "            \"arriveAreaId\": 6,\n" +
            "            \"arriveArea\": \"总公司\",\n" +
            "            \"consigner\": \"刘诗诗\",\n" +
            "            \"consignerMobile\": \"13745456767\",\n" +
            "            \"consignerAddress\": \"北京朝阳区三环到四环之间分拣中心自动化运单下单地址\",\n" +
            "            \"receiverName\": \"赵丽颖\",\n" +
            "            \"receiverMobile\": \"13899131888\",\n" +
            "            \"receiverAddress\": \"北京五环外大兴亦庄荣京东街朝林广场\",\n" +
            "            \"goodNumber\": 5,\n" +
            "            \"volumeFormula\": \"7*8*9\",\n" +
            "            \"goodVolume\": 555.56,\n" +
            "            \"goodWeight\": 14.86,\n" +
            "            \"requireTime\": 1566973817000,\n" +
            "            \"importantHint\": \"三方订单号imp：ESL4398056124588\",\n" +
            "            \"freight\": \"9.99\",\n" +
            "            \"codMoney\": \"31.8\",\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"yn\": 1,\n" +
            "            \"waybillType\": 10000,\n" +
            "            \"sendPay\": \"10000000000000000000000000010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"payment\": 1,\n" +
            "            \"provinceId\": 1,\n" +
            "            \"cityId\": 72,\n" +
            "            \"countryId\": 2799,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"oldSiteId\": 39,\n" +
            "            \"busiId\": 31201,\n" +
            "            \"busiName\": \"次晨达\",\n" +
            "            \"sendCityId\": 72,\n" +
            "            \"againWeight\": 6.88,\n" +
            "            \"transferStationId\": 0,\n" +
            "            \"distributeStoreId\": 21637,\n" +
            "            \"distributeType\": 70,\n" +
            "            \"parentOrderId\": \"222230233656214884019886\",\n" +
            "            \"firstTime\": 1566888495000,\n" +
            "            \"busiOrderCode\": \"222641235746\",\n" +
            "            \"sourceCode\": \"1006\",\n" +
            "            \"waybillSign\": \"20001000110009000000000000202100000020030002005000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"senderCompany\": \"北京京东北辰冬装有限公司\"\n" +
            "        },\n" +
            "        \"waybillState\": {\n" +
            "            \"mId\": 1166240996015493120,\n" +
            "            \"waybillCode\": \"JDVA00024194366\",\n" +
            "            \"waybillState\": 1,\n" +
            "            \"signState\": 0,\n" +
            "            \"signHasReturn\": 0,\n" +
            "            \"waybillType\": 0,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"createSite\": 39,\n" +
            "            \"laterUser\": \"system\",\n" +
            "            \"yn\": 1,\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"waybillFlag\": 1,\n" +
            "            \"siteId\": 39,\n" +
            "            \"storeId\": 98,\n" +
            "            \"cky2\": 6,\n" +
            "            \"operateTime\": 1566888496000\n" +
            "        },\n" +
            "        \"packageList\": [\n" +
            "            {\n" +
            "                \"packageId\": 1166240996355231744,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-1-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996376203264,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-2-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996388786176,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-3-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996397174784,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-4-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996409757696,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-5-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            }\n" +
            "        ],\n" +
            "        \"goodsList\": [{\n" +
            "                \"goodId\": 1173497822029291520,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品1\",\n" +
            "                \"sku\": \"EMG4398059210869\",\n" +
            "                \"goodCount\": 5,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"goodId\": 1173497822029291521,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品2\",\n" +
            "                \"sku\": \"EMG4398059210945\",\n" +
            "                \"goodCount\": 10,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"goodId\": 1173497822029291522,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品3\",\n" +
            "                \"sku\": \"EMG4398059210947\",\n" +
            "                \"goodCount\": 15,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 1\n" +
            "            }]\n" +
            "    },\n" +
            "    \"message\": \"接口调用成功\"\n" +
            "}";

    //C2C 运单
    static String waybillC2CJson = "{\n" +
            "    \"resultCode\": 1,\n" +
            "    \"data\": {\n" +
            "        \"waybill\": {\n" +
            "            \"cid\": 1166240995931607040,\n" +
            "            \"eid\": 1166240995965161472,\n" +
            "            \"waybillCode\": \"JDVA00024194366\",\n" +
            "            \"vendorId\": \"36271732635\",\n" +
            "            \"arriveAreaId\": 6,\n" +
            "            \"arriveArea\": \"总公司\",\n" +
            "            \"consigner\": \"刘诗诗\",\n" +
            "            \"consignerMobile\": \"13745456767\",\n" +
            "            \"consignerAddress\": \"北京朝阳区三环到四环之间分拣中心自动化运单下单地址\",\n" +
            "            \"receiverName\": \"赵丽颖\",\n" +
            "            \"receiverMobile\": \"13899131888\",\n" +
            "            \"receiverAddress\": \"北京五环外大兴亦庄荣京东街朝林广场\",\n" +
            "            \"goodNumber\": 5,\n" +
            "            \"volumeFormula\": \"7*8*9\",\n" +
            "            \"goodVolume\": 555.56,\n" +
            "            \"goodWeight\": 14.86,\n" +
            "            \"requireTime\": 1566973817000,\n" +
            "            \"importantHint\": \"三方订单号imp：ESL4398056124588\",\n" +
            "            \"freight\": \"9.99\",\n" +
            "            \"codMoney\": \"31.8\",\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"yn\": 1,\n" +
            "            \"waybillType\": 10000,\n" +
            "            \"sendPay\": \"10000000000000000000000000010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"payment\": 1,\n" +
            "            \"provinceId\": 1,\n" +
            "            \"cityId\": 72,\n" +
            "            \"countryId\": 2799,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"oldSiteId\": 39,\n" +
            "            \"busiId\": 31201,\n" +
            "            \"busiName\": \"次晨达\",\n" +
            "            \"sendCityId\": 72,\n" +
            "            \"againWeight\": 6.88,\n" +
            "            \"transferStationId\": 0,\n" +
            "            \"distributeStoreId\": 21637,\n" +
            "            \"distributeType\": 70,\n" +
            "            \"parentOrderId\": \"222230233656214884019886\",\n" +
            "            \"firstTime\": 1566888495000,\n" +
            "            \"busiOrderCode\": \"222641235746\",\n" +
            "            \"sourceCode\": \"1006\",\n" +
            "            \"waybillSign\": \"20001000110009000000000000208100000020030002005000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"senderCompany\": \"北京京东北辰冬装有限公司\"\n" +
            "        },\n" +
            "        \"waybillState\": {\n" +
            "            \"mId\": 1166240996015493120,\n" +
            "            \"waybillCode\": \"JDVA00024194366\",\n" +
            "            \"waybillState\": 1,\n" +
            "            \"signState\": 0,\n" +
            "            \"signHasReturn\": 0,\n" +
            "            \"waybillType\": 0,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"createSite\": 39,\n" +
            "            \"laterUser\": \"system\",\n" +
            "            \"yn\": 1,\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"waybillFlag\": 1,\n" +
            "            \"siteId\": 39,\n" +
            "            \"storeId\": 98,\n" +
            "            \"cky2\": 6,\n" +
            "            \"operateTime\": 1566888496000\n" +
            "        },\n" +
            "        \"packageList\": [\n" +
            "            {\n" +
            "                \"packageId\": 1166240996355231744,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-1-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996376203264,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-2-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996388786176,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-3-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996397174784,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-4-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"packageId\": 1166240996409757696,\n" +
            "                \"packageBarcode\": \"JDVA00024194366-5-5-\",\n" +
            "                \"waybillCode\": \"JDVA00024194366\",\n" +
            "                \"goodWeight\": 7.89,\n" +
            "                \"goodVolume\": \"5.55\",\n" +
            "                \"remark\": \"分拣中心自动化下单\",\n" +
            "                \"createTime\": 1566888495000,\n" +
            "                \"updateTime\": 1566888495000,\n" +
            "                \"yn\": 1,\n" +
            "                \"againWeight\": 6.88,\n" +
            "                \"packTime\": 1566887416000,\n" +
            "                \"flag\": 1\n" +
            "            }\n" +
            "        ],\n" +
            "        \"goodsList\": [{\n" +
            "                \"goodId\": 1173497822029291520,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品1\",\n" +
            "                \"sku\": \"EMG4398059210869\",\n" +
            "                \"goodCount\": 5,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"goodId\": 1173497822029291521,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品2\",\n" +
            "                \"sku\": \"EMG4398059210945\",\n" +
            "                \"goodCount\": 10,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"goodId\": 1173497822029291522,\n" +
            "                \"waybillCode\": \"JDVC00000922021\",\n" +
            "                \"goodName\": \"测试商品3\",\n" +
            "                \"sku\": \"EMG4398059210947\",\n" +
            "                \"goodCount\": 15,\n" +
            "                \"createTime\": 1568618658000,\n" +
            "                \"updateTime\": 1568618658000,\n" +
            "                \"yn\": 1,\n" +
            "                \"snManage\": 1\n" +
            "            }]\n" +
            "    },\n" +
            "    \"message\": \"接口调用成功\"\n" +
            "}";

    //仓配 运单 换单后得原单
    static String waybillCANGPReturnJson  = "{\n" +
            "    \"resultCode\": 1,\n" +
            "    \"data\": {\n" +
            "            \"cid\": 1166240995931607040,\n" +
            "            \"eid\": 1166240995965161472,\n" +
            "            \"waybillCode\": \"JDVA00024194366\",\n" +
            "            \"vendorId\": \"36271732635\",\n" +
            "            \"arriveAreaId\": 6,\n" +
            "            \"arriveArea\": \"总公司\",\n" +
            "            \"consigner\": \"刘诗诗\",\n" +
            "            \"consignerMobile\": \"13745456767\",\n" +
            "            \"consignerAddress\": \"北京朝阳区三环到四环之间分拣中心自动化运单下单地址\",\n" +
            "            \"receiverName\": \"赵丽颖\",\n" +
            "            \"receiverMobile\": \"13899131888\",\n" +
            "            \"receiverAddress\": \"北京五环外大兴亦庄荣京东街朝林广场\",\n" +
            "            \"goodNumber\": 5,\n" +
            "            \"volumeFormula\": \"7*8*9\",\n" +
            "            \"goodVolume\": 555.56,\n" +
            "            \"goodWeight\": 14.86,\n" +
            "            \"requireTime\": 1566973817000,\n" +
            "            \"importantHint\": \"三方订单号imp：ESL4398056124588\",\n" +
            "            \"freight\": \"9.99\",\n" +
            "            \"codMoney\": \"31.8\",\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"yn\": 1,\n" +
            "            \"waybillType\": 10000,\n" +
            "            \"sendPay\": \"10000000000000000000000000010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"payment\": 1,\n" +
            "            \"provinceId\": 1,\n" +
            "            \"cityId\": 72,\n" +
            "            \"countryId\": 2799,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"oldSiteId\": 39,\n" +
            "            \"busiId\": 31201,\n" +
            "            \"busiName\": \"次晨达\",\n" +
            "            \"sendCityId\": 72,\n" +
            "            \"againWeight\": 6.88,\n" +
            "            \"transferStationId\": 0,\n" +
            "            \"distributeStoreId\": 21637,\n" +
            "            \"distributeType\": 70,\n" +
            "            \"parentOrderId\": \"222230233656214884019886\",\n" +
            "            \"firstTime\": 1566888495000,\n" +
            "            \"busiOrderCode\": \"ESL222641235746\",\n" +
            "            \"sourceCode\": \"1006\",\n" +
            "            \"waybillSign\": \"00001000110009000000000000202100000020030002005000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"senderCompany\": \"北京京东北辰冬装有限公司\"\n" +

            "    },\n" +
            "    \"message\": \"接口调用成功\"\n" +
            "}";
    //纯配 运单 换单后得原单
    static String waybillCHUNPReturnJson  = "{\n" +
            "    \"resultCode\": 1,\n" +
            "    \"data\": {\n" +
            "            \"cid\": 1166240995931607040,\n" +
            "            \"eid\": 1166240995965161472,\n" +
            "            \"waybillCode\": \"JDVA00024194366\",\n" +
            "            \"vendorId\": \"36271732635\",\n" +
            "            \"arriveAreaId\": 6,\n" +
            "            \"arriveArea\": \"总公司\",\n" +
            "            \"consigner\": \"刘诗诗\",\n" +
            "            \"consignerMobile\": \"13745456767\",\n" +
            "            \"consignerAddress\": \"北京朝阳区三环到四环之间分拣中心自动化运单下单地址\",\n" +
            "            \"receiverName\": \"赵丽颖\",\n" +
            "            \"receiverMobile\": \"13899131888\",\n" +
            "            \"receiverAddress\": \"北京五环外大兴亦庄荣京东街朝林广场\",\n" +
            "            \"goodNumber\": 5,\n" +
            "            \"volumeFormula\": \"7*8*9\",\n" +
            "            \"goodVolume\": 555.56,\n" +
            "            \"goodWeight\": 14.86,\n" +
            "            \"requireTime\": 1566973817000,\n" +
            "            \"importantHint\": \"三方订单号imp：ESL4398056124588\",\n" +
            "            \"freight\": \"9.99\",\n" +
            "            \"codMoney\": \"31.8\",\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"yn\": 1,\n" +
            "            \"waybillType\": 10000,\n" +
            "            \"sendPay\": \"10000000000000000000000000010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"payment\": 1,\n" +
            "            \"provinceId\": 1,\n" +
            "            \"cityId\": 72,\n" +
            "            \"countryId\": 2799,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"oldSiteId\": 39,\n" +
            "            \"busiId\": 31201,\n" +
            "            \"busiName\": \"次晨达\",\n" +
            "            \"sendCityId\": 72,\n" +
            "            \"againWeight\": 6.88,\n" +
            "            \"transferStationId\": 0,\n" +
            "            \"distributeStoreId\": 21637,\n" +
            "            \"distributeType\": 70,\n" +
            "            \"parentOrderId\": \"222230233656214884019886\",\n" +
            "            \"firstTime\": 1566888495000,\n" +
            "            \"busiOrderCode\": \"222641235746\",\n" +
            "            \"sourceCode\": \"1006\",\n" +
            "            \"waybillSign\": \"20001000110009000000000000202100000020030002005000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"senderCompany\": \"北京京东北辰冬装有限公司\"\n" +

            "    },\n" +
            "    \"message\": \"接口调用成功\"\n" +
            "}";

    //C2C 运单 换单后得原单
    static String waybillC2CReturnJson  = "{\n" +
            "    \"resultCode\": 1,\n" +
            "    \"data\": {\n" +
            "            \"cid\": 1166240995931607040,\n" +
            "            \"eid\": 1166240995965161472,\n" +
            "            \"waybillCode\": \"JDVA00024194365\",\n" +
            "            \"vendorId\": \"36271732635\",\n" +
            "            \"arriveAreaId\": 6,\n" +
            "            \"arriveArea\": \"总公司\",\n" +
            "            \"consigner\": \"刘诗诗\",\n" +
            "            \"consignerMobile\": \"13745456767\",\n" +
            "            \"consignerAddress\": \"北京朝阳区三环到四环之间分拣中心自动化运单下单地址\",\n" +
            "            \"receiverName\": \"赵丽颖\",\n" +
            "            \"receiverMobile\": \"13899131888\",\n" +
            "            \"receiverAddress\": \"北京五环外大兴亦庄荣京东街朝林广场\",\n" +
            "            \"goodNumber\": 5,\n" +
            "            \"volumeFormula\": \"7*8*9\",\n" +
            "            \"goodVolume\": 555.56,\n" +
            "            \"goodWeight\": 14.86,\n" +
            "            \"requireTime\": 1566973817000,\n" +
            "            \"importantHint\": \"三方订单号imp：ESL4398056124588\",\n" +
            "            \"freight\": \"9.99\",\n" +
            "            \"codMoney\": \"31.8\",\n" +
            "            \"updateTime\": 1566888809000,\n" +
            "            \"yn\": 1,\n" +
            "            \"waybillType\": 10000,\n" +
            "            \"sendPay\": \"10000000000000000000000000010100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"payment\": 1,\n" +
            "            \"provinceId\": 1,\n" +
            "            \"cityId\": 72,\n" +
            "            \"countryId\": 2799,\n" +
            "            \"createTime\": 1564588800000,\n" +
            "            \"oldSiteId\": 39,\n" +
            "            \"busiId\": 31201,\n" +
            "            \"busiName\": \"次晨达\",\n" +
            "            \"sendCityId\": 72,\n" +
            "            \"againWeight\": 6.88,\n" +
            "            \"transferStationId\": 0,\n" +
            "            \"distributeStoreId\": 21637,\n" +
            "            \"distributeType\": 70,\n" +
            "            \"parentOrderId\": \"222230233656214884019886\",\n" +
            "            \"firstTime\": 1566888495000,\n" +
            "            \"busiOrderCode\": \"222641235746\",\n" +
            "            \"sourceCode\": \"1006\",\n" +
            "            \"waybillSign\": \"20001000110009000000000000208100000020030002005000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
            "            \"senderCompany\": \"北京京东北辰冬装有限公司\"\n" +

            "    },\n" +
            "    \"message\": \"接口调用成功\"\n" +
            "}";

    public static BaseEntity<BigWaybillDto> CANG_P_WAYBILL_RESULT = (BaseEntity<BigWaybillDto>)gson.fromJson(waybillCANGPJson, new TypeToken<BaseEntity<BigWaybillDto>>(){}.getType());

    public static BaseEntity<Waybill> CANG_P_RETURN_WAYBILL_RESULT = (BaseEntity<Waybill>)gson.fromJson(waybillCANGPReturnJson, new TypeToken<BaseEntity<Waybill>>(){}.getType());

    public static BaseEntity<BigWaybillDto> CHUN_P_WAYBILL_RESULT = (BaseEntity<BigWaybillDto>)gson.fromJson(waybillCHUNPJson, new TypeToken<BaseEntity<BigWaybillDto>>(){}.getType());

    public static BaseEntity<Waybill> CHUN_P_RETURN_WAYBILL_RESULT = (BaseEntity<Waybill>)gson.fromJson(waybillCHUNPReturnJson, new TypeToken<BaseEntity<Waybill>>(){}.getType());

    public static BaseEntity<BigWaybillDto> C2C_WAYBILL_RESULT = (BaseEntity<BigWaybillDto>)gson.fromJson(waybillC2CJson, new TypeToken<BaseEntity<BigWaybillDto>>(){}.getType());

    public static BaseEntity<Waybill> C2C_RETURN_WAYBILL_RESULT = (BaseEntity<Waybill>)gson.fromJson(waybillC2CReturnJson, new TypeToken<BaseEntity<Waybill>>(){}.getType());



}
