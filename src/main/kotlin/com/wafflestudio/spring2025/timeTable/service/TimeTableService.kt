package com.wafflestudio.spring2025.timeTable.service

import com.wafflestudio.spring2025.comment.CommentUpdateForbiddenException
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.timeTable.LectureNotInTimeTableException
import com.wafflestudio.spring2025.timeTable.TimeTableModifyForbiddenException
import com.wafflestudio.spring2025.timeTable.TimeTableNameBlankException
import com.wafflestudio.spring2025.timeTable.TimeTableNotFoundException
import com.wafflestudio.spring2025.timeTable.TimeTableReadForbiddenException
import com.wafflestudio.spring2025.timeTable.TimeTableUpdateForbiddenException
import com.wafflestudio.spring2025.timeTable.dto.ClassSessionResponse
import com.wafflestudio.spring2025.timeTable.dto.LectureResponse
import com.wafflestudio.spring2025.timeTable.dto.TimeTableDetailResponse
import com.wafflestudio.spring2025.timeTable.dto.UpdateTimeTableResponse
import com.wafflestudio.spring2025.timeTable.dto.core.TimeTableDto
import com.wafflestudio.spring2025.timeTable.model.Semester
import com.wafflestudio.spring2025.timeTable.model.TimeTable
import com.wafflestudio.spring2025.timeTable.repository.TimeTableLectureRepository
import com.wafflestudio.spring2025.timeTable.repository.TimeTableRepository
import com.wafflestudio.spring2025.user.model.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimeTableService(
    private val timeTableRepository: TimeTableRepository,
    private val lectureRepository: LectureRepository,
    private val timeTableLectureRepository: TimeTableLectureRepository,
) {
    fun createTimeTable(
        name: String,
        year: Int,
        semester: Semester,
        user: User,
    ): TimeTableDto {
        if (name.isBlank()) {
            throw TimeTableNameBlankException()
        }

        val timeTable =
            timeTableRepository.save(
                TimeTable(
                    name = name,
                    year = year,
                    semester = semester,
                    userId = user.id!!,
                ),
            )
        return TimeTableDto(timeTable, user)
    }

    @Transactional(readOnly = true)
    fun getTimeTables(user: User): List<TimeTableDto> {
        // TimeTableRepository를 사용해 userId와 일치하는 모든 TimeTable 엔티티를 조회
        val timeTables = timeTableRepository.findAllByUserId(userId = user.id!!)

        return timeTables.map { timeTable -> TimeTableDto(timeTable, user) }
    }

    fun updateTimeTableName(
        timeTableId: Long,
        user: User,
        name: String,
    ): UpdateTimeTableResponse {
        if (name.isBlank()) {
            throw TimeTableNameBlankException()
        }

        val timeTable =
            timeTableRepository.findByIdOrNull(timeTableId)
                ?: throw TimeTableNotFoundException()

        if (timeTable.userId != user.id) {
            throw TimeTableUpdateForbiddenException()
        }

        name.let { timeTable.name = it }
        val updatedTimeTable = timeTableRepository.save(timeTable)

        return TimeTableDto(updatedTimeTable, user)
    }

    fun deleteTimeTable(
        timeTableId: Long,
        user: User,
    ) {
        val timeTable =
            timeTableRepository
                .findById(timeTableId)
                .orElseThrow {
                    throw TimeTableNotFoundException()
                }

        if (timeTable.userId != user.id) {
            throw CommentUpdateForbiddenException()
        }

        timeTableRepository.delete(timeTable)

        // 강의 추가 내역 삭제 로직은 데이터베이스 설계로 자동 구현 (TimeTableLecture
    }

    fun getTimeTableDetail(
        timeTableId: Long,
        user: User,
    ): TimeTableDetailResponse {
        // 1. 시간표 조회 및 소유권 확인

        // ID로 '먼저' 찾는다 (존재 여부 확인)
        val timeTable =
            timeTableRepository
                .findById(timeTableId)
                .orElseThrow { TimeTableNotFoundException() } // (없으면 404)

        // 소유권을 '나중에' 확인한다
        if (timeTable.userId != user.id) {
            throw TimeTableReadForbiddenException() // (내 것이 아니면 403)
        }

        // 2. Repository의 커스텀 쿼리로 데이터 가져옴
        val flatRows = lectureRepository.findLectureDetailsByTimeTableId(timeTableId)

        var totalCredits = 0.0

        // 3. '중첩된' DTO 구조로 재조립
        val lectureResponses =
            flatRows
                .groupBy { it.lectureId }
                .map { (lectureId, rows) ->

                    val firstRow = rows.first()
                    totalCredits += firstRow.credits ?: 0.0

                    // 3-2. 세션 목록 생성
                    val sessions =
                        rows
                            .map { row ->
                                ClassSessionResponse(
                                    // sessionId = row.sessionId,
                                    // 필요 없는 정보 제외
                                    dayOfWeek = row.dayOfWeek,
                                    startTime = row.startTime,
                                    endTime = row.endTime,
                                    location = row.location,
                                    courseFormat = row.courseFormat,
                                )
                            }.distinct()

                    // 3-3. LectureResponse DTO 생성 (모든 필드 매핑)
                    LectureResponse(
                        id = lectureId,
                        // 기본 정보
                        year = firstRow.year,
                        semester = firstRow.semester,
                        division = firstRow.division,
                        college = firstRow.college,
                        department = firstRow.department,
                        courseType = firstRow.courseType,
                        grade = firstRow.grade,
                        courseNumber = firstRow.courseNumber,
                        lectureNumber = firstRow.lectureNumber,
                        courseTitle = firstRow.courseTitle,
                        subtitle = firstRow.subtitle,
                        credits = firstRow.credits,
                        classTime = firstRow.classTime,
                        labTime = firstRow.labTime,
                        professor = firstRow.professor,
                        // 수강신청 관련 정보
                        preRegistrationCount = firstRow.preRegistrationCount,
                        preRegistrationCountForNonFreshman = firstRow.preRegistrationCountForNonFreshman,
                        preRegistrationCountForFreshman = firstRow.preRegistrationCountForFreshman,
                        quota = firstRow.quota,
                        nonfreshmanQuota = firstRow.nonfreshmanQuota,
                        registrationCount = firstRow.registrationCount,
                        // 기타 정보
                        remark = firstRow.remark,
                        language = firstRow.language,
                        status = firstRow.status,
                        // 세션 목록
                        classSessions = sessions,
                    )
                }

        // 4. 최종 응답 DTO 반환
        return TimeTableDetailResponse(
            id = timeTable.id!!,
            name = timeTable.name,
            year = timeTable.year,
            semester = timeTable.semester,
            totalCredits = totalCredits, // 계산된 총 학점
            lectures = lectureResponses, // 변환된 강의 목록
        )
    }

    /**
     * 시간표에서 강의 삭제
     *
     * @param userId 사용자 ID
     * @param timeTableId 시간표 ID
     * @param lectureId 삭제할 강의 ID
     * @throws TimeTableNotFoundException 시간표가 존재하지 않는 경우
     * @throws TimeTableModifyForbiddenException 시간표의 소유자가 아닌 경우
     * @throws LectureNotInTimeTableException 강의가 시간표에 포함되어 있지 않은 경우
     */
    fun removeLecture(
        userId: Long,
        timeTableId: Long,
        lectureId: Long,
    ) {
        // 1. 시간표 조회 및 소유권 확인
        val timeTable =
            timeTableRepository
                .findById(timeTableId)
                .orElseThrow { TimeTableNotFoundException() }

        if (timeTable.userId != userId) {
            throw TimeTableModifyForbiddenException()
        }

        // 2. 해당 시간표에 강의가 포함되어 있는지 확인
        if (!timeTableLectureRepository.existsByTimeTableIdAndLectureId(timeTableId, lectureId)) {
            throw LectureNotInTimeTableException()
        }

        // 3. 시간표-강의 연결 삭제
        timeTableLectureRepository.deleteByTimeTableIdAndLectureId(timeTableId, lectureId)
    }
}
