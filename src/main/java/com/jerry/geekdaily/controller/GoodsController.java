//package com.jerry.geekdaily.controller;
//
//import com.dangdang.ddframe.rdb.sharding.keygen.KeyGenerator;
//import com.jerry.geekdaily.domain.Goods;
//import com.jerry.geekdaily.repository.GoodsRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//@RestController
//public class GoodsController {
//
//    private String randomDate(){
//        int year=2019;
//        Random rndMonth=new Random();
//        int month=rndMonth.nextInt(12)+1;
//        Random rndDay=new Random();
//        int Day=rndDay.nextInt(30)+1;
//        Random rndHour=new Random();
//        int hour=rndHour.nextInt(23);
//        Random rndMinute=new Random();
//        int minute=rndMinute.nextInt(60);
//        Random rndSecond=new Random();
//        int second=rndSecond.nextInt(60);
//        System.out.println("date:"+year+"-"+cp(month)+"-"+cp(Day)+"  "+cp(hour)+":"+cp(minute)+":"+cp(second));
//        return year+"-"+cp(month)+"-"+cp(Day)+"  "+cp(hour)+":"+cp(minute)+":"+cp(second);
//    }
//    private String cp(int num){
//        String Num=num+"";
//        if (Num.length()==1){
//            return "0"+Num;
//        }else {
//            return Num;
//        }
//    }
//
//    @Autowired
//    private KeyGenerator keyGenerator;
//
//    @Autowired
//    private GoodsRepository goodsRepository;
//
//    @GetMapping("save")
//    public String save(){
//        ArrayList<Goods> lists = new ArrayList<>();
//        for(int i= 1 ; i <= 40 ; i ++){
//            Goods goods = new Goods();
//            goods.setGoodsId((long) i);
//            goods.setGoodsName( "shangpin" + i);
//            goods.setGoodsType((long) (i+1));
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            try {
//                goods.setDate(sdf.parse(randomDate()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            lists.add(goods);
////            goodsRepository.save(goods);
//        }
//        goodsRepository.saveAll(lists);
//        return "success";
//    }
//
//    @GetMapping("select")
//    public String select(){
//        return goodsRepository.findAll().toString();
//    }
//
//    @GetMapping("delete")
//    public void delete(){
//        goodsRepository.deleteAll();
//    }
//
//    @GetMapping("query1")
//    public Object query1(){
//        return goodsRepository.findAllByGoodsIdBetween(10L, 30L);
//    }
//
//    @GetMapping("query2")
//    public Object query2(){
//        List<Long> goodsIds = new ArrayList<>();
//        goodsIds.add(10L);
//        goodsIds.add(15L);
//        goodsIds.add(20L);
//        goodsIds.add(25L);
//        return goodsRepository.findAllByGoodsIdIn(goodsIds);
//    }
//}
