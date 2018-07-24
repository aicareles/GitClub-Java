package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.domain.UploadFile;
import com.jerry.geekdaily.repository.UploadFileRepository;
import com.jerry.geekdaily.util.FileUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

//@RestController
@Api(value = "UploadController", description = "文件上传相关接口")
@RestController //这里必须是@Controller  如果是@RestController   则返回的html是个字符串
public class UploadController {

    private final static Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private UploadFileRepository uploadFileRepository;

    //Save the uploaded file to this folder
//    private static String UPLOADED_FOLDER = "E://springboot_upload//";
    private static String UPLOADED_FOLDER = "//tmp//upload//";

    @GetMapping(value = ("upload"))
    public ModelAndView upload(){
        return new ModelAndView("upload");
    }

    @GetMapping(value = ("multifile"))
    public ModelAndView multifile(){
        return new ModelAndView("multifile");
    }

    @PostMapping("/uploadEditFile")
    public String handleEditFile(@RequestParam(value = "fileName") String fileName,@RequestParam(value = "newFileName") String newFileName,
                                 @RequestParam(value = "des") String des, HttpServletResponse response, Model model){
        if(response.getCharacterEncoding()!=null){
            response.setCharacterEncoding("GBK");
        }
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(StringUtils.isEmpty(fileName) || StringUtils.isEmpty(des)){
            out.print("<script>alert('文件名或描述不可为空');</script>");
            out.flush();//有了这个，下面的return就不会执行了
            return "";
        }else {
            UploadFile uploadFile = uploadFileRepository.findByFileName(fileName);
            uploadFile.setFileName(newFileName);
            uploadFile.setDes(des);
            logger.info("fileName:"+fileName);
            logger.info("des:"+des);
            uploadFileRepository.saveAndFlush(uploadFile);
            //修改本地文件名或者服务器文件名
            File file = new File(UPLOADED_FOLDER,fileName);
            if(file.exists()){
                file.renameTo(new File(UPLOADED_FOLDER, newFileName));
            }else {
                logger.info("要编辑的文件不存在");
            }
        }
        model.addAttribute("editSuccess","文件编辑成功!");
        return "redirect:fileList";
    }

    /**
     * 单个文件上传具体实现方法;
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file,@RequestParam(value = "des") String des,  HttpServletRequest request) {
        ModelAndView model = new ModelAndView("uploadStatus");
        if (!file.isEmpty()) {
            try {
//                byte[] bytes = file.getBytes();
//                Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
//                Files.write(path, bytes);

                File path = null;
                try {
                    path = new File(ResourceUtils.getURL("classpath:").getPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if(!path.exists()) path = new File("");
                System.out.println("path:"+path.getAbsolutePath());

                //如果上传目录为/static/images/upload/，则可以如下获取：
                File upload = new File(path.getAbsolutePath(),"static/images/upload/");
                if(!upload.exists()) upload.mkdirs();
                System.out.println("upload url:"+upload.getAbsolutePath());

                byte[] bytes = file.getBytes();
                Path path1 = Paths.get(upload.getAbsolutePath(), file.getOriginalFilename());
                Files.write(path1, bytes);

//                File newFile = new File(UPLOADED_FOLDER, file.getOriginalFilename());
//                if(!newFile.exists()){
//                    newFile.createNewFile();
//                }
//                BufferedOutputStream out = new BufferedOutputStream(
//                        new FileOutputStream(newFile));
//                System.out.println(file.getName());
//                out.write(file.getBytes());
//                out.flush();
//                out.close();

                //保存上传的文件对象到数据库
                UploadFile uploadFile = new UploadFile();
                uploadFile.setDate(System.currentTimeMillis());
                uploadFile.setFileName(file.getOriginalFilename());
                uploadFile.setSize(FileUtils.displayFileSize(file.getSize()));
                uploadFile.setDes(des);
                uploadFileRepository.save(uploadFile);
                logger.info("保存文件成功");
                model.addObject("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");
            } catch (IOException e) {
                e.printStackTrace();
                model.addObject("message",  "上传失败:"+e.getMessage());
            }
        } else {
            model.addObject("message",  "Please select a file to upload");
        }
        return model;
    }


//    /**
//     * 文章大图上传;
//     *
//     * @param file
//     * @return
//     */
//    @PostMapping("/uploadArticleImg")
//    public String uploadArticleImg(@RequestParam("file") MultipartFile file,  HttpServletRequest request,ModelMap model) {
//        if (!file.isEmpty()) {
//            //保存时的文件名
//            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//            Calendar calendar = Calendar.getInstance();
//            String dateName = df.format(calendar.getTime())+file.getOriginalFilename();
//
//            System.out.println(dateName);
//            //保存文件的绝对路径
//            WebApplicationContext webApplicationContext = (WebApplicationContext)SpringContextUtils.applicationContext;
//            ServletContext servletContext = webApplicationContext.getServletContext();
//            String realPath = servletContext.getRealPath("/");
//            String filePath = realPath + "WEB-INF"+File.separator + "classes" + File.separator +"static" + File.separator + "resource" + File.separator+dateName;
//            System.out.println("绝对路径:"+filePath);
//
//            File newFile = new File(filePath);
//
//            //MultipartFile的方法直接写文件
//            try {
//
//                //上传文件
//                file.transferTo(newFile);
//
//                //数据库存储的相对路径
//                String projectPath = servletContext.getContextPath();
//                HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
//                String contextpath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+projectPath;
//                String url = contextpath + "/resource/"+dateName;
//                System.out.println("相对路径:"+url);
//                //文件名与文件URL存入数据库表
//
//
//
//            } catch (IllegalStateException | IOException e) {
//                e.printStackTrace();
//            }
//        }else {
//            return "文件不能为空";
//        }
//    }



    /**
     * 单个文件上传具体实现方法;
     *
     * @param file
     * @return
     */
//    @PostMapping("/uploadFile")
//    public String handleFileUpload(@RequestParam("file") MultipartFile file,@RequestParam(value = "des") String des,  HttpServletRequest request,ModelMap model) {
//        if (!file.isEmpty()) {
//            try {
//                /*
//                 * 这段代码执行完毕之后，图片上传到了工程的跟路径； 大家自己扩散下思维，如果我们想把图片上传到
//                 * d:/files大家是否能实现呢？ 等等;
//                 * 这里只是简单一个例子,请自行参考，融入到实际中可能需要大家自己做一些思考，比如： 1、文件路径； 2、文件名；
//                 * 3、文件格式; 4、文件大小的限制;
//                 */
////                BufferedOutputStream out = new BufferedOutputStream(
////                        new FileOutputStream(new File(
////                                file.getOriginalFilename())));
////                System.out.println(file.getName());
////                out.write(file.getBytes());
////                out.flush();
////                out.close();
//
////                int count;
////                int length = (int) file.getSize();
////                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(new File(file.getOriginalFilename())));
////                InputStream input = new BufferedInputStream(file.getInputStream());
////                byte[] buffer = new byte[256 * 1024];
////                long total = 0;
////                while ((count = input.read(buffer)) != -1) {
////                    total += count;
////                    int progress = (int)((total*100)/length);
////                    redirectAttributes.addFlashAttribute("progress",progress+"%");
////                    output.write(buffer, 0, count);
////                }
//
//                if (request.getCharacterEncoding() == null) {
//                    request.setCharacterEncoding("UTF-8");
//                }
////                //获取存储路径
////                String path=request.getSession().getServletContext().getRealPath("/");
////                logger.info("获取存储路径："+path);
////                //上传文件名称
////                String fileName=file.getOriginalFilename();
////                logger.info("上传文件名称："+fileName);
////                //创建存储目录
////                File targetFile=new File(path,fileName);
////
////                if(!targetFile.exists()){
////                    targetFile.createNewFile();
////                }
////                file.transferTo(targetFile);
////                //将文件路径转发到页面
////                model.addAttribute("message", request.getContextPath()+"/upload/"+fileName);
////                logger.info("文件路径："+request.getContextPath());
//
//                byte[] bytes = file.getBytes();
//                Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
//                Files.write(path, bytes);
//
////                //保存文件的绝对路径
////                        WebApplicationContext webApplicationContext = (WebApplicationContext)SpringContextUtils.applicationContext;
////                ServletContext servletContext = webApplicationContext.getServletContext();
////                String realPath = servletContext.getRealPath("/");
////                String filePath = realPath + "WEB-INF"+File.separator + "classes" + File.separator +"static" + File.separator + "resource" + File.separator+file.getOriginalFilename();
////                System.out.println("绝对路径:"+filePath);
////                File newFile = new File(filePath);
////                //上传文件
////                file.transferTo(newFile);
////                //数据库存储的相对路径
////                String projectPath = servletContext.getContextPath();
////                HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
////                String contextpath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+projectPath;
////                String url = contextpath + "/resource/"+file.getOriginalFilename();
////                System.out.println("相对路径:"+url);
//                //保存上传的文件对象到数据库
//                UploadFile uploadFile = new UploadFile();
//                uploadFile.setDate(System.currentTimeMillis());
//                uploadFile.setFileName(file.getOriginalFilename());
//                uploadFile.setSize(FileUtils.displayFileSize(file.getSize()));
//                uploadFile.setDes(des);
//                uploadFileRepository.save(uploadFile);
//                logger.info("保存文件成功");
////                redirectAttributes.addFlashAttribute("message",
////                        "You successfully uploaded '" + file.getOriginalFilename() + "'");
//                model.addAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");
//            } catch (IOException e) {
//                e.printStackTrace();
////                redirectAttributes.addFlashAttribute("message", "上传失败:"+e.getMessage());
//                model.addAttribute("message",  "上传失败:"+e.getMessage());
//                logger.info("redirect:uploadStatus");
////                return "redirect:uploadStatus";
//                return "/uploadStatus";
//            }
//            logger.info("redirect:/uploadStatus");
////            return "redirect:uploadStatus";
//            return "/uploadStatus";
//        } else {
////            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
//            model.addAttribute("message",  "Please select a file to upload");
////            return "redirect:uploadStatus";
//            return "/uploadStatus";
//        }
//    }


    /**
     * 实现多文件上传
     * */
    @PostMapping("/multifileUpload")
    public ModelAndView multifileUpload(HttpServletRequest request, RedirectAttributes redirectAttributes){
//    public @ResponseBody String multifileUpload(@RequestParam("fileName")List<MultipartFile> files){
        ModelAndView model = new ModelAndView("uploadStatus");
        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("fileName");

        if(files.isEmpty()){
//            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            model.addObject("message", "Please select a file to upload");
            return model;
        }

//        String path = "F:/test" ;

        for(MultipartFile file:files){
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);

            if(file.isEmpty()){
                model.addObject("message", "文件不能为空!");
                return model;
            }else{
                File dest = new File(UPLOADED_FOLDER + "/" + fileName);
                if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                try {
                    file.transferTo(dest);
                }catch (Exception e) {
                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    redirectAttributes.addFlashAttribute("message", "上传失败:"+e.getMessage());
                    model.addObject("message", "上传失败:"+e.getMessage());
                    return model;
                }
            }
        }
//        redirectAttributes.addFlashAttribute("message", "You successfully uploaded ");
        model.addObject("message", "You successfully uploaded ");
        return model;
    }

    @GetMapping("/uploadStatus")
    public ModelAndView uploadStatus() {
        return new ModelAndView("uploadStatus");
    }
}
