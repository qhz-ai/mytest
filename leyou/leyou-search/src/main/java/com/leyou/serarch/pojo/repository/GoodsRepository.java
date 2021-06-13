package com.leyou.serarch.pojo.repository;

import com.leyou.serarch.pojo.Goods;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {


}
