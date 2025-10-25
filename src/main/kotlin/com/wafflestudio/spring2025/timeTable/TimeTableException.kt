package com.wafflestudio.spring2025.timeTable

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class TimeTableException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class TimeTableNameBlankException :
    TimeTableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "TimeTable name cannot be blank",
    )

class TimeTableNotFoundException :
    TimeTableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "TimeTable not found",
    )

class TimeTableUpdateForbiddenException :
    TimeTableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You don't have permission to update this TimeTable",
    )

class TimeTableReadForbiddenException :
    TimeTableException(
        errorCode = 0, // (에러 코드 정책에 맞게 수정)
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You don't have permission to read this TimeTable",
    )

class TimeTableModifyForbiddenException :
    TimeTableException(
        errorCode = 0, // 에러 코드 정책에 맞게 수정
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You don't have permission to modify this TimeTable",
    )

class LectureNotInTimeTableException :
    TimeTableException(
        errorCode = 0, // 에러 코드 정책에 맞게 수정
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "The lecture is not included in this TimeTable",
    )
