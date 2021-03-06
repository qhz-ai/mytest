package com.leyou.serarch.pojo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.serarch.pojo.Client.BrandClient;
import com.leyou.serarch.pojo.Client.CategoryClient;
import com.leyou.serarch.pojo.Client.GoodsClient;
import com.leyou.serarch.pojo.Client.SpecificationClient;
import com.leyou.serarch.pojo.Goods;

import com.leyou.serarch.pojo.SearchRequest;
import com.leyou.serarch.pojo.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;



import java.io.IOException;
import java.util.*;

@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper Mapper=new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();
        //根据分类的id查询分类的名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //根据品牌Id来去查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //根据spuId查询所有的sku
        List<Sku> skus = this.goodsClient.querSkuBySpuId(spu.getId());
        //遍历所有的sku，获取所有的价格，然后放在一个新的数组
        List<Long> prices=new ArrayList<>();
        //搜集sku的必要字段信息
        List<Map<String,Object>> skuMapList=new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //获取sku中的图片，数据库中的图片可能是多张，多张是以逗号进行分割，所以也以，来进行切割，返回图片数组，获取第一张图片
            map.put("image", StringUtils.isEmpty(sku.getImages())?"": StringUtils.split(sku.getImages(),",")[0]);
            skuMapList.add(map);
        });
        //根据spu中的cid3查询出所有的搜索规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);
        //根据spuId查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值进行反序列化
        Map<String,Object> genericSpecMap = Mapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {});
       //把特殊的规格参数值进行反序列化
        Map<String,List<Object>> specialSpecMap = Mapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {});
        HashMap<String, Object> specs = new HashMap<>();
        params.forEach(param->{
            //判断规格参数的类型是否是通用规格参数
            if (param.getGeneric()){
                //如果是通用类型的参数，从genericSpecMap获取规格参数值
                String value = genericSpecMap.get(param.getId().toString()).toString();
                //判断是否是数值类型，如果是数值类型那么应该返回一个区间
                if(param.getNumeric()){
                    value = chooseSegment(value, param);
                }
                specs.put(param.getName(),value);
            }else{
                //如果是特殊的规格参数从specialSpecMap获取值
                List<Object> value = specialSpecMap.get(param.getId().toString());
                specs.put(param.getName(),value);
            }

        });
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要分类名称以及品牌名称
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names," ")+ " "+brand);
        //获取spu下的所有sku的价格
        goods.setPrice(prices);
        //获取spu下的所有sku,并转化成json字符串
        goods.setSkus(Mapper.writeValueAsString(skuMapList));
        //获取所有的查询的规格参数
        goods.setSpecs(specs);

        return goods;
    }
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


    public PageResult<Goods> search(SearchRequest request){

        //
        if(StringUtils.isBlank(request.getKey())){
            return null;
        }
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        //添加分页
        //添加分页，分页页码从0开始
        queryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //执行查询，获取结果集
        Page<Goods> goodsPage = this.goodsRepository.search(queryBuilder.build());

        //封装结果集并返回
        return new PageResult<>(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent());
    }
}
