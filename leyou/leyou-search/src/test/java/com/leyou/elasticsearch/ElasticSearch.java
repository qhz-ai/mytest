package com.leyou.elasticsearch;




import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.serarch.pojo.Client.GoodsClient;
import com.leyou.serarch.pojo.Goods;
import com.leyou.serarch.pojo.repository.GoodsRepository;
import com.leyou.serarch.pojo.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticSearch {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;
    @Test
    public void test(){

        Integer rows=100;
        Integer page=1;
        do {
            //分页查询spu,获取分页结果集
            PageResult<SpuBo> pageResult = this.goodsClient.querySpuBoByPage(null, null, page, rows);
            List<SpuBo> items = pageResult.getItems();

            List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();

                }
                return null;
            }).collect(Collectors.toList());

            //执行新增数据的方法
            this.goodsRepository.saveAll(goodsList);
            rows=items.size();
            page++;

        }while (rows==100);

    }
    @Test
    public  void test2(){
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);
    }


}
