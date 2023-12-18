package com.hanghae.lemonairservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AwsService {
	private final AmazonS3 amazonS3;

	@Value("${aws.s3.bucket}")
	private String bucket;

	@Value("${aws.cloudfront.domain}")
	private String cloudFrontDomain;

	public String getThumbnailCloudFrontUrl(String streamerLoginId) {
		String key = streamerLoginId + "/thumbnail/" + streamerLoginId + "_thumbnail.jpg";
		return cloudFrontDomain + amazonS3.getUrl(bucket, key).getPath();
	}

	public String getM3U8CloudFrontUrl(String streamerLoginId) {
		String key = streamerLoginId + "/videos/" + "m3u8-" + streamerLoginId + ".m3u8";
		return cloudFrontDomain + amazonS3.getUrl(bucket, key).getPath();
	}
}
