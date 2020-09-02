package cn.user.controller;

import cn.beans.dto.Dto;
import cn.beans.entity.User;
import cn.beans.vo.UserVO;
import cn.common.utils.DtoUtil;
import cn.common.utils.MD5;
import cn.dao.aop.MyLog;
import cn.dao.userservice.user.UserService;
import cn.user.service.SignService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用户基本信息(User)表控制层
 *
 * @author makejava
 * @since 2020-05-08 22:25:41
 */
@RestController
@Api(tags = "用户管理接口")
public class UserController {
    @Autowired
    UserService userService;    //用户接口
    @Autowired
    SignService signService;    //注册接口

    /**
     * 短信注册
     * @param
     * @return
     */
    @PostMapping("/doRegisterByPhone")
    @ApiOperation(value = "手机短信注册",notes = "用户名、密码、用户编号")
    public Dto doRegisterByPhone(String code,String password,String username){
        //1.手机号验证、验证号码格式
        if (!this.validPhone(username)) {
            return DtoUtil.returnSuccess("请使用正确的手机号");
        }
        //2.调用Service
        User user = new User();
        user.setMobileUser(username);
        try {
           User ur=userService.getUser(username);
            if (null != ur) {
                return DtoUtil.returnSuccess("用户已存在");
            } else {
                user.setMobilePsw(password);
                user.setUserNo(Long.valueOf(code));
                user.setStatusId(0);
                user.setMobilePsw(MD5.getMd5(user.getMobilePsw(), 32));
                signService.educationCreateUserByPhone(user);
                return DtoUtil.returnSuccess("手机注册成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }

    /**
     * 手机短信验证
     * @param username
     * @param code
     * @return
     */
    @PostMapping("/validatePhone")
    @ApiOperation(value = "手机短信验证",notes = "用户名、验证码")
    public Dto validatePhone(String code,String username){
        try {
            if (signService.validatePhone(username, code)) {
                return DtoUtil.returnSuccess("验证成功");
            } else {
                return DtoUtil.returnSuccess("验证失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }


    /**
     * 邮箱注册
     * @param
     * @return
     */
    @PostMapping("/doRegisterByMail")
    @ApiOperation(value = "邮箱注册",notes = "用户名、密码、用户编号")
    public Dto doRegisterByMail(String code,String password,String username){
        //1.手机号验证、验证号码格式
        if (!this.validEmail(username)) {
            return DtoUtil.returnSuccess("请使用正确的邮箱");
        }
        //2.调用Service
        User user = new User();
        user.setMobileUser(username);
        try {
            User ur=userService.getUser(username);
            if (null != ur) {
                return DtoUtil.returnSuccess("用户已存在");
            } else {
                user.setMobilePsw(password);
                user.setUserNo(Long.valueOf(code));
                user.setStatusId(0);
                user.setMobilePsw(MD5.getMd5(user.getMobilePsw(), 32));
                signService.educationCreateUser(user);
                return DtoUtil.returnSuccess("邮箱注册成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }

    /**
     * 邮箱验证
     * @param username
     * @param code
     * @return
     */
    @PostMapping("/validateMail")
    @ApiOperation(value = "邮箱验证",notes = "用户名、验证码")
    public Dto validateMail(String code,String username){
        try {
            if (signService.activate(username, code)) {
                return DtoUtil.returnSuccess("验证成功");
            } else {
                return DtoUtil.returnSuccess("验证失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }


    /**
     * 通过主键查询单条数据
     * @param id 主键
     * @return 单条数据
     */
    @MyLog("根据id查询用户信息")
    @PostMapping("/api/getUserById")
    @ApiOperation(value = "根据id查询用户信息",notes = "id")
    public Dto getUserById(Long id){
        try{
            if (id>0){
                User user=userService.queryById(id);
                if (user!=null){
                    return DtoUtil.returnSuccess("200",user);
                }
                return DtoUtil.returnSuccess("没有用户信息");
            }
            return DtoUtil.returnSuccess("用户id不能为空");
        }catch (Exception e){
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }

    @MyLog("根据id修改用户密码")
    @PostMapping("/api/updateByMobilePsw")
    @ApiOperation(value = "根据id修改用户密码",notes = "String")
    public Dto updateByMobilePsw(Long id,String mobilePsw){
        try{
            if (id>0&&mobilePsw!=null){
                User u=new User();
                u.setId(id);
                u.setMobilePsw(mobilePsw);
                u.setGmtModified(new Date());
                User user=userService.update(u);
                if (user!=null){
                    return DtoUtil.returnSuccess("200",user);
                }
                return DtoUtil.returnSuccess("密码修改失败");
            }
            return DtoUtil.returnSuccess("用户id或新密码不能为空");
        }catch (Exception e){
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }

    @MyLog("修改用户信息")
    @PostMapping("/api/updateUser")
    @ApiOperation(value = "根据id查询用户信息",notes = "User对象")
    public Dto updateUser(@RequestBody User user){
        try{
            if (user!=null){
                User u=userService.update(user);
                if (u!=null){
                    return DtoUtil.returnSuccess("200",user);
                }
                return DtoUtil.returnSuccess("修改用户信息失败");
            }
            return DtoUtil.returnSuccess("参数不能为空");
        }catch (Exception e){
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }

    /**
     * 合法E-mail地址：
     * 1. 必须包含一个并且只有一个符号“@”
     * 2. 第一个字符不得是“@”或者“.”
     * 3. 不允许出现“@.”或者.@
     * 4. 结尾不得是字符“@”或者“.”
     * 5. 允许“@”前的字符中出现“＋”
     * 6. 不允许“＋”在最前面，或者“＋@”
     */
    private boolean validEmail(String email) {
        String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        return Pattern.compile(regex).matcher(email).find();
    }

    /**
     * 验证是否合法的手机号
     *
     * @param phone
     * @return
     */
    private boolean validPhone(String phone) {
        String regex = "^1[3578]{1}\\d{9}$";
        return Pattern.compile(regex).matcher(phone).find();
    }
}