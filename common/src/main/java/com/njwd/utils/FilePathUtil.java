package com.njwd.utils;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/12/25 16:15
 */
public class FilePathUtil {
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static String getRealFilePath(String path) {
        return path.replace("/", FILE_SEPARATOR).replace("\\", FILE_SEPARATOR);
    }

    public static String getHttpURLPath(String path) {
        return path.replace("\\", "/");
    }

}
