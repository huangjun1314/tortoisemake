package cn.user.service;

import cn.beans.entity.User;
import cn.common.utils.MD5;
import com.alibaba.fastjson.JSON;
import nl.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 生成Token字符串
     *
     * @param userAgent
     * @param user
     * @return
     */
    public String generateToken(String userAgent, User user) {
        StringBuilder str = new StringBuilder();
        str.append("token:");
        UserAgent agent = UserAgent.parseUserAgentString(userAgent);
        //判断是否是移动端，是就存入移动端否则就存入PC端
        if (agent.getOperatingSystem().isMobileDevice()) {
            str.append("MOBILE-");
        } else {
            str.append("PC-");
        }
        str.append(MD5.getMd5(user.getMobileUser().toString(), 32) + "-");
        str.append(user.getId().toString() + "-");
        str.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-");
        str.append(MD5.getMd5(userAgent, 6));
        return str.toString();
    }

    /**
     * Token信息保存到redis
     *
     * @param token
     * @param user
     */
    public void sava(String token, User user) {

        //判断是PC端还是移动端
        if (token.startsWith("token:PC-")) {
            stringRedisTemplate.opsForValue().set(token,JSON.toJSONString(user),2 * 60 * 60, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(token, JSON.toJSONString(user));
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param userAgent
     * @param token
     * @return
     */
    @Override
    public boolean validate(String userAgent, String token) {
        System.out.println("token:----------------------:" + token);
        //检查token是否存在返回boolean类型
        if (!stringRedisTemplate.hasKey(token)) {    //判断token是否存在redis中
            return false;
        }
        String agentMD5 = token.split("-")[4];
        if (!MD5.getMd5(userAgent, 6).equals(agentMD5)) {
            return false;
        }
        return true;
    }

    /**
     * 删除token
     *
     * @param token
     */
    @Override
    public void delete(String token) {
        stringRedisTemplate.delete(token);
    }

    private long protrctedTime = 30 * 60;
    private long delay = 2 * 60;

    /**
     * 置换token
     *
     * @param userAgent
     * @param token
     * @return
     * @throws Exception
     */
    @Override
    public String reloadToken(String userAgent, String token) throws Exception {
        //1.验证token是否有效
        if (!stringRedisTemplate.hasKey(token)) {
            throw new Exception("token无效");
        }
        //2.token能不能置换
        Date genTime = new SimpleDateFormat("yyyyMMddHHmmsss").parse(token.split("-")[3]);
        long passed = Calendar.getInstance().getTimeInMillis() - genTime.getTime();
        if (passed < protrctedTime * 1000) {
            throw new Exception("token置换保护期内，不能置换，剩余" + (protrctedTime * 1000 - passed) / 1000);
        }
        //3.进行置换
        String user = stringRedisTemplate.opsForValue().get(token);
        User itripUser = JSON.parseObject(user, User.class);
        String newToken = this.generateToken(userAgent, itripUser);
        //4.老的token延时过期
        stringRedisTemplate.opsForValue().set(token,JSON.toJSONString(user),2 * 60 * 60, TimeUnit.SECONDS);
        //5.新的token保存至redis
        this.sava(newToken, itripUser);
        return newToken;
    }


}
