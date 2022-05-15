package com.sms.api.smsAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@AllArgsConstructor
public class SmsDto {
    /******************** 인증정보 ********************/
    private String user_id; // SMS 아이디
    private String key; //인증키

    /******************** 전송정보 ********************/
    @NotBlank(message = "보낼 메세지를 입력하세요")
    private String msg;// 메세지 내용

    @NotBlank(message = "받으실 분의 번호를 입력하세요")
    @Size(min = 9, max = 12, message = "받으실 분의 번호가 너무 길거나 짧습니다")
    private String receiver; // 수신번호

    private String destination;// 수신인 %고객명% 치환

    private String sender;// 발신번호

    private String rdate;// 예약일자 - 20161004 : 2016-10-04일기준

    private String rtime; // 예약시간 - 1930 : 오후 7시30분

    private String testmode_yn;// Y 인경우 실제문자 전송X , 자동취소(환불) 처리

    private String title;//  LMS, MMS 제목 (미입력시 본문중 44Byte 또는 엔터 구분자 첫라인)

    private String image;// 이미지 첨부파일

    /********* Save Sending history **********/
    private String rid; // request id

    private String mid; // mid

    private String uid;

    /********* get SMS history ***********/
    private String page;

    private String page_size;

    private String start_date;

    private String limit_day;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
