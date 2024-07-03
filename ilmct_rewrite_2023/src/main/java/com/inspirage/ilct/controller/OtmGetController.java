package com.inspirage.ilct.controller;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.service.OtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/v1/otm-get/")
@CrossOrigin

public class OtmGetController {

	@Autowired
	OtmService otmService;


	@GetMapping(value = "getCurrentRoute")
	public ResponseEntity<ApiResponse> getCurrentRoute(@RequestParam(value = "loadId") String loadId) {
		ApiResponse response = otmService.getCurrentRoute(loadId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


}
