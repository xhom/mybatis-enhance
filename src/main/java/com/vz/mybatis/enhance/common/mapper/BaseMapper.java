package com.vz.mybatis.enhance.common.mapper;

import com.vz.mybatis.enhance.common.mapper.qr.Querier;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author visy.wang
 * @description: 基础通用Mapper
 * @date 2023/4/24 12:59
 */
public interface BaseMapper<T,K>{
    @SelectProvider(type = BaseSqlProvider.class, method = "selectByPrimaryKey")
    T selectByPrimaryKey(@Param("id") K id);

    @SelectProvider(type = BaseSqlProvider.class, method = "selectByExample")
    List<T> selectByExample(@Param("querier") Querier<T> querier);

    @SelectProvider(type = BaseSqlProvider.class, method = "countByExample")
    long countByExample(@Param("querier") Querier<T> querier);

    @DeleteProvider(type = BaseSqlProvider.class, method = "deleteByPrimaryKey")
    int deleteByPrimaryKey(@Param("id") K id);

    @DeleteProvider(type = BaseSqlProvider.class, method = "deleteByExample")
    int deleteByExample(@Param("querier") Querier<T> querier);

    @InsertProvider(type = BaseSqlProvider.class, method = "insert")
    int insert(T record);

    @InsertProvider(type = BaseSqlProvider.class, method = "insertSelective")
    int insertSelective(T record);

    @UpdateProvider(type = BaseSqlProvider.class, method = "updateByPrimaryKey")
    int updateByPrimaryKey(T record);

    /*










    int updateByExample(@Param("record") T record,
                        @Param("example") BaseExample example);

    int updateByExampleSelective(@Param("record") T record,
                                 @Param("example") BaseExample example);



    int updateByPrimaryKeySelective(T record);*/
}
