package com.jerry.geekdaily.util;

import org.apache.http.HttpException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileUtils {

    //字节大小，K,M,G
    public static final long KB = 1024;
    public static final long MB = KB * 1024;
    public static final long GB = MB * 1024;

    /**
     * 文件字节大小显示成M,G和K
     * @param size
     * @return
     */
    public static String displayFileSize(long size) {
        if (size >= GB) {
            return String.format("%.1f GB", (float) size / GB);
        } else if (size >= MB) {
            float value = (float) size / MB;
            return String.format(value > 100 ? "%.0f MB" : "%.1f MB", value);
        } else if (size >= KB) {
            float value = (float) size / KB;
            return String.format(value > 100 ? "%.0f KB" : "%.1f KB", value);
        } else {
            return String.format("%d B", size);
        }
    }

    public static File multi2File(MultipartFile file){
        File f = null;
        try {
            f=File.createTempFile("tmp", null);
            file.transferTo(f);
            f.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return 文件路径
     */
//    public String uploadImg(MultipartFile file) {
//        String dateName = null;
//        if (file == null || file.isEmpty() || file.getSize() == 0) return dateName;
//        //文章大图文件上传
//        try {
//            File path = null;
//            try {
//                path = new File(ResourceUtils.getURL("classpath:").getPath());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            if (!path.exists()) path = new File("");
//            System.out.println("path:" + path.getAbsolutePath());
//            //如果上传目录为/static/images/upload/，则可以如下获取：
//            File upload = new File(path.getAbsolutePath(), "static/images/upload/");
//            if (!upload.exists()) upload.mkdirs();
//            System.out.println("upload url:" + upload.getAbsolutePath());
//            //保存时的文件名(时间戳生成)
//            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//            Calendar calendar = Calendar.getInstance();
//            dateName = df.format(calendar.getTime()) + file.getOriginalFilename();
//            Path path1 = Paths.get(upload.getAbsolutePath(), dateName);
//            byte[] bytes = file.getBytes();
//            Files.write(path1, bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        }
//        return FILE_FOLDER+dateName;
//    }


}
