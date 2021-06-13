package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows
    ){

       PageResult<SpuBo> pageResult= this.goodsService.querySpuBoByPage(key,saleable,page,rows);
        if(CollectionUtils.isEmpty(pageResult.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/spu/detail/{spuId}")
    public  ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId){

                SpuDetail spuDetail=this.goodsService.querySpuDetailBySpuId(spuId);
                if (spuDetail==null){
                    return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(spuDetail);
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querSkuBySpuId(@RequestParam("id")Long id){
        List<Sku> skus=this.goodsService.querySkuBySpuId(id);
        if(CollectionUtils.isEmpty(skus)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    @GetMapping("{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id")Long id){
        Spu sPu= this.goodsService.querySpuById(id);
        if (sPu==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sPu);
    }

}
