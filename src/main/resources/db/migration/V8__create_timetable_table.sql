-- V8__create_timetable_table.sql
-- 시간표 테이블 생성

CREATE TABLE IF NOT EXISTS timetable (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- [수정됨] 올바른 FOREIGN KEY 구문 사용
                                         user_id BIGINT NOT NULL,

                                         name VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    semester VARCHAR(50) NOT NULL,

    -- FOREIGN KEY 제약조건을 별도로 정의
    CONSTRAINT fk_timetable_user FOREIGN KEY (user_id) REFERENCES users(id)
    );

