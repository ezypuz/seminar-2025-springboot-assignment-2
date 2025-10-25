package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.helper.DataGenerator
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
class TimetableIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val dataGenerator: DataGenerator,
    ) {
        @Test
        fun `should create a timetable`() {
            // 시간표를 생성할 수 있다
        }

        /**
         * 추가 테스트
         */
        @Test
        fun `should not create a time table with a blank name`() {
            // 시간표를 빈 제목으로 생성할 수 없다
        }

        @Test
        fun `should retrieve all own timetables`() {
            // 자신의 모든 시간표 목록을 조회할 수 있다
        }

        @Test
        fun `should retrieve timetable details`() {
            // 시간표 상세 정보를 조회할 수 있다
        }

        @Test
        fun `should update timetable name`() {
            // 시간표 이름을 수정할 수 있다
        }

        /**
         * 추가 테스트
         */
        @Test
        fun `should not update a time table with a blank name`() {
            // 시간표는 빈 제목으로 수정할 수 없다
        }

        @Test
        fun `should not update another user's timetable`() {
            // 다른 사람의 시간표는 수정할 수 없다
        }

        @Test
        fun `should delete a timetable`() {
            // 시간표를 삭제할 수 있다
        }

        @Test
        fun `should not delete another user's timetable`() {
            // 다른 사람의 시간표는 삭제할 수 없다
        }

        @Test
        fun `should search for courses`() {
            // 강의를 검색할 수 있다
        }

        @Test
        fun `should add a course to timetable`() {
            // 시간표에 강의를 추가할 수 있다
        }

        /**
         * 추가 테스트
         * TODO 이미 추가된 강의에 대한 추가 요청이 오는 경우, conflict 에러를 반환한다
         * TODO 설강되지 않은 강의에 대한 추가 요청이 오는 경우 에러를 반환한다.
         * TODO 없는 강의를 추가할 수 없다
         * TODO 없는 강의를 삭제할 수 없다
         * TODO 없는 시간표는 조회되지 않습니다.
         * TODO 남의 시간표는 조회할 수 없다.
         * TODO 시간표 삭제 시, 추가했던 강의 내역도 삭제된다.
         */

        @Test
        fun `should return error when adding overlapping course to timetable`() {
            // 시간표에 강의 추가 시, 시간이 겹치면 에러를 반환한다 (같은 강의가 아닌 경우)
        }

        @Test
        fun `should not add a course to another user's timetable`() {
            // 다른 사람의 시간표에는 강의를 추가할 수 없다
        }

        @Test
        fun `should remove a course from timetable`() {
            // 시간표에서 강의를 삭제할 수 있다
        }

        @Test
        fun `should not remove a course from another user's timetable`() {
            // 다른 사람의 시간표에서는 강의를 삭제할 수 없다
        }

        @Test
        @Disabled("곧 안내드리겠습니다")
        fun `should fetch and save course information from SNU course registration site`() {
            // 서울대 수강신청 사이트에서 강의 정보를 가져와 저장할 수 있다
        }

        @Test
        fun `should return correct course list and total credits when retrieving timetable details`() {
            // 시간표 상세 조회 시, 강의 정보 목록과 총 학점이 올바르게 반환된다
        }

        @Test
        fun `should paginate correctly when searching for courses`() {
            // 강의 검색 시, 페이지네이션이 올바르게 동작한다
        }
    }
