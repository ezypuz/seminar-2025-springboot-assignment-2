package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.batch.dto.LectureImportResult
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
class SnuBatchIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
    ) {
        // @Disabled("실제 외부 API 호출은 수동 테스트 또는 별도의 통합 테스트에서 수행")
        @Test
        fun `should fetch and save lectures from SNU site`() {
            // given: 임포트할 학년도와 학기 설정
            val year = "2025"
            val semester = Semester.WINTER // WINTER 학기

            // when: 관리자용 배치 API 호출
            val result =
                mvc
                    .perform(
                        post("/admin/batch/import-lectures")
                            .param("year", year)
                            .param("semester", semester.name),
                    ).andExpect(status().isOk) // then: API 호출 성공 (200 OK)
                    .andReturn()
                    .response
                    .getContentAsString(Charsets.UTF_8)
                    .let { mapper.readValue(it, LectureImportResult::class.java) as LectureImportResult }

            // then: 임포트 결과 검증
            assertTrue(result.successCount > 0, "최소 1개 이상의 강의가 임포트되어야 함")
            println("Import Result: totalCount=${result.totalCount}, successCount=${result.successCount}, failCount=${result.failCount}")
        }

        // @Disabled("실제 외부 API 호출은 수동 테스트 또는 별도의 통합 테스트에서 수행")
        @Test
        fun `should search imported lectures by keyword`() {
            // given: 테스트용 데이터 임포트 (테스트의 독립성 보장)
            val year = "2025"
            val semester = Semester.WINTER
            mvc
                .perform(
                    post("/admin/batch/import-lectures")
                        .param("year", year)
                        .param("semester", semester.name),
                ).andExpect(status().isOk) // 임포트가 성공했는지 먼저 확인

            // -----------------------------------------------------------------
            // (A) 키워드 없이 검색 (전체 목록 조회)
            // -----------------------------------------------------------------
            // when: 키워드 없이 검색 API 호출
            mvc
                .perform(
                    get("/api/v1/lectures/search")
                        .param("year", year)
                        .param("semester", semester.name)
                        .param("keyword", "") // 빈 키워드
                        .param("page", "0")
                        .param("size", "10"),
                )
                // then: 검색 결과가 1개 이상인지 확인
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content.length()").value(Matchers.greaterThan(0)))

            // -----------------------------------------------------------------
            // (B) "대학" 키워드로 검색 (특정 키워드 조회)
            // -----------------------------------------------------------------
            // when: "대학" 키워드로 검색 API 호출
            mvc
                .perform(
                    get("/api/v1/lectures/search")
                        .param("year", year)
                        .param("semester", semester.name)
                        .param("keyword", "대학") // "대학" 키워드
                        .param("page", "0")
                        .param("size", "10"),
                )
                // then: 검색 결과 검증
                .andExpect(status().isOk)
                // 페이징과 관계없이 "대학"을 포함하는 총 강의 수가 13개인지 확인
                .andExpect(jsonPath("$.totalElements").value(13))
                // 현재 페이지(0번)에는 10개가 조회되었는지 확인
                .andExpect(jsonPath("$.content.length()").value(10))
        }
    }
