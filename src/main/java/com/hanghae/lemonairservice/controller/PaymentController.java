package com.hanghae.lemonairservice.controller;

import java.io.IOException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
		this.iamportClient = new IamportClient(APIKey,APISecret);
	}

	// payments.js에서 33~42번 줄의 코드가
	// 1건의 결제 정보이고, 이 결제 정보의 고유번호가 imp_uid입니다.
	// url의 경로를 통해 카카오페이 결제 api를 제공하는 회사의 서버로 데이터를 전송하고
	// 검사가 완료되면 카카오 결제 기능 창과 함께 데이터를 담아서 전송해줍니다.
	@PostMapping("/verifyIamport/{imp_uid}")
	public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid)
		throws IamportResponseException, IOException {
		return iamportClient.paymentByImpUid(imp_uid);
	}


}