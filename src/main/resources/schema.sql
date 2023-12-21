CREATE TABLE member
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    login_id     VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(255) NOT NULL UNIQUE,
    stream_key VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE member_channel
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    title               VARCHAR(255) NOT NULL UNIQUE,
    member_id           BIGINT NOT NULL UNIQUE,
    streamer_nickname   VARCHAR(255) NOT NULL,
    on_air              BOOLEAN NOT NULL
);

ALTER TABLE member_channel
ADD started_at DATETIME;
