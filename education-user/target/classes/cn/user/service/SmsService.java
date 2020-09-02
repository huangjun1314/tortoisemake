package cn.user.service;

//发送短信接口
public interface SmsService {

    /**
     * 发送短信接口
     *
     * @param to         发送给谁
     * @param templateId 模板id
     * @param datas      内容
     */
    public void send(String to, String templateId, String[] datas);
}
