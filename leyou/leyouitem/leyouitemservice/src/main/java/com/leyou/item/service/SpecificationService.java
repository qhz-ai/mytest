package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper groupMapper;
    @Autowired
    private SpecParamMapper paramMapper;
    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid){
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.groupMapper.select(specGroup);
    }

    public List<SpecGroup> queryGroupsByParam(Long cid){

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> groupList = this.groupMapper.select(specGroup);
        groupList.forEach(group->{
            List<SpecParam> params = this.queryParams(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groupList;
    }
    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    public List<SpecParam> queryParams(Long gid,  Long cid,  Boolean generic,  Boolean searching){
        SpecParam param = new SpecParam();
        if (gid!=null) {
            param.setGroupId(gid);
        }
        if(cid!=null){
            param.setCid(cid);
        }
        if (generic!=null){
            param.setGeneric(generic);
        }
        if (searching!=null){
            param.setSearching(searching);
        }
        return this.paramMapper.select(param);
    }
}
