CREATE TABLE member
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    login_id     VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(255) NOT NULL UNIQUE,
    stream_key VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE member_channel
(
    id                  SERIAL PRIMARY KEY,
    title               BIGINT NOT NULL UNIQUE,
    member_id           BIGINT NOT NULL UNIQUE,
    streamer_nickname   VARCHAR(255) NOT NULL,
    total_streaming     INTEGER NOT NULL,
    on_air              BOOLEAN NOT NULL
);

CREATE TABLE point
(
    id                  SERIAL PRIMARY KEY,
    member_id           BIGINT NOT NULL UNIQUE,
    point               INTEGER NOT NULL,
    nickname            VARCHAR(255) NOT NULL
);

CREATE TABLE donation
(
    id                  SERIAL PRIMARY KEY,
    streamer_id         BIGINT NOT NULL UNIQUE,
    donater_id          BIGINT NOT NULL UNIQUE,
    contents            VARCHAR(255) NOT NULL,
    donated_at          TIMESTAMP NOT NULL,
    donate_point        INTEGER NOT NULL
);


ALTER TABLE member_channel
ADD started_at DATETIME;
