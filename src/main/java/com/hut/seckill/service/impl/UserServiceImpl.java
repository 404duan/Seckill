package com.hut.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hut.seckill.exception.GlobalException;
import com.hut.seckill.mapper.UserMapper;
import com.hut.seckill.pojo.User;
import com.hut.seckill.service.IUserService;
import com.hut.seckill.utils.CookieUtil;
import com.hut.seckill.utils.MD5Util;
import com.hut.seckill.utils.UUIDUtil;
import com.hut.seckill.vo.LoginVO;
import com.hut.seckill.vo.RespBean;
import com.hut.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     * @param loginVO
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        /*参数校验*/
        /*if (!StringUtils.hasLength(mobile) || !StringUtils.hasLength(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if (!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/
        /*根据手机号获取用户*/
        User user = userMapper.selectById(mobile);
        if (null == user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        /*判断密码是否正确*/
        if (!MD5Util.fromPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        /*生成cookie*/
        String ticket = UUIDUtil.uuid();
        /*将用户信息存入redis中*/
        redisTemplate.opsForValue().set("user:" + ticket, user);
//        request.getSession().setAttribute(ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (!StringUtils.hasLength(userTicket)){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        CookieUtil.setCookie(request, response, "userTicket", userTicket);
        return user;
    }

    /**
     * 更新密码
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if (1 == result){
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
