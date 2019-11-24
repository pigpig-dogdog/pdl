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
    @ApiModelProperty(required = true)
    @NotBlank(message = "用户名不可为空")
    private String username;

    @ApiModelProperty(required = true)
    @NotBlank(message = "密码不可为空")
    private String password;
}
