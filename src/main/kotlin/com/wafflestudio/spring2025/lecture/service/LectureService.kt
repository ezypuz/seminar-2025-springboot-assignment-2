package com.wafflestudio.spring2025.lecture.service

import com.wafflestudio.spring2025.lecture.dto.LectureSearchResponse
import com.wafflestudio.spring2025.lecture.dto.core.ClassSessionDto
import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.lecture.repository.ClassSessionRepository
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LectureService(
    private val lectureRepository: LectureRepository,
    private val classSessionRepository: ClassSessionRepository,
) {
    /**
     * 강의 검색
     * - 연도/학기로 필터링
     * - 키워드가 강의명 또는 교수명에 포함되는 강의 검색
     */
    fun searchLectures(
        year: String,
        semester: Semester,
        keyword: String,
        pageable: Pageable,
    ): LectureSearchResponse { // Page<LectureDto>
        val lectures =
            lectureRepository.findByYearAndSemesterAndKeyword(
                year = year,
                semester = semester,
                keyword = keyword,
                pageable = pageable,
            )

        // Page<Lecture>를 Page<LectureDto>로 변환
        return lectures.map { lecture ->
            // 각 강의의 세션 정보 조회
            val sessions =
                classSessionRepository
                    .findByLectureId(lecture.id!!)
                    .map { session ->
                        ClassSessionDto(
                            dayOfWeek = session.dayOfWeek,
                            startTime = session.startTime,
                            endTime = session.endTime,
                            location = session.location,
                            courseFormat = session.courseFormat,
                        )
                    }

            // LectureDto 생성 (LectureSearchResponse가 아님!)
            LectureDto(
                id = lecture.id!!,
                year = lecture.year,
                semester = lecture.semester,
                division = lecture.division,
                college = lecture.college,
                department = lecture.department,
                courseType = lecture.courseType,
                grade = lecture.grade,
                courseNumber = lecture.courseNumber,
                lectureNumber = lecture.lectureNumber,
                courseTitle = lecture.courseTitle,
                subtitle = lecture.subtitle,
                credits = lecture.credits,
                classTime = lecture.classTime,
                labTime = lecture.labTime,
                professor = lecture.professor,
                preRegistrationCount = lecture.preRegistrationCount,
                preRegistrationCountForNonFreshman = lecture.preRegistrationCountForNonFreshman,
                preRegistrationCountForFreshman = lecture.preRegistrationCountForFreshman,
                quota = lecture.quota,
                nonfreshmanQuota = lecture.nonfreshmanQuota,
                registrationCount = lecture.registrationCount,
                remark = lecture.remark,
                language = lecture.language,
                status = lecture.status,
                classSessions = sessions,
            )
        }
    }
}
