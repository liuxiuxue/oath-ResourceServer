package com.liuxiuxue.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class IndexController {

	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/")
	public String index(HttpServletRequest request, Model model){
		String code = request.getParameter("code");
		model.addAttribute("code", code);
		return "index";
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/token")
	public void getToken(HttpServletRequest request, HttpServletResponse response, Model model) throws ServletException, IOException{
		String code = request.getParameter("code");
		if (StringUtils.trimToNull(code) == null)
			throw new RuntimeException("code is not exist");
		model.addAttribute("code", code);
		String url = "http://127.0.0.1:8000/oauth/token?grant_type=authorization_code"
				+ "&redirect_uri=http://127.0.0.1:8001/csdn/token"
				+ "&code=" + code
				+ "&client_secret=csdn-app-secret"
				+ "&client_id=csdn-app-client";
				
		// 通过授权码请求token令牌
		Map<String, Object> map = restTemplate.postForObject(url, null, Map.class);
		String access_token = (String) map.get("access_token");
		@SuppressWarnings("unused")
		String refresh_token = (String) map.get("refresh_token");
		@SuppressWarnings("unused")
		String expires_in =  map.get("expires_in").toString();
		HttpHeaders headers = new HttpHeaders();
		if (access_token != null && !"".equals(access_token))
			headers.add("Authorization", "Bearer " + access_token);
//		request.getRequestDispatcher("/user").forward(request,response);
	}
	
	@GetMapping("/user")
	public String message(HttpServletRequest request, Model model, Principal principal){
		if (principal != null)
			model.addAttribute("name", principal.getName());
		return "user";
	}
	
}
