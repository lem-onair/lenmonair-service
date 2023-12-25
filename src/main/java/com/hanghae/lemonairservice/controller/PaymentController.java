package com.hanghae.lemonairservice.controller;

import java.io.IOException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.PostConstruct;

@Controller
public class PaymentController {
	private IamportClient iamportClient;

	@PostConstruct
	public void IamPortController() {
		this.iamportClient = new IamportClient("7485758722740306",
			"i7NtziVw7jwtGoIuDSZH7hJ51oKEgdXY4tMhnBL6DWLKl22afBB5jLoAWDikHpyzcs8G2I4Hb7E96OZw");
	}

	@ResponseBody
	@RequestMapping("/verify/{imp_uid}")
	public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws
		IamportResponseException,
		IOException {
		return iamportClient.paymentByImpUid(imp_uid);
	}





}