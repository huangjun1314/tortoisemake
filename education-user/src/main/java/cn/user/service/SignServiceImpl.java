package cn.user.service;

import cn.beans.entity.User;
import cn.common.utils.MD5;
import cn.dao.userservice.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SignServiceImpl implements SignService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;  //redis操作

    @Autowired
    SmsService smsService;              //短信service

    @Autowired
    UserService userService;        //用户
    @Autowired
    MailService mailService;        //邮箱注册


    /**
     * 邮箱注册
     *
     * @param user
     */
    @Override
    public void educationCreateUser(User user) throws Exception {
        //1.创建用户
        userService.insert(user);
        //2.生成验证码
        String code = MD5.getMd5(new Date().toLocaleString(),32);   //产生随机数
        System.out.println("生成随机数："+code);
        //3.发送验证码
        mailService.sendSimpleMail("1484516059@qq.com",user.getMobileUser(),
                "1484516059@qq.com","邮箱注册验证码：",code);
        //4.缓存短信验证码到redis
        stringRedisTemplate.opsForValue().set("activation:" + user.getMobileUser(),code,2*60, TimeUnit.SECONDS);
        System.out.println("huoqu:"+stringRedisTemplate.opsForValue().get("activation:" + user.getMobileUser()));
    }
    /**
     * 邮箱激活
     *
     * @param mail 收件人地址
     * @param code 激活码
     * @return
     */
    @Override
    public boolean activate(String mail, String code) throws Exception {
        System.out.println("验证码："+mail+",redis:"+code);
        String key="activation:"+mail;
        String value=stringRedisTemplate.opsForValue().get("activation:" + mail);
        System.out.println("---------redis:"+value);
        if (null!=code&&value.equals(code)){
            User user=userService.getUser(mail);    //验证是否存在
            if (null!=user){
                user.setStatusId(1);
                user.setUserSource("qq邮箱");
                userService.update(user);
                return true;
            }
        }
        return false;
    }
    /**
     * 手机短信注册
     *
     * @param user
     * @throws Exception
     */
    @Override
    public void educationCreateUserByPhone(User user) throws Exception {
        //1.创建用户
        userService.insert(user);
        //2.生成验证码
        int code = MD5.getRandomCode();   //产生4位随机数
        //3.发送验证码
        String[] datas = {String.valueOf(code), "1"};
        smsService.send(user.getMobileUser(), "1", datas);
        //4.缓存短信验证码到redis
        stringRedisTemplate.opsForValue().set("activation:" + user.getMobileUser(),String.valueOf(code),2*60, TimeUnit.SECONDS);
        System.out.println("huoqu:"+stringRedisTemplate.opsForValue().get("activation:" + user.getMobileUser()));
    }

    /**
     * 短信验证
     *
     * @param phoneNum 手机号
     * @param code     验证码
     * @return
     */
    @Override
    public boolean validatePhone(String phoneNum, String code) throws Exception {
        System.out.println("验证码："+phoneNum+",redis:"+code);
        String key="activation:"+phoneNum;
        String value=stringRedisTemplate.opsForValue().get("activation:" + phoneNum);
        System.out.println("---------redis:"+value);
        if (null!=code&&value.equals(code)){
            User user=userService.getUser(phoneNum);    //验证是否存在
            if (null!=user){
                user.setStatusId(1);
                user.setUserSource("手机号");
                userService.update(user);
                return true;
            }
        }
        return false;
    }

}
