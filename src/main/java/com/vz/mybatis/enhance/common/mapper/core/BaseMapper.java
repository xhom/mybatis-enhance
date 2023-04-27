package com.vz.mybatis.enhance.common.mapper.core;

import com.vz.mybatis.enhance.common.mapper.qr.Querier;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

/**
 * @author visy.wang
 * @description: 基础通用Mapper
 * @date 2023/4/24 12:59
 * <T> 数据库表对应实体类型
 * <K> 主键类型
 */
public interface BaseMapper<T,K>{
    /**
     * 按主键查询记录
     * @param id 主键
     * @return 记录
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectById")
    T selectById(@Param("id") K id);

    /**
     * 按主键列表批量查询记录
     * @param idList 主键列表
     * @return 记录列表
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectByIds")
    List<T> selectByIds(@Param("idList") Collection<K> idList);

    /**
     * 按条件查询一条记录（多条记录时，自动取第一条）
     * @param querier 查询条件
     * @return 记录
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectOne")
    T selectOne(@Param("querier") Querier<T> querier);

    /**
     * 按条件查询记录列表
     * @param querier 查询条件
     * @return 记录列表
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectList")
    List<T> selectList(@Param("querier") Querier<T> querier);

    /**
     * 查询所有记录列表
     * @return 记录列表
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectAll")
    List<T> selectAll();

    /**
     * 按条件查询记录数
     * @param querier 查询条件
     * @return 记录数
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "count")
    long count(@Param("querier") Querier<T> querier);

    /**
     * 查询所有记录总数
     * @return 记录数
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "countAll")
    long countAll();

    /**
     * 按主键删除记录
     * @param id 主键
     * @return 删除成功数量
     */
    @DeleteProvider(type = BaseSqlProvider.class, method = "deleteById")
    int deleteById(@Param("id") K id);

    /**
     * 按主键列表批量删除记录
     * @param idList 主键列表
     * @return 删除成功数量
     */
    @DeleteProvider(type = BaseSqlProvider.class, method = "deleteByIds")
    int deleteByIds(@Param("idList") Collection<K> idList);

    /**
     * 按条件删除记录
     * @param querier 筛选条件
     * @return 删除成功数量
     */
    @DeleteProvider(type = BaseSqlProvider.class, method = "delete")
    int delete(@Param("querier") Querier<T> querier);

    /**
     * 新增一条记录（包含为null的字段）
     * @param record 记录信息
     * @return 新增成功数量
     */
    @InsertProvider(type = BaseSqlProvider.class, method = "insert")
    //@SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    //@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "record.id")
    int insert(@Param("record") T record);

    /**
     * 新增一条记录（不包含为null的字段）
     * @param record 记录信息
     * @return 新增成功数量
     */
    @InsertProvider(type = BaseSqlProvider.class, method = "insertSelective")
    //@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "record.id")
    int insertSelective(@Param("record") T record);

    /**
     * 按主键更新记录（包含为null的字段）
     * @param record 待更新记录信息（主键值不能为null）
     * @return 更新成功数量
     */
    @UpdateProvider(type = BaseSqlProvider.class, method = "updateById")
    int updateById(@Param("record") T record);

    /**
     * 按主键更新记录（不包含为null的字段）
     * @param record 待更新记录信息（主键值不能为null）
     * @return 更新成功数量
     */
    @UpdateProvider(type = BaseSqlProvider.class, method = "updateByIdSelective")
    int updateByIdSelective(@Param("record") T record);

    /**
     * 按条件更新记录（包含record中为null的字段）
     * @param record 待更新记录信息
     * @param querier 更新条件
     * @return 更新成功数量
     */
    @UpdateProvider(type = BaseSqlProvider.class, method = "update")
    int update(@Param("record") T record, @Param("querier") Querier<T> querier);

    /**
     * 按条件更新记录（不包含record中为null的字段）
     * @param record 待更新记录信息
     * @param querier 更新条件
     * @return 更新成功数量
     */
    @UpdateProvider(type = BaseSqlProvider.class, method = "updateSelective")
    int updateSelective(@Param("record") T record, @Param("querier") Querier<T> querier);
}
