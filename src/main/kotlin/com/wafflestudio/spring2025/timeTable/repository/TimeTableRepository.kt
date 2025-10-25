package com.wafflestudio.spring2025.timeTable.repository

import com.wafflestudio.spring2025.timeTable.model.TimeTable
import org.springframework.data.repository.CrudRepository

interface TimeTableRepository : CrudRepository<TimeTable, Long> {
    fun findAllByUserId(userId: Long): List<TimeTable>
}
