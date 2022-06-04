package com.hut.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hut.seckill.pojo.User;
import com.hut.seckill.vo.LoginVO;
import com.hut.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-23
 */
public interface IUserService extends IService<User> {

    /**
     * 登录
     * @param loginVO
     * @param request
     * @param response
     * @return
     */
    RespBean doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新密码
     * @param userTicket
     * @param password
     * @return
     */
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
