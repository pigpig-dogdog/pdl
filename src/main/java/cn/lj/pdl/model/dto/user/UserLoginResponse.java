package cn.lj.pdl.model.dto.user;

import lombok.Data;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Data
public class UserLoginResponse {
    private Long id;
    private String username;
    private String token;
}
