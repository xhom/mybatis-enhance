package com.vz.mybatis.enhance.controller;

import com.vz.mybatis.enhance.common.mapper.qr.Querier;
import com.vz.mybatis.enhance.entity.TSupplierUser;
import com.vz.mybatis.enhance.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/24 18:09
 */
@RequestMapping("/test")
@RestController
public class TestController {
    @Resource
    private UserMapper userMapper;

    @GetMapping("/user/get/{id}")
    public TSupplierUser getUser(@PathVariable("id") Long id){
        return userMapper.selectById(id);
    }

    @GetMapping("/user/list")
    public Map<String,Object> userList(){
        Querier<TSupplierUser> querier = Querier.<TSupplierUser>query()
                .gt(TSupplierUser::getId, 4)
                .gt(TSupplierUser::getEnterpriseId, 3)
                .limit(3);

        Map<String,Object> map = new HashMap<>();
        map.put("total", userMapper.count(querier));
        map.put("list", userMapper.select(querier));
        return map;
    }

    @GetMapping("/user/listAll")
    public List<TSupplierUser> listAll(){
        return userMapper.select(Querier.query());
    }

    @GetMapping("/user/del/{id}")
    public Integer deleteUser(@PathVariable("id") Long id){
        int count = userMapper.deleteById(id);
        System.out.println("delete rows ="+count);
        return count;
    }

    @GetMapping("/user/add")
    public TSupplierUser addUser(){
        Querier<TSupplierUser> querier = Querier.<TSupplierUser>query()
                .orderByDesc(TSupplierUser::getId)
                .limit(1);
        List<TSupplierUser> supplierUserList = userMapper.select(querier);
        Long newId = supplierUserList.get(0).getId()+1;

        TSupplierUser user = new TSupplierUser();
        user.setUserName("张三"+newId);
        user.setPhone("19301293031"+newId);
        user.setPassword("aaaaaaa");
        user.setCreateDt(new Date());
        user.setEnterpriseId(newId);
        userMapper.insert(user);


        System.out.println("new User: "+user);
        return userMapper.selectById(newId);
    }

}
