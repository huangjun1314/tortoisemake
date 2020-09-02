package cn.user.service;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void send(String to, String templateId, String[] datas) {
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init("app.cloopen.com", "8883");
        sdk.setAccount("8a216da8701eb7c1017042679fb10f4b", "eefbb70ad59342729aac17f8c86f84f5");//设置账号id
        sdk.setAppId("8a216da8701eb7c101704267a00f0f51");
        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
        if ("000000".equals(result.get("statusCode"))) {
            System.out.println("短信发送成功");
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
            try {
                throw new Exception(result.get("statusCode").toString() + " 错误信息= " + result.get("statusMsg").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
