package com.hanghae.lemonairservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.s3.AmazonS3;
import com.hanghae.lemonairservice.entity.Member;

@ExtendWith(MockitoExtension.class)
public class AwsServiceTest {

	@InjectMocks
	private AwsService awsService;

	@Test
	void getThumbnailCloudFrontUrl(){

		String streamerId = "mbkang922";
		String expectedUrl = "https://your-cloudfront-domain.com/testStreamer/thumbnail/testStreamer_thumbnail.jpg";
	}



}
