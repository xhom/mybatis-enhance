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

    @SelectProvider(type = BaseSqlProvider.class, method = "selectList")
    List<T> selectList(@Param("querier") Querier<T> querier);

    @SelectProvider(type = BaseSqlProvider.class, method = "selectAll")
    List<T> selectAll();

    @SelectProvider(type = BaseSqlProvider.class, method = "count")
    long count(@Param("querier") Querier<T> querier);

    @SelectProvider(type = BaseSqlProvider.class, method = "countAll")
    long countAll();

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

    @UpdateProvider(type = BaseSqlProvider.class, method = "updateByIdSelective")
    int updateByIdSelective(@Param("record") T record);

    @UpdateProvider(type = BaseSqlProvider.class, method = "update")
    int update(@Param("record") T record, @Param("querier") Querier<T> querier);

    @UpdateProvider(type = BaseSqlProvider.class, method = "updateSelective")
    int updateSelective(@Param("record") T record, @Param("querier") Querier<T> querier);
}
