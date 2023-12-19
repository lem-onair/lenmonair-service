package com.hanghae.lemonairservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.security.UserDetailsImpl;
import com.hanghae.lemonairservice.service.StreamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/streams")
@Slf4j
public class StreamController {
	private final StreamService streamService;

	// @PostMapping("/{streamerId}/check")
	// public ResponseEntity<Boolean> checkStreamValidity(@PathVariable String streamerId, @RequestBody StreamKeyRequestDto streamKey) {
	//     // return streamService.checkStreamValidity(streamerId, streamKey);
	//     return ResponseEntity.ok(false);
	// }

	@PostMapping("/onair")
	public Mono<ResponseEntity<String>> startStream(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return streamService.startStream(userDetails.getMember())
			.map(started -> {
			if (started) {
				return ResponseEntity.ok("방송이 시작됩니다. : " + userDetails.getLoginId());
			} else {
				return ResponseEntity.badRequest().body("방송 시작을 실패하였습니다. : " + userDetails.getLoginId());
			}
		});
	}

	   @PostMapping("/offair")
	   public Mono<ResponseEntity<String>> stopStream(@AuthenticationPrincipal UserDetailsImpl userDetails) {
	       return streamService.stopStream(userDetails.getMember())
			   .map(stopped ->{
				   if (stopped) {
					   return ResponseEntity.ok("스트리밍이 종료 되었습니다. : " + userDetails.getLoginId());
				   } else {
					   return ResponseEntity.badRequest().body("스트리밍 종료가 실패하였습니다. : " + userDetails.getLoginId());
				   }
			   });
	   }

}
