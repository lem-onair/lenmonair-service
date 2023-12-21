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
    title               VARCHAR(255) NOT NULL UNIQUE,
    member_id           VARCHAR(255) NOT NULL UNIQUE,
    streamer_nickname   VARCHAR(255) NOT NULL,
    total_streaming     INTEGER NOT NULL,
    on_air              BOOLEAN NOT NULL
);

ALTER TABLE member_channel
ADD started_at DATETIME;
