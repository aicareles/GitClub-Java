//package com.jerry.geekdaily.repository;
//
//import com.jerry.geekdaily.domain.Goods;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface GoodsRepository extends JpaRepository<Goods, Long> {
//
//    List<Goods> findAllByGoodsIdBetween(Long goodsId1, Long goodsId2);
//
//    List<Goods> findAllByGoodsIdIn(List<Long> goodsIds);
//}
