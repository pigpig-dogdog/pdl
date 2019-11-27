package cn.lj.pdl.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @author luojian
 * @date 2019/11/23
 */
@ApiModel
@Data
public class UserRegisterRequest {
    @ApiModelProperty(value = "用户名", required = true, example = "admin", position = 1)
    @Size(min = 2, max = 32, message = "用户名有效长度范围: 2 ~ 32")
    private String username;

    @ApiModelProperty(value = "密码", required = true, example = "\"123456\"", position = 2)
    @Size(min = 3, max = 100, message = "用户名有效长度范围: 3 ~ 100")
    private String password;
}
