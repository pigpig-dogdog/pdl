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
    @ApiModelProperty(required = true)
    @Size(min = 2, message = "用户名长度不能小于2")
    @Size(max = 32, message = "用户名长度不能大于32")
    private String username;

    @ApiModelProperty(required = true)
    @Size(min = 3, message = "密码长度不能小于3")
    @Size(max = 100, message = "密码长度不能大于100")
    private String password;
}
