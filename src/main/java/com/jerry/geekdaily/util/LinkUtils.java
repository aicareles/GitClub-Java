package com.jerry.geekdaily.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class LinkUtils {

    /**
     * 将长链接转换为短链接
     * @param url
     * @return
     */
    public static String gererateShortUrl(String url) {
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "502" ;
        // 要使用生成 URL 的字符
        String[] chars = new String[] { "a" , "b" , "c" , "d" , "e" , "f" , "g" , "h" ,
                "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" ,
                "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" ,
                "6" , "7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" ,
                "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" , "S" , "T" ,
                "U" , "V" , "W" , "X" , "Y" , "Z"

        };
        // 对传入网址进行 MD5 加密
        String sMD5EncryptResult = MD5Util.MD5(key+url);
        String hex = sMD5EncryptResult;

//        String[] resUrl = new String[4];
//        for ( int i = 0; i < 4; i++) {

        // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
        String sTempSubString = hex.substring(2 * 8, 2 * 8 + 8);    //固定取第三组

        // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
        long lHexLong = 0x3FFFFFFF & Long.parseLong (sTempSubString, 16);
        String outChars = "" ;
        for ( int j = 0; j < 6; j++) {
            // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
            long index = 0x0000003D & lHexLong;
            // 把取得的字符相加
            outChars += chars[( int ) index];
            // 每次循环按位右移 5 位
            lHexLong = lHexLong >> 5;
        }
        // 把字符串存入对应索引的输出数组
//            resUrl[i] = outChars;
//        }
        return outChars;
    }

    /**
     * 功能：检测当前URL是否可连接或是否有效,
     * 描述：利用正则表达式判断url是否有效
     * @param url 指定URL网络地址
     * @return URL
     */
    public static boolean verifyURL(String url) {
        Pattern pattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        return pattern.matcher(url).matches();
    }

}
