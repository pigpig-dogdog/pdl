package cn.lj.pdl.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author luojian
 * @date 2019/11/23
 */
@ApiModel
@Data
public class UserLoginRequest {
    @ApiModelProperty(value = "用户名", required = true, example = "admin", position = 1)
    @NotBlank(message = "用户名不可为空")
    private String username;

    @ApiModelProperty(value = "密码", required = true, example = "\"123456\"", position = 2)
    @NotBlank(message = "密码不可为空")
    private String password;
}
