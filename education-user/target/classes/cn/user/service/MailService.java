package cn.user.service;

public interface MailService {
    /**
     * 简单邮件发送
     *
     * @param from    发送人
     * @param to      收件人
     * @param cc      抄送人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendSimpleMail(String from, String to, String cc, String subject, String content);
}
