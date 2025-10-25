-- V10__create_class_session_table.sql
-- 강의 시간/장소/형태 테이블 생성
-- (V7__create_lecture_table.sql에서 분리된 데이터)

CREATE TABLE IF NOT EXISTS class_session (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- [주의] 외래 키 (FK): lecture(id)를 참조합니다.
    -- lecture 테이블의 행(강의)이 삭제되면(ON DELETE CASCADE),
    -- 여기에 연결된 모든 시간/장소 데이터도 자동으로 연쇄 삭제됩니다.
                                          lecture_id BIGINT NOT NULL REFERENCES lecture(id) ON DELETE CASCADE,

    -- 파싱된 강의 시간 정보 (모두 NULL 허용)
    day_of_week INT NULL,          -- 요일 (0=월, 1=화 ...)
    start_time INT NULL,           -- 시작 시간 (분 단위, 예: 10:00 -> 600)
    end_time INT NULL,             -- 종료 시간 (분 단위, 예: 11:50 -> 710)

    location VARCHAR(255) NULL,    -- 강의실
    course_format VARCHAR(255) NULL  -- 수업 형태 (예: "이론", "실습", "온라인")
    );

-- 특정 강의에 속한 시간들을 빠르게 찾기 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_class_session_lecture_id ON class_session(lecture_id);