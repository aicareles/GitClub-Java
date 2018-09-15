package com.jerry.geekdaily.controller;

import com.jerry.geekdaily.domain.Game;
import com.jerry.geekdaily.repository.GameRepository;
import com.jerry.geekdaily.util.DateUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Api(value = "GameController", description = "世界杯竞猜相关接口")
@RestController //这里必须是@Controller  如果是@RestController   则返回的html是个字符串
public class GameController {

    private final static Logger logger = LoggerFactory.getLogger(GameController.class);


    @Autowired
    private GameRepository gameRepository;

    @GetMapping(value = ("upload_info"))
    public ModelAndView upload_info() {
        return new ModelAndView("upload_info");
    }

    @PostMapping("/uploadGame")
    public void handleGameUpload(HttpServletResponse response, @RequestParam(value = "user_info") String user_info,@RequestParam(value = "game_info") String game_info,@RequestParam(value = "game_score") String game_score) {
//        List<UploadFile> fileList = uploadFileRepository.findAll();
//        dto.addAttribute("fileList", fileList);
        if(response.getCharacterEncoding()!=null){
            response.setCharacterEncoding("GBK");
        }
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Game> gameList = gameRepository.findAll();
        for (int i=0; i<gameList.size();i++){
            Game game = gameList.get(i);
            if(user_info.equals(game.getNick_name()) && DateUtils.getDay(System.currentTimeMillis()).equals(game.getDay())){
                out.print("<script>alert('该用户当天已经提交，请勿重复提交!');</script>");
                return;
            }
        }
        Game game = new Game();
        game.setNick_name(user_info);
        game.setGame_info(game_info);
        game.setGame_score(game_score);
        game.setDate(System.currentTimeMillis());
        game.setDay(DateUtils.getDay(System.currentTimeMillis()));
        gameRepository.save(game);
        logger.info("预测信息提交成功!");
        out.print("<script>alert('保存表单文件成功!');</script>");
//        out.flush();//有了这个，下面的return就不会执行了
//        return "redirect:upload_info";
    }



    @GetMapping("/gameAdminList")
    public ModelAndView getGameList(HttpServletResponse response) {
        List<Game> gameList = gameRepository.findAll();
        ModelAndView model = new ModelAndView("game_list");
        model.addObject("gameList", gameList);
        return model;
    }

}
