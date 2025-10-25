package com.wafflestudio.spring2025.lecture.service

import com.wafflestudio.spring2025.lecture.dto.LectureSearchResponse
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LectureService(
    private val lectureRepository: LectureRepository,
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
    ): Page<LectureSearchResponse> {
        val lectures =
            lectureRepository.findByYearAndSemesterAndKeyword(
                year = year,
                semester = semester,
                keyword = keyword,
                pageable = pageable,
            )

        return lectures.map { lecture ->
            LectureSearchResponse(
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
                quota = lecture.quota,
                registrationCount = lecture.registrationCount,
                remark = lecture.remark,
                language = lecture.language,
                status = lecture.status,
            )
        }
    }
}
