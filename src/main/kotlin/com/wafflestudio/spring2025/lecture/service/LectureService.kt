package com.wafflestudio.spring2025.lecture.service

import com.wafflestudio.spring2025.lecture.dto.core.ClassSessionDto
import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LectureService(
    private val lectureRepository: LectureRepository,
) {
    fun searchLectures(
        year: String,
        semester: Semester,
        keyword: String,
        pageable: Pageable,
    ): Page<LectureDto> {
        // 1. ✅ 강의 ID만 조회 (페이지네이션 적용)
        val lectureIds = lectureRepository.findLectureIds(
            year = year,
            semester = semester,
            keyword = keyword,
            limit = pageable.pageSize,
            offset = pageable.offset.toInt(),
        )

        // 강의가 없으면 빈 페이지 반환
        if (lectureIds.isEmpty()) {
            val total = lectureRepository.countLectures(year, semester, keyword)
            return PageImpl(emptyList(), pageable, total)
        }

        // 2. ✅ 해당 강의들의 상세 정보 + 세션 조회
        val rows = lectureRepository.findLectureDetailsByIds(lectureIds)

        // 3. ✅ 전체 개수 조회
        val total = lectureRepository.countLectures(year, semester, keyword)

        // 4. ✅ lectureId로 그룹핑하여 LectureDto 생성
        val lectureDtos =
            rows
                .groupBy { it.lectureId }
                .map { (_, lectureRows) ->
                    val first = lectureRows.first()

                    val sessions =
                        lectureRows
                            .filter { it.sessionId != null }
                            .map { row ->
                                ClassSessionDto(
                                    dayOfWeek = row.dayOfWeek,
                                    startTime = row.startTime,
                                    endTime = row.endTime,
                                    location = row.location,
                                    courseFormat = row.courseFormat,
                                )
                            }

                    LectureDto(
                        id = first.lectureId,
                        year = first.year,
                        semester = first.semester,
                        division = first.division,
                        college = first.college,
                        department = first.department,
                        courseType = first.courseType,
                        grade = first.grade,
                        courseNumber = first.courseNumber,
                        lectureNumber = first.lectureNumber,
                        courseTitle = first.courseTitle,
                        subtitle = first.subtitle,
                        credits = first.credits,
                        classTime = first.classTime,
                        labTime = first.labTime,
                        professor = first.professor,
                        preRegistrationCount = first.preRegistrationCount,
                        preRegistrationCountForNonFreshman = first.preRegistrationCountForNonFreshman,
                        preRegistrationCountForFreshman = first.preRegistrationCountForFreshman,
                        quota = first.quota,
                        nonfreshmanQuota = first.nonfreshmanQuota,
                        registrationCount = first.registrationCount,
                        remark = first.remark,
                        language = first.language,
                        status = first.status,
                        classSessions = sessions,
                    )
                }

        return PageImpl(lectureDtos, pageable, total)
    }
}
