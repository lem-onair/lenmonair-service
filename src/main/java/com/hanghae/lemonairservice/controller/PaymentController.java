package com.hanghae.lemonairservice.controller;

import java.io.IOException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
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

	@PostConstruct
	public void PaymentController() {
		this.iamportClient = new IamportClient("7485758722740306",
			"18cJeOZQsnD3Zf9DGnnJ8NOMdXwrq3wROUBfFghmacXRO1BGYxijfuLIKQFe9Fa1Vk7ECDqPpLBIgs3z");
	}

	@PostMapping("/verifyIamport/{imp_uid}")
	public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid)
		throws IamportResponseException, IOException {
		return iamportClient.paymentByImpUid(imp_uid);
	}


}