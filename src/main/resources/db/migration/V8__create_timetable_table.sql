-- V8__create_timetable_table.sql
-- 시간표 테이블 생성

CREATE TABLE IF NOT EXISTS timetable (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- [수정됨] ON DELETE CASCADE 제거
    -- user가 삭제될 때 timetable이 연쇄 삭제되는 것을 방지.
    -- 삭제 로직은 Service 레이어가 책임져야 함.
                                         user_id BIGINT NOT NULL REFERENCES users(id),

    name VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    semester VARCHAR(50) NOT NULL
    );

-- 특정 유저의 시간표 목록을 빠르게 찾기 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_timetable_user ON timetable(user_id);