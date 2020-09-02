package cn.user.controller;

import cn.beans.dto.Dto;
import cn.beans.entity.User;
import cn.common.utils.DtoUtil;
import cn.common.utils.MD5;
import cn.dao.userservice.user.UserService;
import cn.user.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "用户登录模块")
public class LoginController {

    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;

	//你好还不错哟
    @PostMapping("/dologin")
    @ApiOperation(value = "登录方法",notes = "登录方法")
    public Dto dologin(String usernae, String password, HttpServletRequest request){
        try{
            User user=userService.getUserByuserNameAndpassWord(usernae, MD5.getMd5(password,32));
            if (user!=null && user.getStatusId()!=0){
                String userAgent=request.getHeader("user-agent");
                System.out.println("------"+userAgent);
                String token=tokenService.generateToken(userAgent,user);    //生成token
                tokenService.sava(token,user);  //保存到reids
                return DtoUtil.returnSuccess(token);
            }else{
                return DtoUtil.returnSuccess("用户名或密码错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }

    @PostMapping("/logout")
    @ApiOperation(value = "退出方法",notes = "退出方法")
    public Dto logout(String token, HttpServletRequest request){
        try{
            if (tokenService.validate(request.getHeader("user-agent"),token)){
                tokenService.delete(token);  //删除token
                return DtoUtil.returnSuccess("200");
            }else{
                return DtoUtil.returnSuccess("500");
            }
        }catch (Exception e){
            e.printStackTrace();
            return DtoUtil.returnFail("异常",e.getMessage());
        }
    }

}
