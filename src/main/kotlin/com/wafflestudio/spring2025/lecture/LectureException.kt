package com.wafflestudio.spring2025.lecture

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus // ✅ 이게 더 깔끔

sealed class LectureException(
    errorCode: Int,
    httpStatusCode: HttpStatus, // ✅ HttpStatusCode 대신 HttpStatus
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class LectureNotFoundException :
    LectureException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND, // ✅ 가독성 좋음
        msg = "Lecture not found",
    )

class LectureAlreadyInTimeTableException :
    LectureException(
        errorCode = 1,
        httpStatusCode = HttpStatus.CONFLICT, // ✅ 409
        msg = "The lecture is already in the timetable",
    )
