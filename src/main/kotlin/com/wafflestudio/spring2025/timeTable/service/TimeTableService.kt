package com.wafflestudio.spring2025.timeTable.service

import com.wafflestudio.spring2025.lecture.LectureAlreadyInTimeTableException
import com.wafflestudio.spring2025.lecture.LectureNotFoundException
import com.wafflestudio.spring2025.lecture.dto.core.ClassSessionDto
import com.wafflestudio.spring2025.lecture.dto.core.LectureDto
import com.wafflestudio.spring2025.lecture.model.ClassSession
import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.lecture.repository.ClassSessionRepository
import com.wafflestudio.spring2025.lecture.repository.LectureDetailRow
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.timeTable.LectureNotInTimeTableException
import com.wafflestudio.spring2025.timeTable.TimeConflictException
import com.wafflestudio.spring2025.timeTable.TimeTableModifyForbiddenException
import com.wafflestudio.spring2025.timeTable.TimeTableNameBlankException
import com.wafflestudio.spring2025.timeTable.TimeTableNotFoundException
import com.wafflestudio.spring2025.timeTable.TimeTableReadForbiddenException
import com.wafflestudio.spring2025.timeTable.TimeTableUpdateForbiddenException
import com.wafflestudio.spring2025.timeTable.dto.TimeTableDetailResponse
import com.wafflestudio.spring2025.timeTable.dto.UpdateTimeTableNameResponse
import com.wafflestudio.spring2025.timeTable.dto.core.TimeTableDto
import com.wafflestudio.spring2025.timeTable.model.Semester
import com.wafflestudio.spring2025.timeTable.model.TimeTable
import com.wafflestudio.spring2025.timeTable.model.TimeTableLecture
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
    private val classSessionRepository: ClassSessionRepository,
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
    ): UpdateTimeTableNameResponse {
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
            throw TimeTableModifyForbiddenException()
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
                                ClassSessionDto(
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
                    LectureDto(
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
        val timeTable = validateTimeTableExists(timeTableId)
        ensureUserCanModifyTimetable(timeTable, userId)

        // 2. 해당 시간표에 강의가 포함되어 있는지 확인
        if (!timeTableLectureRepository.existsByTimeTableIdAndLectureId(timeTableId, lectureId)) {
            throw LectureNotInTimeTableException()
        }

        // 3. 시간표-강의 연결 삭제
        timeTableLectureRepository.deleteByTimeTableIdAndLectureId(timeTableId, lectureId)
    }

    /**
     * 시간표에 강의 추가
     * - 강의 존재 여부 확인
     * - 시간표 소유권 확인
     * - 시간 중복 검증
     * - 중복 추가 방지
     */
    @Transactional
    fun addLecture(
        user: User,
        timeTableId: Long,
        lectureId: Long,
    ): TimeTableDetailResponse {
        // 1. 시간표 조회 및 소유권 확인
        val timeTable = validateTimeTableExists(timeTableId)
        ensureUserCanModifyTimetable(timeTable, user.id!!)

        // 2. 강의 존재 여부 확인
        validateLectureExists(lectureId)

        // 3. 이미 추가된 강의인지 확인
        if (timeTableLectureRepository.existsByTimeTableIdAndLectureId(timeTableId, lectureId)) {
            throw LectureAlreadyInTimeTableException()
        }

        // 4. 추가하려는 강의의 세션 정보 조회
        val newSessions = classSessionRepository.findByLectureId(lectureId)

        // 5. 시간표의 기존 강의들과 시간 중복 검증
        val existingRows = lectureRepository.findLectureDetailsByTimeTableId(timeTableId)
        validateTimeConflict(newSessions, existingRows)

        // 6. 시간표에 강의 추가
        timeTableLectureRepository.save(
            TimeTableLecture(
                timeTableId = timeTableId,
                lectureId = lectureId,
            ),
        )

        // 7. 업데이트된 시간표 반환
        return getTimeTableDetail(timeTableId, user)
    }

    private fun validateTimeTableExists(timeTableId: Long): TimeTable =
        timeTableRepository
            .findById(timeTableId)
            .orElseThrow { TimeTableNotFoundException() }

    private fun ensureUserCanModifyTimetable(
        timeTable: TimeTable,
        userId: Long,
    ) {
        if (timeTable.userId != userId) {
            throw TimeTableModifyForbiddenException()
        }
    }

    private fun validateLectureExists(lectureId: Long): Lecture =
        lectureRepository
            .findById(lectureId)
            .orElseThrow { LectureNotFoundException() }

    /**
     * 시간 중복 검증
     * - 새로 추가하려는 강의의 세션들이 기존 강의들과 시간이 겹치는지 확인
     */
    private fun validateTimeConflict(
        newSessions: List<ClassSession>,
        existingRows: List<LectureDetailRow>,
    ) {
        // 기존 강의들의 세션 정보 추출
        val existingSessions =
            existingRows
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

        // 새 세션과 기존 세션들 간 시간 중복 체크
        for (newSession in newSessions) {
            for (existingSession in existingSessions) {
                if (isTimeConflict(newSession, existingSession)) {
                    throw TimeConflictException()
                }
            }
        }
    }

    /**
     * 두 세션이 시간이 겹치는지 확인
     * - 같은 요일이고, 시간이 겹치면 true 반환
     */
    private fun isTimeConflict(
        session1: ClassSession,
        session2: ClassSessionDto,
    ): Boolean {
        // 요일이 다르면 겹치지 않음
        if (session1.dayOfWeek != session2.dayOfWeek) return false

        // 둘 중 하나라도 시간 정보가 없으면 겹치지 않는 것으로 간주
        val start1 = session1.startTime ?: return false
        val end1 = session1.endTime ?: return false
        val start2 = session2.startTime ?: return false
        val end2 = session2.endTime ?: return false

        // 시간 겹침 체크: [start1, end1)과 [start2, end2)가 겹치는가?
        // 겹치지 않는 조건: end1 <= start2 OR end2 <= start1
        // 겹치는 조건: NOT (겹치지 않는 조건)
        return !(end1 <= start2 || end2 <= start1)
    }
}
