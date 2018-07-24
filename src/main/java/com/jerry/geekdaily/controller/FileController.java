package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.domain.UploadFile;
import com.jerry.geekdaily.repository.UploadFileRepository;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Api(value = "FileController", description = "文件管理相关接口")
@RestController //这里必须是@Controller  如果是@RestController   则返回的html是个字符串
public class FileController {

    private final static Logger logger = LoggerFactory.getLogger(FileController.class);

//    private static String DOWNLOAD_FOLDER = "E://springboot_upload//";
    private static String DOWNLOAD_FOLDER = "//tmp//upload//";

    @Autowired
    private UploadFileRepository uploadFileRepository;

    @GetMapping(value = ("/download_list"))
    public ModelAndView download() {
        return new ModelAndView("download_list");
    }

    @GetMapping("/fileList")
    public ModelAndView handleFileUpload(HttpServletRequest request) {
        List<UploadFile> fileList = uploadFileRepository.findAll();
        ModelAndView model = new ModelAndView("download_list");
        model.addObject("fileList", fileList);
        return model;
    }

    @Autowired
    public UploadFileRepository repository;

    @PostMapping("download")
    public void downLoad(HttpServletResponse response, @RequestParam("fileName")String fileName) {
//        String fileName = "1.jpg";
        File file = new File(DOWNLOAD_FOLDER + "/" + fileName);
        if (file.exists()) { //判断文件父目录是否存在
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            ServletOutputStream os = null; //输出流
            try {
//                OutputStream out = response.getOutputStream();
//                FileInputStream in = new FileInputStream(file);
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i > 0) {
                    os.write(buffer);
                    i = bis.read(buffer);
                }
//                IOUtils.copy(fis, os);
                os.flush();
//                byte[] b = new byte[1024];
//                int n;
//                while((n=in.read(b))!=-1){
//                    out.write(b, 0, n);
//                }
//                in.close();
//                out.flush();
//                out.close();
                os.close();
                bis.close();
                fis.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download" + fileName);
        }
//        return "/download_list!";
//        return null;
    }


    //编辑
    @PostMapping("editFile")
    public ModelAndView editFile(@RequestParam("fileName")String fileName) {
        File f = new File(DOWNLOAD_FOLDER + "/" + fileName);
        ModelAndView model = new ModelAndView("edit");
        if (f.exists()) { //判断文件父目录是否存在
            UploadFile uploadFile = repository.findByFileName(fileName);
            logger.info("文件名："+uploadFile.getFileName());
            model.addObject("uploadFile", uploadFile);
        }
        return model;
    }

    //删除
    @PostMapping("delete")
    public String delFile(@RequestParam("fileName")String fileName){
        File f = new File(DOWNLOAD_FOLDER + "/" + fileName);
        if(f.exists()){
            repository.deleteByFileName(fileName);
        }
        return "redirect:/fileList";
    }



}
