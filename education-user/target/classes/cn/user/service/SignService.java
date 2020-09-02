package cn.user.service;

import cn.beans.entity.User;

/**
 * 注册接口
 */
public interface SignService {
    /**
     * 邮箱注册
     *
     * @param user
     */
    public void educationCreateUser(User user) throws Exception;

    /**
     * 邮箱激活
     *
     * @param mail 收件人地址
     * @param code 激活码
     * @return
     */
    public boolean activate(String mail, String code) throws Exception;

    /**
     * 手机短信注册
     *
     * @param user
     * @throws Exception
     */
    public void educationCreateUserByPhone(User user) throws Exception;

    /**
     * 短信验证
     *
     * @param phoneNum 手机号
     * @param code     验证码
     * @return
     */
    public boolean validatePhone(String phoneNum, String code) throws Exception;

}
