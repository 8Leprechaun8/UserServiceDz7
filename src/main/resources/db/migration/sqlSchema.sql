--liquibase formatted sql

--changeset dvoyevodin_13062026:1
--preconditions onFail:HALT onError:HALT
CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL,
    email VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

--changeset dvoyevodin_13062026:2
--preconditions onFail:HALT onError:HALT
CREATE TABLE IF NOT EXISTS roles (
    user_id uuid NOT NULL,
    title VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, title),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

--changeset dvoyevodin_13062026:3
--preconditions onFail:HALT onError:HALT
INSERT INTO users (id, username, password, is_active, email, age, created_at)
VALUES ('bda4b32d-7e09-4dd5-8dd4-afe3260d358b', 'mokrushin',
'$2a$10$RJ9Zpo0wUToyyTHNBM3uaukfSzto7JUc3eL6mCfu.un1hbpQpbZQ2', true,
'mokrushin@mail.ru', 33, '2026-06-15 14:30:00+03');

--changeset dvoyevodin_13062026:4
--preconditions onFail:HALT onError:HALT
INSERT INTO users (id, username, password, is_active, email, age, created_at)
VALUES ('57894ae8-7ddf-426c-a161-3b5470d94d7c', 'snegirev',
'$2a$10$RJ9Zpo0wUToyyTHNBM3uaukfSzto7JUc3eL6mCfu.un1hbpQpbZQ2', true,
'snegirev@mail.ru', 34, '2026-06-15 14:30:00+03');

--changeset dvoyevodin_13062026:5
--preconditions onFail:HALT onError:HALT
INSERT INTO roles (user_id, title)
VALUES ('bda4b32d-7e09-4dd5-8dd4-afe3260d358b', 'ROLE_ADMIN');

--changeset dvoyevodin_13062026:6
--preconditions onFail:HALT onError:HALT
INSERT INTO roles (user_id, title)
VALUES ('bda4b32d-7e09-4dd5-8dd4-afe3260d358b', 'ROLE_USER');

--changeset dvoyevodin_13062026:7
--preconditions onFail:HALT onError:HALT
INSERT INTO roles (user_id, title)
VALUES ('57894ae8-7ddf-426c-a161-3b5470d94d7c', 'ROLE_USER');
