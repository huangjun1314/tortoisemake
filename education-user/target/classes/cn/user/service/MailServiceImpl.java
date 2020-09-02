package cn.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService{

    @Autowired
    JavaMailSender javaMailSender;

    /**
     * 简单邮件发送
     *
     * @param from    发送人
     * @param to      收件人
     * @param cc      抄送人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendSimpleMail(String from, String to, String cc, String subject, String content) {
        SimpleMailMessage simple = new SimpleMailMessage();
        simple.setFrom(from);
        simple.setTo(to);
        simple.setCc(cc);
        simple.setSubject(subject);
        simple.setText(content);
        //发送
        javaMailSender.send(simple);
    }

}
