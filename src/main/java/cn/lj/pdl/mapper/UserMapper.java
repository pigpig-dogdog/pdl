package cn.lj.pdl.mapper;

import cn.lj.pdl.model.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author luojian
 * @date 2019/11/26
 */
@Mapper
@Repository
public interface UserMapper {
    /**
     * 插入用户
     *
     * @param userDO userDO
     */
    void insert(UserDO userDO);

    /**
     * 删除用户
     *
     * @param id 用户主键
     */
    void delete(@Param("id") Long id);

    /**
     * 查找用户名是否存在
     *
     * @param username 用户名
     * @return boolean
     */
    Boolean existsByUsername(@Param("username") String username);

    /**
     * 根据用户名获取UserDO
     *
     * @param username 用户名
     * @return UserDO
     */
    UserDO findByUsername(@Param("username") String username);

}
