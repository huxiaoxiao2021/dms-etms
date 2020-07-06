package com.jd.bluedragon.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BuilderMysqlCreateDbSql {
    private static Map<String,String> ipTODataBase = new HashMap<String,String>();
    static {
        ipTODataBase.put("qldms001.mysql.jddb.com","bd_dms_spl0,bd_dms_spl16 ,bd_dms_spl32 ,bd_dms_spl48 ,bd_dms_spl64 ,bd_dms_spl80 ,bd_dms_spl96 ,bd_dms_spl112 ,bd_dms_spl128 ,bd_dms_spl144 ,bd_dms_spl160 ,bd_dms_spl176 ,bd_dms_spl192 ,bd_dms_spl208 ,bd_dms_spl224 ,bd_dms_spl240 ,bd_dms_spl256 ,bd_dms_spl272 ,bd_dms_spl288 ,bd_dms_spl304 ,bd_dms_spl320 ,bd_dms_spl336 ,bd_dms_spl352 ,bd_dms_spl368 ,bd_dms_spl384 ,bd_dms_spl400 ,bd_dms_spl416 ,bd_dms_spl432 ,bd_dms_spl448 ,bd_dms_spl464 ,bd_dms_spl480 ,bd_dms_spl496");
        ipTODataBase.put("qldms002.mysql.jddb.com","bd_dms_spl1,bd_dms_spl17,bd_dms_spl33,bd_dms_spl49,bd_dms_spl65,bd_dms_spl81,bd_dms_spl97,bd_dms_spl113,bd_dms_spl129,bd_dms_spl145,bd_dms_spl161,bd_dms_spl177,bd_dms_spl193,bd_dms_spl209,bd_dms_spl225,bd_dms_spl241,bd_dms_spl257,bd_dms_spl273,bd_dms_spl289,bd_dms_spl305,bd_dms_spl321,bd_dms_spl337,bd_dms_spl353,bd_dms_spl369,bd_dms_spl385,bd_dms_spl401,bd_dms_spl417,bd_dms_spl433,bd_dms_spl449,bd_dms_spl465,bd_dms_spl481,bd_dms_spl497");
        ipTODataBase.put("qldms003.mysql.jddb.com","bd_dms_spl2,bd_dms_spl18,bd_dms_spl34,bd_dms_spl50,bd_dms_spl66,bd_dms_spl82,bd_dms_spl98,bd_dms_spl114,bd_dms_spl130,bd_dms_spl146,bd_dms_spl162,bd_dms_spl178,bd_dms_spl194,bd_dms_spl210,bd_dms_spl226,bd_dms_spl242,bd_dms_spl258,bd_dms_spl274,bd_dms_spl290,bd_dms_spl306,bd_dms_spl322,bd_dms_spl338,bd_dms_spl354,bd_dms_spl370,bd_dms_spl386,bd_dms_spl402,bd_dms_spl418,bd_dms_spl434,bd_dms_spl450,bd_dms_spl466,bd_dms_spl482,bd_dms_spl498");
        ipTODataBase.put("qldms004.mysql.jddb.com","bd_dms_spl3,bd_dms_spl19,bd_dms_spl35,bd_dms_spl51,bd_dms_spl67,bd_dms_spl83,bd_dms_spl99,bd_dms_spl115,bd_dms_spl131,bd_dms_spl147,bd_dms_spl163,bd_dms_spl179,bd_dms_spl195,bd_dms_spl211,bd_dms_spl227,bd_dms_spl243,bd_dms_spl259,bd_dms_spl275,bd_dms_spl291,bd_dms_spl307,bd_dms_spl323,bd_dms_spl339,bd_dms_spl355,bd_dms_spl371,bd_dms_spl387,bd_dms_spl403,bd_dms_spl419,bd_dms_spl435,bd_dms_spl451,bd_dms_spl467,bd_dms_spl483,bd_dms_spl499");
        ipTODataBase.put("qldms005.mysql.jddb.com","bd_dms_spl4,bd_dms_spl20,bd_dms_spl36,bd_dms_spl52,bd_dms_spl68,bd_dms_spl84,bd_dms_spl100,bd_dms_spl116,bd_dms_spl132,bd_dms_spl148,bd_dms_spl164,bd_dms_spl180,bd_dms_spl196,bd_dms_spl212,bd_dms_spl228,bd_dms_spl244,bd_dms_spl260,bd_dms_spl276,bd_dms_spl292,bd_dms_spl308,bd_dms_spl324,bd_dms_spl340,bd_dms_spl356,bd_dms_spl372,bd_dms_spl388,bd_dms_spl404,bd_dms_spl420,bd_dms_spl436,bd_dms_spl452,bd_dms_spl468,bd_dms_spl484,bd_dms_spl500");
        ipTODataBase.put("qldms006.mysql.jddb.com","bd_dms_spl5,bd_dms_spl21,bd_dms_spl37,bd_dms_spl53,bd_dms_spl69,bd_dms_spl85,bd_dms_spl101,bd_dms_spl117,bd_dms_spl133,bd_dms_spl149,bd_dms_spl165,bd_dms_spl181,bd_dms_spl197,bd_dms_spl213,bd_dms_spl229,bd_dms_spl245,bd_dms_spl261,bd_dms_spl277,bd_dms_spl293,bd_dms_spl309,bd_dms_spl325,bd_dms_spl341,bd_dms_spl357,bd_dms_spl373,bd_dms_spl389,bd_dms_spl405,bd_dms_spl421,bd_dms_spl437,bd_dms_spl453,bd_dms_spl469,bd_dms_spl485,bd_dms_spl501");
        ipTODataBase.put("qldms007.mysql.jddb.com","bd_dms_spl6,bd_dms_spl22,bd_dms_spl38,bd_dms_spl54,bd_dms_spl70,bd_dms_spl86,bd_dms_spl102,bd_dms_spl118,bd_dms_spl134,bd_dms_spl150,bd_dms_spl166,bd_dms_spl182,bd_dms_spl198,bd_dms_spl214,bd_dms_spl230,bd_dms_spl246,bd_dms_spl262,bd_dms_spl278,bd_dms_spl294,bd_dms_spl310,bd_dms_spl326,bd_dms_spl342,bd_dms_spl358,bd_dms_spl374,bd_dms_spl390,bd_dms_spl406,bd_dms_spl422,bd_dms_spl438,bd_dms_spl454,bd_dms_spl470,bd_dms_spl486,bd_dms_spl502");
        ipTODataBase.put("qldms008.mysql.jddb.com","bd_dms_spl7,bd_dms_spl23,bd_dms_spl39,bd_dms_spl55,bd_dms_spl71,bd_dms_spl87,bd_dms_spl103,bd_dms_spl119,bd_dms_spl135,bd_dms_spl151,bd_dms_spl167,bd_dms_spl183,bd_dms_spl199,bd_dms_spl215,bd_dms_spl231,bd_dms_spl247,bd_dms_spl263,bd_dms_spl279,bd_dms_spl295,bd_dms_spl311,bd_dms_spl327,bd_dms_spl343,bd_dms_spl359,bd_dms_spl375,bd_dms_spl391,bd_dms_spl407,bd_dms_spl423,bd_dms_spl439,bd_dms_spl455,bd_dms_spl471,bd_dms_spl487,bd_dms_spl503");
        ipTODataBase.put("my15005m.mysql.jddb.com","bd_dms_spl8,bd_dms_spl24,bd_dms_spl40,bd_dms_spl56,bd_dms_spl72,bd_dms_spl88,bd_dms_spl104,bd_dms_spl120,bd_dms_spl136,bd_dms_spl152,bd_dms_spl168,bd_dms_spl184,bd_dms_spl200,bd_dms_spl216,bd_dms_spl232,bd_dms_spl248,bd_dms_spl264,bd_dms_spl280,bd_dms_spl296,bd_dms_spl312,bd_dms_spl328,bd_dms_spl344,bd_dms_spl360,bd_dms_spl376,bd_dms_spl392,bd_dms_spl408,bd_dms_spl424,bd_dms_spl440,bd_dms_spl456,bd_dms_spl472,bd_dms_spl488,bd_dms_spl504");
        ipTODataBase.put("my15006m.mysql.jddb.com","bd_dms_spl9,bd_dms_spl25,bd_dms_spl41,bd_dms_spl57,bd_dms_spl73,bd_dms_spl89,bd_dms_spl105,bd_dms_spl121,bd_dms_spl137,bd_dms_spl153,bd_dms_spl169,bd_dms_spl185,bd_dms_spl201,bd_dms_spl217,bd_dms_spl233,bd_dms_spl249,bd_dms_spl265,bd_dms_spl281,bd_dms_spl297,bd_dms_spl313,bd_dms_spl329,bd_dms_spl345,bd_dms_spl361,bd_dms_spl377,bd_dms_spl393,bd_dms_spl409,bd_dms_spl425,bd_dms_spl441,bd_dms_spl457,bd_dms_spl473,bd_dms_spl489,bd_dms_spl505");
        ipTODataBase.put("my15007m.mysql.jddb.com","bd_dms_spl10,bd_dms_spl26,bd_dms_spl42,bd_dms_spl58,bd_dms_spl74,bd_dms_spl90,bd_dms_spl106,bd_dms_spl122,bd_dms_spl138,bd_dms_spl154,bd_dms_spl170,bd_dms_spl186,bd_dms_spl202,bd_dms_spl218,bd_dms_spl234,bd_dms_spl250,bd_dms_spl266,bd_dms_spl282,bd_dms_spl298,bd_dms_spl314,bd_dms_spl330,bd_dms_spl346,bd_dms_spl362,bd_dms_spl378,bd_dms_spl394,bd_dms_spl410,bd_dms_spl426,bd_dms_spl442,bd_dms_spl458,bd_dms_spl474,bd_dms_spl490,bd_dms_spl506");
        ipTODataBase.put("my15008m.mysql.jddb.com","bd_dms_spl11,bd_dms_spl27,bd_dms_spl43,bd_dms_spl59,bd_dms_spl75,bd_dms_spl91,bd_dms_spl107,bd_dms_spl123,bd_dms_spl139,bd_dms_spl155,bd_dms_spl171,bd_dms_spl187,bd_dms_spl203,bd_dms_spl219,bd_dms_spl235,bd_dms_spl251,bd_dms_spl267,bd_dms_spl283,bd_dms_spl299,bd_dms_spl315,bd_dms_spl331,bd_dms_spl347,bd_dms_spl363,bd_dms_spl379,bd_dms_spl395,bd_dms_spl411,bd_dms_spl427,bd_dms_spl443,bd_dms_spl459,bd_dms_spl475,bd_dms_spl491,bd_dms_spl507");
        ipTODataBase.put("my15009m.mysql.jddb.com","bd_dms_spl12,bd_dms_spl28,bd_dms_spl44,bd_dms_spl60,bd_dms_spl76,bd_dms_spl92,bd_dms_spl108,bd_dms_spl124,bd_dms_spl140,bd_dms_spl156,bd_dms_spl172,bd_dms_spl188,bd_dms_spl204,bd_dms_spl220,bd_dms_spl236,bd_dms_spl252,bd_dms_spl268,bd_dms_spl284,bd_dms_spl300,bd_dms_spl316,bd_dms_spl332,bd_dms_spl348,bd_dms_spl364,bd_dms_spl380,bd_dms_spl396,bd_dms_spl412,bd_dms_spl428,bd_dms_spl444,bd_dms_spl460,bd_dms_spl476,bd_dms_spl492,bd_dms_spl508");
        ipTODataBase.put("my15010m.mysql.jddb.com","bd_dms_spl13,bd_dms_spl29,bd_dms_spl45,bd_dms_spl61,bd_dms_spl77,bd_dms_spl93,bd_dms_spl109,bd_dms_spl125,bd_dms_spl141,bd_dms_spl157,bd_dms_spl173,bd_dms_spl189,bd_dms_spl205,bd_dms_spl221,bd_dms_spl237,bd_dms_spl253,bd_dms_spl269,bd_dms_spl285,bd_dms_spl301,bd_dms_spl317,bd_dms_spl333,bd_dms_spl349,bd_dms_spl365,bd_dms_spl381,bd_dms_spl397,bd_dms_spl413,bd_dms_spl429,bd_dms_spl445,bd_dms_spl461,bd_dms_spl477,bd_dms_spl493,bd_dms_spl509");
        ipTODataBase.put("my15011m.mysql.jddb.com","bd_dms_spl14,bd_dms_spl30,bd_dms_spl46,bd_dms_spl62,bd_dms_spl78,bd_dms_spl94,bd_dms_spl110,bd_dms_spl126,bd_dms_spl142,bd_dms_spl158,bd_dms_spl174,bd_dms_spl190,bd_dms_spl206,bd_dms_spl222,bd_dms_spl238,bd_dms_spl254,bd_dms_spl270,bd_dms_spl286,bd_dms_spl302,bd_dms_spl318,bd_dms_spl334,bd_dms_spl350,bd_dms_spl366,bd_dms_spl382,bd_dms_spl398,bd_dms_spl414,bd_dms_spl430,bd_dms_spl446,bd_dms_spl462,bd_dms_spl478,bd_dms_spl494,bd_dms_spl510");
        ipTODataBase.put("my15012m.mysql.jddb.com","bd_dms_spl15,bd_dms_spl31,bd_dms_spl47,bd_dms_spl63,bd_dms_spl79,bd_dms_spl95,bd_dms_spl111,bd_dms_spl127,bd_dms_spl143,bd_dms_spl159,bd_dms_spl175,bd_dms_spl191,bd_dms_spl207,bd_dms_spl223,bd_dms_spl239,bd_dms_spl255,bd_dms_spl271,bd_dms_spl287,bd_dms_spl303,bd_dms_spl319,bd_dms_spl335,bd_dms_spl351,bd_dms_spl367,bd_dms_spl383,bd_dms_spl399,bd_dms_spl415,bd_dms_spl431,bd_dms_spl447,bd_dms_spl463,bd_dms_spl479,bd_dms_spl495,bd_dms_spl511");

    }



    /**
     * 创建alter 表语句
     * @throws Exception
     */
    private static void builderMysqlAlterSqlToFile()throws Exception{
        String createfileNamePrefix = "D:\\工作\\需求\\分库分表添加字段\\11111111\\";
        String fileNameIn = "D:\\工作\\需求\\分库分表添加字段\\send_d-alter.txt";
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileNameIn),"utf-8"))){
            String str;
            StringBuffer sb = new StringBuffer();
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }
            br.close();
            int fileNum = 1;
            for(Map.Entry<String,String> entry:ipTODataBase.entrySet()){
                String[] dbNames = entry.getValue().split(",");
                StringBuffer ipRelativeSql = new StringBuffer();
                for(String dbName : dbNames){
                    ipRelativeSql.append(String.format(sb.toString(),dbName.trim()));
                }
                String name = createfileNamePrefix +fileNum +"-"+entry.getKey()+ ".txt";
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(name))){
                    bw.write(ipRelativeSql.toString());
                    bw.write("\n");
                    bw.close();
                }
                fileNum++;
            }
        }
    }


    public static void main(String[] args)throws Exception {
        builderMysqlAlterSqlToFile();
    }
}
