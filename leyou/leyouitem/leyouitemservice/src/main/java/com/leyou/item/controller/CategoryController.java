package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam("pid") Long pid){

        if(pid==null||pid.longValue()<0){
            //相应400,相当于ResponseEntity.status(HttpStatus.BAD_Request).build()
            return ResponseEntity.badRequest().build();
        }
       List<Category> categories= this.categoryService.queryCategoriesByPid(pid);
        if(CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categories);

    }
    //商品分类名称
    @GetMapping
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids")List<Long> ids){
        List<String> list = this.categoryService.queryNameByIds(ids);
        if(CollectionUtils.isEmpty(list)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(list);
    }
}
