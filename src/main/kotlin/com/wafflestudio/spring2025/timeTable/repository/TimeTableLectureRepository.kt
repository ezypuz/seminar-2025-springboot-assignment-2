package com.wafflestudio.spring2025.timeTable.repository

import com.wafflestudio.spring2025.timeTable.model.TimeTableLecture
import org.springframework.data.repository.CrudRepository

interface TimeTableLectureRepository : CrudRepository<TimeTableLecture, Long> {
    fun existsByTimeTableIdAndLectureId(
        timeTableId: Long,
        lectureId: Long,
    ): Boolean

    fun deleteByTimeTableIdAndLectureId(
        timeTableId: Long,
        lectureId: Long,
    )
}
