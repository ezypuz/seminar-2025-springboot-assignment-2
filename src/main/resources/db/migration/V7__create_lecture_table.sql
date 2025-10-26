-- 강의 마스터 테이블 생성 (모든 컬럼 NULL 허용)

CREATE TABLE IF NOT EXISTS lecture (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- [수정] String 타입 및 NULL 허용
    year VARCHAR(50) NULL,
    semester VARCHAR(50) NULL,

    -- 강의 상세 정보 (엑셀 데이터)
    division VARCHAR(255) NULL,
    college VARCHAR(255) NULL,
    department VARCHAR(255) NULL,
    course_type VARCHAR(255) NULL, -- courseType
    grade INT NULL,
    course_number VARCHAR(100) NULL, -- courseNumber
    lecture_number VARCHAR(100) NULL, -- lectureNumber
    course_title VARCHAR(255) NULL, -- courseTitle
    subtitle VARCHAR(255) NULL,
    credits DECIMAL(3, 1) NULL, -- credits (학점)
    class_time INT NULL, -- classTime (강의 시수)
    lab_time INT NULL, -- labTime (실습 시수)
    professor VARCHAR(255) NULL,

    -- 장바구니/정원 정보
    pre_registration_count INT NULL, -- preRegistrationCount
    pre_registration_count_for_non_freshman INT NULL, -- preRegistrationCountForNonFreshman
    pre_registration_count_for_freshman INT NULL, -- preRegistrationCountForFreshman
    quota INT NULL, -- [수정] quota (NOT NULL -> NULL)
    nonfreshman_quota INT NULL, -- nonfreshmanQuota
    registration_count INT NULL, -- registrationCount

    -- 기타 정보
    remark TEXT NULL, -- 비고 (길 수 있으므로 TEXT)
    language VARCHAR(255) NULL,
    status VARCHAR(255) NULL
);