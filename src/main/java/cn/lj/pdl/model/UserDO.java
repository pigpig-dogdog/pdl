package cn.lj.pdl.model;

import lombok.Data;

import java.util.Date;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Data
public class UserDO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
