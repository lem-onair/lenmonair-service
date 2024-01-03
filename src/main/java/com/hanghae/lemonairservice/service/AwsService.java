package com.hanghae.lemonairservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AwsService {

	@Value("${aws.cloudfront.domain}")
	private String cloudFrontDomain;

	public String getThumbnailCloudFrontUrl(String streamerLoginId) {
		String uri = "/" + streamerLoginId + "/thumbnail/" + streamerLoginId + "_thumbnail.jpg";
		return cloudFrontDomain + uri;
	}

	public String getM3U8CloudFrontUrl(String streamerLoginId) {
		String uri = "/" + streamerLoginId + "/videos/" + "m3u8-" + streamerLoginId + ".m3u8";
		return cloudFrontDomain + uri;
	}
}
