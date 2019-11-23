package cn.lj.pdl.repository;

import cn.lj.pdl.model.UserDO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author luojian
 * @date 2019/11/23
 */
public interface UserRepository extends JpaRepository<UserDO, Long> {
    /**
     * 略
     *
     * @param username 用户名
     * @return boolean
     */
    boolean existsByUsername(String username);

    /**
     * 略
     *
     * @param username 用户名
     * @return UserDO
     */
    UserDO findByUsername(String username);
}
