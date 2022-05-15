package com.sms.api.smsAPI.controller;

import com.sms.api.smsAPI.dto.SmsDto;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;

@Slf4j // Logging
@RequestMapping("/sms/client") //
@PropertySource("classpath:api.properties")
@RestController
public class SmsController {

        @Value("${sms.id}")
        private String id;

        @Value("${sms.key}")
        private String key;

        @Value("utf-8")
        String encodingType;

        @Value("https://apis.aligo.in/")
        String sms_url;

        // Send SMS Request
        @PostMapping("/send")
        public JSONObject SendSMS(@RequestBody @Valid SmsDto sms, BindingResult bindingResult) throws Exception {
            // check default parameters for sending
            // params : receiver, msg, uid, mid
            // Error Handling
            ErrorHandling(bindingResult);

            sms.setUser_id(id);
            sms.setKey(key);
            log.info("@@@@ check dto : " + sms.toString());


            String url = "send/";
            JSONObject result = sendSMS(sms, url);

            return result;
        }
        // Get SMS Sent History List
        @GetMapping("/list")
        public JSONObject getSendList(@RequestBody @Valid SmsDto sms, BindingResult bindingResult) throws Exception {
            // Error Handling
            ErrorHandling(bindingResult);
            sms.setUser_id(id);
            sms.setKey(key);
            String url = "list/";
            JSONObject result = sendSMS(sms, url);

            return result;
        }

        // Get SMS Sent Detail History
        @GetMapping("/detail")
        public JSONObject getDetailList(@RequestBody @Valid SmsDto sms, BindingResult bindingResult) throws Exception {
            ErrorHandling(bindingResult);
            sms.setUser_id(id);
            sms.setKey(key);
            String url = "sms_list/";
            JSONObject result = sendSMS(sms, url);

            return result;
        }

        // Error Handling Method
        private void ErrorHandling(BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(objectError -> {
                    log.error("code : " + objectError.getCode());
                    log.error("defaultMessage : " + objectError.getDefaultMessage());
                    log.error("objectName : " + objectError.getObjectName());
                });
            }
        }

        // Request Client for SMS Server
        private JSONObject sendSMS(SmsDto sms, String url) throws IOException, IllegalAccessException, ParseException {
            String image = "";
            File imgFile = new File(image);

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            // Why builder ?
            // This API parameter content type is 'urlencode'
            // builder supports 'urlencode' , and easy add key&value

            Object obj = sms;
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                String key = f.getName();
                Object val = f.get(obj);
                if (val == null || ("").equals(val.toString())) {
                    val = "";
                }
                builder.part(key, val);
                log.info("####### Builder part key : " + key + " , value : " + val + " #######");
            }

            // Request Check image file
            if (image != null && 0 < image.length() && imgFile.exists()) {
                byte[] filecontent = Files.readAllBytes(imgFile.toPath());
                builder.part("image", new ByteArrayResource(filecontent));
            }

            // WebFlux -> WebClient is Spring before RESTtemplate after WebClient ,
            // WebCLient is Non-Blocking, Single Thread , easy JSON & XML requst and response
            String response = WebClient.create()
                    .post()
                    .uri(sms_url + url)
                    .contentType(MediaType.APPLICATION_JSON) // Request Type JSON
                    .acceptCharset(Charset.forName(encodingType)) // CharSet Setting
                    .body(BodyInserters.fromMultipartData(builder.build())) // body : Request Params key&value , content type 'urlencode'
                    .retrieve().bodyToMono(String.class)
                    .block();

            log.info("##### check response : " + response);

            // need return response result to content type JSON
            // Convert String to JSON
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(response);


            return jsonObj;
        }

}
