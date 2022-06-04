package com.hut.seckill.vo;

import com.hut.seckill.validator.Mobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 登录参数
 * @author DUANQI
 * DateTime: 2022-05-23 20:55
 */
@Data
public class LoginVO {
    @NotNull
    @Mobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
