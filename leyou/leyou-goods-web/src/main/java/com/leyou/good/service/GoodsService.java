package com.leyou.good.service;

import com.leyou.good.client.BrandClient;
import com.leyou.good.client.CategoryClient;
import com.leyou.good.client.GoodsClient;
import com.leyou.good.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private SpecificationClient specificationClient;

    public Map<String ,Object> loadData(Long spuId){

        HashMap<String, Object> model = new HashMap<String, Object>();
        //查询spu
        Spu spu = this.goodsClient.querySpuById(spuId);
        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);
        //查询分类
        List<Long> cids=Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNameByIds(cids);
        //初始化一个分类的map
        List<Map<String,Object>> categories=new ArrayList<>();

        for (int i = 0; i < cids.size(); i++) {
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("id",cids.get(i));
            map.put("name",names.get(i));
            categories.add(map);
        }
        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //skus
        List<Sku> skus = this.goodsClient.querSkuBySpuId(spuId);
        //查询规格参数组
        List<SpecGroup> groups = this.specificationClient.queryGroupsWithParam(spu.getCid3());
        //查询特殊的规格参数
        List<SpecParam> specParams = this.specificationClient.queryParams(null, spu.getCid3(), false, null);
       //初始化特殊规格的map
        Map<Long,String> paramMap=new HashMap<>();
        specParams.forEach(specParam -> {
            paramMap.put(specParam.getId(),specParam.getName());
        });
        model.put("spu",spu);
        model.put("spuDetail",spuDetail);
        model.put("categories",categories);
        model.put("brand",brand);
        model.put("skus",skus);
        model.put("groups",groups);
        model.put("paramMap",paramMap);

        return model;
    }
}
