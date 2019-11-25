package cn.lj.pdl.dto.user;

import lombok.Data;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Data
public class UserLoginResponse {
    private String username;
    private String token;
}
