package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySpuBoByPage(String key,Boolean saleable,Integer page,Integer rows){
        Example example = new Example(Spu.class);
        Example.Criteria criteria=example.createCriteria();

        //搜索条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(saleable !=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //分页条件
        PageHelper.startPage(page,rows);
        //执行查询
        List<Spu> spus= this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo= new PageInfo<>(spus);

        ArrayList<SpuBo> spuBos = new ArrayList<SpuBo>();
        spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            //copy共同属性的对象的值到新的对象
            BeanUtils.copyProperties(spu,spuBo);
            //查询分类名称
           List<String> names= this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            spuBo.setCname(StringUtils.join(names,"/"));
            //查询品牌的名称
            spuBo.setBname(this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            spuBos.add(spuBo);
        });

        return new PageResult<>(pageInfo.getTotal(),spuBos);

    }

    public SpuDetail querySpuDetailBySpuId(Long SpuId){
        SpuDetail spuDetail = this.spuDetailMapper.selectByPrimaryKey(SpuId);
        return spuDetail;
    }


    public List<Sku> querySkuBySpuId(Long spuId){
         this.skuMapper.selectByPrimaryKey(spuId);
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spu_id",spuId);
        List<Sku> skus = this.skuMapper.selectByExample(example);
        return skus;
    }

    public Spu querySpuById(Long id){
        return this.spuMapper.selectByPrimaryKey(id);

    }

    @Transactional
    public void save(SpuBo spu){
        //保存spu
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());


    }
}
