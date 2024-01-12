package com.hanghae.lemonairservice.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;

@RestController
public class PaymentController {
	private IamportClient iamportClient;

	@Value("${kakao.key}")
	private String APIKey;
	@Value("${kakao.secret}")
	private String APISecret;

	@PostConstruct
	public void PaymentController() {
		this.iamportClient = new IamportClient(APIKey, APISecret);
	}

	@PostMapping("/verifyIamport/{imp_uid}")
	public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws
		IamportResponseException,
		IOException {
		return iamportClient.paymentByImpUid(imp_uid);
	}
}