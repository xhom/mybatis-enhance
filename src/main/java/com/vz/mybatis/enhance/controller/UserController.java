package com.vz.mybatis.enhance.controller;

import com.vz.mybatis.enhance.common.mapper.qr.Querier;
import com.vz.mybatis.enhance.entity.TSupplierUser;
import com.vz.mybatis.enhance.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/24 18:09
 */
@RequestMapping("/user")
@RestController
public class UserController {
    @Resource
    private UserMapper userMapper;

    @RequestMapping("/get/{id}")
    public Map<String,Object> getById(@PathVariable("id") Long id){
        TSupplierUser user = userMapper.selectById(id);
        return resp(0, user);
    }

    @RequestMapping("/list")
    public Map<String,Object> list(@RequestParam Long entId,
                                   @RequestParam(required = false, defaultValue = "20") Integer limit){
        Querier<TSupplierUser> querier = Querier.<TSupplierUser>query()
                .gt(TSupplierUser::getEnterpriseId, entId)
                .limit(limit);

        long total = userMapper.count(querier);
        List<TSupplierUser> supplierUserList = userMapper.selectList(querier);
        return resp(total, supplierUserList);
    }

    @RequestMapping("/listAll")
    public Map<String,Object> listAll(){
        long all = userMapper.countAll();
        List<TSupplierUser> supplierUserList = userMapper.selectAll();
        return resp(all, supplierUserList);
    }

    @RequestMapping("/del/{id}")
    public Map<String,Object> deleteById(@PathVariable("id") Long id){
        int rows = userMapper.deleteById(id);
        return resp(rows, null);
    }

    @RequestMapping("/delete")
    public Map<String,Object> delete(@RequestParam Long entId){
        int rows = userMapper.delete(Querier.<TSupplierUser>query().eq(TSupplierUser::getEnterpriseId, entId));
        return resp(rows, null);
    }

    @RequestMapping("/add")
    public Map<String,Object> add(@RequestParam(required = false, defaultValue = "1") Integer type){
        Querier<TSupplierUser> querier = Querier.<TSupplierUser>query()
                .desc(TSupplierUser::getId)
                .limit(1);

        List<TSupplierUser> supplierUserList = userMapper.selectList(querier);
        Long newId = supplierUserList.get(0).getId()+1;

        TSupplierUser user = new TSupplierUser();
        user.setUserName("张三"+newId);
        user.setPhone("19301293031"+newId);
        user.setPassword(UUID.randomUUID().toString().replace("-", ""));
        user.setCreateDt(new Date());
        user.setEnterpriseId(newId);

        int rows = type==1 ? userMapper.insert(user) : userMapper.insertSelective(user);

        return resp(rows, user);
    }

    @RequestMapping("/update")
    public Map<String,Object> update(@RequestParam Long entId,
                                     @RequestParam(required = false, defaultValue = "1") Integer type){
        Querier<TSupplierUser> querier = Querier.<TSupplierUser>query().eq(TSupplierUser::getEnterpriseId, entId);

        TSupplierUser user = new TSupplierUser();
        user.setPassword(UUID.randomUUID().toString().replace("-", ""));
        user.setCreateDt(new Date());

        int rows = type==1 ? userMapper.update(user, querier) : userMapper.updateSelective(user, querier);

        return resp(rows, null);
    }

    @RequestMapping("/upd/{id}")
    public Map<String,Object> updateById(@PathVariable("id") Long id,
                                         @RequestParam(required = false, defaultValue = "1") Integer type){
        TSupplierUser user = new TSupplierUser();
        user.setId(id);
        user.setPassword(UUID.randomUUID().toString().replace("-", ""));
        user.setCreateDt(new Date());

        int rows = type==1 ? userMapper.updateById(user) : userMapper.updateByIdSelective(user);

        return resp(rows, null);
    }

    private Map<String,Object> resp(long total, Object data){
        Map<String,Object> mp = new HashMap<>();
        mp.put("rows", total);
        mp.put("data", data);
        return mp;
    }

}
