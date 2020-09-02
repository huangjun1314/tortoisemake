package cn.user.service;


import cn.beans.entity.User;

public interface TokenService {

    /**
     * 生成Token
     *
     * @param userAgent
     * @param user
     * @return
     */
    public String generateToken(String userAgent, User user);

    /**
     * 保存Token到redis
     *
     * @param token
     * @param user
     */
    public void sava(String token, User user);

    /**
     * 验证Token是否有效
     *
     * @param userAgent
     * @param token
     * @return
     */
    public boolean validate(String userAgent, String token);

    /**
     * 删除token方法
     *
     * @param token
     */
    public void delete(String token);

    /**
     * @param userAgent
     * @param token
     * @return
     */
    public String reloadToken(String userAgent, String token) throws Exception;
}
