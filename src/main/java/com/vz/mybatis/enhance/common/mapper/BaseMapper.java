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
    @SelectProvider(type = BaseSqlProvider.class, method = "selectById")
    T selectById(@Param("id") K id);

    @SelectProvider(type = BaseSqlProvider.class, method = "select")
    List<T> select(@Param("querier") Querier<T> querier);

    @SelectProvider(type = BaseSqlProvider.class, method = "selectAll")
    List<T> selectAll();

    @SelectProvider(type = BaseSqlProvider.class, method = "count")
    long count(@Param("querier") Querier<T> querier);

    @DeleteProvider(type = BaseSqlProvider.class, method = "deleteById")
    int deleteById(@Param("id") K id);

    @DeleteProvider(type = BaseSqlProvider.class, method = "delete")
    int delete(@Param("querier") Querier<T> querier);

    @InsertProvider(type = BaseSqlProvider.class, method = "insert")
    int insert(@Param("record") T record);

    @InsertProvider(type = BaseSqlProvider.class, method = "insertSelective")
    int insertSelective(@Param("record") T record);

    @UpdateProvider(type = BaseSqlProvider.class, method = "updateById")
    int updateById(@Param("record") T record);

    /*










    int updateByExample(@Param("record") T record,
                        @Param("example") BaseExample example);

    int updateByExampleSelective(@Param("record") T record,
                                 @Param("example") BaseExample example);



    int updateByPrimaryKeySelective(T record);*/
}
