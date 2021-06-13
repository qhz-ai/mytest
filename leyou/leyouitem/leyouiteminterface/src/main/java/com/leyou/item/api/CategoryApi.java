package com.leyou.item.api;

import com.leyou.item.pojo.Category;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RequestMapping("category")
public interface CategoryApi {

    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam("pid") Long pid);
    //商品分类名称
    @GetMapping
    public List<String> queryNameByIds(@RequestParam("ids")List<Long> ids);
}
