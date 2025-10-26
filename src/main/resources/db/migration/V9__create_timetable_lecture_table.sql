-- 시간표-강의 연결 (중간 테이블) 생성

CREATE TABLE IF NOT EXISTS timetable_lecture (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- 외래 키: timetable (시간표)
    timetable_id BIGINT NOT NULL,

    -- 외래 키: lecture (강의)
    lecture_id BIGINT NOT NULL,

    -- FOREIGN KEY 제약조건을 별도로 정의
    -- 시간표가 삭제되면(ON DELETE CASCADE) 이 연결도 함께 삭제됨
    CONSTRAINT fk_timetable_lecture_timetable
    FOREIGN KEY (timetable_id)
    REFERENCES timetable(id)
    ON DELETE CASCADE,

    -- 강의가 삭제되면(ON DELETE CASCADE) 이 연결도 함께 삭제됨
    CONSTRAINT fk_timetable_lecture_lecture
    FOREIGN KEY (lecture_id)
    REFERENCES lecture(id)
    ON DELETE CASCADE

    -- [수정됨] UNIQUE(timetable_id, lecture_id) 제약조건을 제거함.
    -- 중복 강의 추가 여부는 Service 로직에서 검증.
);
