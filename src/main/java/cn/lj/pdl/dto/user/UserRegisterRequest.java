package cn.lj.pdl.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author luojian
 * @date 2019/11/23
 */
@ApiModel
@Data
public class UserRegisterRequest {
    @ApiModelProperty(required = true)
    @NotBlank(message = "用户名不可为空")
    @Size(min = 2, max = 32, message = "用户名长度范围:2~32")
    private String username;

    @ApiModelProperty(required = true)
    @NotBlank(message = "密码不可为空")
    @Size(min = 3, message = "密码长度小于3")
    private String password;
}
