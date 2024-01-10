package com.hanghae.lemonairservice.service;

import com.hanghae.lemonairservice.dto.point.DonationWebClientDto;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRankingDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.repository.PointLogRepository;
import com.hanghae.lemonairservice.repository.PointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;
    private final MemberRepository memberRepository;


    @Autowired
    private WebClient webClient;


    public Mono<ResponseEntity<PointResponseDto>> getPoint(Member member) {
        log.info("member.getId() : " + member.getId());
        return pointRepository.findByMemberId(member.getId())
            .map(point -> ResponseEntity.ok().body(new PointResponseDto(member, point.getPoint())))
            .switchIfEmpty(
                Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다.")));
    }

    public Mono<ResponseEntity<PointResponseDto>> addpoint(AddPointRequestDto addPointRequestDto,
        Member member) {
        return pointRepository.findByMemberId(member.getId())
            .log()
            .switchIfEmpty(
                Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다.")))
            .flatMap(point -> pointRepository.save(point.addPoint(addPointRequestDto.getPoint()))
                .log()
                .onErrorResume(
                    exception -> Mono.error(
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "point 추가 오류")))
                .map(savedPoint -> ResponseEntity.ok()
                    .body(new PointResponseDto(member, point.getPoint()))))
            .log();
    }

    @Transactional
    public Mono<ResponseEntity<DonationResponseDto>> usePoint(DonationRequestDto donationRequestDto,
        Member member, Long streamerId) {


        log.info("member nickname: " + member.getNickname());
        return pointRepository.findByMemberId(member.getId())
            .flatMap(donater -> {
                if (Objects.equals(streamerId, donater.getId())) {
                    return Mono.error(
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 방송에 후원하실 수 없습니다."));
                }

                log.info(
                    "donationRequestDto.getDonatePoint() : " + donationRequestDto.getDonatePoint());
                log.info("donationRequestDto.getContents() : " + donationRequestDto.getContents());

                int donatePoint = donationRequestDto.getDonatePoint();

                log.info("donatePoint : " + donatePoint);
                if (donater.getPoint() < donatePoint) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "후원 할 수 있는 금액이 부족합니다."));
                }

                log.info("webClient : " + webClient);

                log.info("streamerId : " + streamerId);

                return pointRepository.findByMemberId(streamerId)
                    .log()
                    .flatMap(streamerPoint -> {
                        return pointRepository.save(donater.usePoint(donatePoint))
                            .onErrorResume(
                                exception -> Mono.error(
                                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "point 사용 오류")))
                            .log()
                            .then(pointRepository.save(streamerPoint.addPoint(donatePoint)))
                            .onErrorResume(
                                exception -> Mono.error(
                                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "point 추가 오류")))
                            .then(
                                webClient
                                    .post()
                                    .uri("http://localhost:8082/api/donation/{streamerId}", streamerId.toString())
                                    .header(HttpHeaders.CONTENT_TYPE,
                                        MediaType.APPLICATION_JSON_VALUE)
                                    .bodyValue(
                                        new DonationWebClientDto(member.getNickname(), streamerId,
                                            donationRequestDto.getContents(), donatePoint))
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .then(Mono.fromCallable(
                                        () -> ResponseEntity.ok(new DonationResponseDto(
                                            member.getId(),
                                            member.getNickname(),
                                            streamerId,
                                            donationRequestDto.getContents(),
                                            donater.getPoint(),
                                            LocalDateTime.now().toString()
                                        )))));
                    });
            });
    }

    public Mono<ResponseEntity<Flux<DonationRankingDto>>> donationRank(Member member) {
        Flux<DonationRankingDto> donationRankDto = pointLogRepository.findByStreamerIdOrderBySumOfDonateLimit10(
                member.getId())
            .concatMap(userId -> pointRepository.findById(userId).flux())
            .map(point -> new DonationRankingDto(point.getNickname()));

        return Mono.just(ResponseEntity.ok(donationRankDto));
    }
}