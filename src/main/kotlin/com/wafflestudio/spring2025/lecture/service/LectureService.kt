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
    /**
     * 강의 검색 (페이지네이션)
     * - 단 한 번의 쿼리로 강의 + 세션 + 전체 개수 조회
     */
    fun searchLectures(
        year: String,
        semester: Semester,
        keyword: String,
        pageable: Pageable,
    ): Page<LectureDto> {
        // 1. 한 번의 쿼리로 모든 데이터 조회 (강의 + 세션 + 전체 개수)
        val rows =
            lectureRepository.findLectureDetailsWithSessions(
                year = year,
                semester = semester,
                keyword = keyword,
                limit = pageable.pageSize,
                offset = pageable.offset.toInt(),
            )

        // 2. 전체 개수 추출 (첫 번째 row에서 가져오기)
        val total = rows.firstOrNull()?.totalCount ?: 0L

        // 3. lectureId로 그룹핑하여 LectureDto 생성
        val lectureDtos =
            rows
                .groupBy { it.lectureId }
                .map { (_, lectureRows) ->
                    val first = lectureRows.first()

                    // 세션 정보 수집 (sessionId가 null이 아닌 것만)
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

        // 4. Page 객체로 변환
        return PageImpl(lectureDtos, pageable, total)
    }
}
