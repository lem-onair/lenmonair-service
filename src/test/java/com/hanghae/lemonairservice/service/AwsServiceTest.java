package com.hanghae.lemonairservice.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AwsServiceTest {

	@InjectMocks
	private AwsService awsService;

	@Test
	void getThumbnailCloudFrontUrlSuccess(){

		String streamerLoginId = "mbkang922";
		String uri = "/" + streamerLoginId + "/thumbnail/" + streamerLoginId + "_thumbnail.jpg";
		String generatedUrl = awsService.getThumbnailCloudFrontUrl(streamerLoginId);

		assertThat(generatedUrl).isEqualTo("null"+uri);
	}
	@Test
	void getM3U8CloudFrontUrl(){

		String streamerLoginId = "mbkang922";
		String uri = "/" + streamerLoginId + "/videos/" + "m3u8-" + streamerLoginId + ".m3u8";
		String generatedUrl = awsService.getM3U8CloudFrontUrl(streamerLoginId);

		assertThat(generatedUrl).isEqualTo("null"+uri);
	}
}
