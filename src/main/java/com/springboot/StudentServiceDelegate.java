package com.springboot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class StudentServiceDelegate {

	@Autowired
    RestTemplate restTemplate;
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
	@HystrixCommand(fallbackMethod = "callStudentServiceAndGetData_fallback")
	public String callStudentServiceAndGetData(String schoolname) {
		System.out.println("Getting School details for " + schoolname);
		Map<String, String> params = new HashMap<String, String>();
	    params.put("schoolname", schoolname);
		/* 
        String response = restTemplate
                .exchange("http://localhost:8098/getStudentDetailsForSchool/{schoolname}"
                , HttpMethod.GET
                , null
                , new ParameterizedTypeReference<String>() {
            }, schoolname).getBody();
 */
        String response = restTemplate.getForObject("http://localhost:8098/getStudentDetailsForSchool/{schoolname}", String.class, params);
        
        System.out.println("Response Received as " + response + " -  " + new Date());
 
        return "NORMAL FLOW !!! - School Name -  " + schoolname + " :::  " +
                    " Student Details " + response + " -  " + new Date();
	}
	
	@SuppressWarnings("unused")
	private String callStudentServiceAndGetData_fallback(String schoolName) {
		System.out.println("Student Service is down!!! fallback route enabled...");
		 
        return "CIRCUIT BREAKER ENABLED!!! No Response From Student Service at this moment. " +
                    " Service will be back shortly - " + new Date();
	}

}
