package com.wafflestudio.spring2025.timeTable.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("timetable")
data class TimeTable(
    @Id
    var id: Long? = null,

    var name: String,

    @Column("academic_year")  // ← 'year' 대신 안전한 이름
    var year: Int,

    var semester: Semester,

    @Column("user_id")        // ← 컬럼명을 명시적으로 매핑
    var userId: Long,
)
