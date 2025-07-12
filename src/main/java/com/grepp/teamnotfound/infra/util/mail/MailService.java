package com.grepp.teamnotfound.infra.util.mail;

import com.grepp.teamnotfound.infra.error.exception.AuthException;
import com.grepp.teamnotfound.infra.error.exception.CommonException;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${email.verification.code.expiration-seconds}")
    private long expirationSeconds;


    public void sendVerificationEmail(String toEmail){
        String verifyCode = VerifyCodeGenerator.generateCode();
        String subject = "[🐶멍멍일지] 회원가입 인증 코드입니다.";
        String text = "인증 코드: " + verifyCode + "\n " + (expirationSeconds/60) + "분 이내에 인증코드를 인증 란에 입력해주세요.";

        stringRedisTemplate.opsForValue().set(
                "email: verifying " + toEmail,
                verifyCode,
                Duration.ofSeconds(expirationSeconds)
        );

        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

        } catch (MailException e) {
            stringRedisTemplate.delete("email: verifying " + toEmail);
            // MailException은 복구 안 되는 오류니, 그냥 Runtime으로 둠
            throw new CommonException(UserErrorCode.EMAIL_VERIFICATION_SEND_FAILED);
        }
    }


    public void verifyEmailCode(String email, String code){
        String redisKey = "email: verifying " + email;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (storedCode == null || !storedCode.equals(code)) {
            // 유저의 입력 오류에 따른 예외니, 그냥 Runtime으로 둠
            throw new AuthException(UserErrorCode.EMAIL_VERIFICATION_FAILED);
        }
        stringRedisTemplate.delete(redisKey);
    }
}
