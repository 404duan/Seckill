package com.hut.seckill.vo;

import com.hut.seckill.utils.ValidatorUtil;
import com.hut.seckill.validator.Mobile;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author DUANQI
 * DateTime: 2022-05-24 13:43
 */
public class IsMobileValidator implements ConstraintValidator<Mobile, String> {
    private boolean required = false;

    @Override
    public void initialize(Mobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            return ValidatorUtil.isMobile(s);
        }else {
            return !StringUtils.hasLength(s) || ValidatorUtil.isMobile(s);
        }
    }
}
