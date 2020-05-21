package com.nb.security.controller;

import com.nb.security.OauthClient.RestOauthClient;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
  @Autowired
  RestOauthClient oauth2RestTemplate;


  @RequestMapping("/user/token")
  public Map<String, Object> userToken(@RequestParam Map<String, String> parameters) {
      parameters.put("grant_type","password");
      parameters.put("client_id","orderService");
      parameters.put("client_secret","123456");//scope
      parameters.put("scope","read");
      return oauth2RestTemplate.postAccessToken(parameters);
  }

  @RequestMapping("/client/token")
  public Map<String, Object> clientToken(@RequestParam Map<String, String> parameters) {
      parameters.put("grant_type","client_credentials");
      return oauth2RestTemplate.postAccessToken(parameters);
  }

}
