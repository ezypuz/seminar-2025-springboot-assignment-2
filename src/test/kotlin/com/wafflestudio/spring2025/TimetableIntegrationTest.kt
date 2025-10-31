package com.wafflestudio.spring2025

import org.junit.jupiter.api.Assertions.assertTrue
import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.batch.dto.LectureImportResult
import com.wafflestudio.spring2025.helper.DataGenerator
import com.wafflestudio.spring2025.timeTable.dto.AddLectureRequest
import com.wafflestudio.spring2025.timeTable.dto.CreateTimeTableRequest
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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
        val (user, token) = dataGenerator.generateUser()

        val request =
            CreateTimeTableRequest(
                name = "2025년 가을학기 시간표",
                year = 2025,
                semester = Semester.AUTUMN,
            )

        mvc
            .perform(
                post("/api/v1/timetables")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value(request.name))
            .andExpect(jsonPath("$.year").value(request.year))
            .andExpect(jsonPath("$.semester").value(request.semester.name))
            .andExpect(jsonPath("$.user.id").value(user.id!!))
    }

    @Test
    fun `should not create a time table with a blank name`() {
        val (_, token) = dataGenerator.generateUser()

        val request =
            CreateTimeTableRequest(
                name = " ",
                year = 2025,
                semester = Semester.AUTUMN,
            )

        mvc
            .perform(
                post("/api/v1/timetables")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should retrieve all own timetables`() {
        val (user, token) = dataGenerator.generateUser()

        val timetableNames = listOf("시간표 1", "시간표 2", "시간표 3")
        timetableNames.forEach { name ->
            val request =
                CreateTimeTableRequest(
                    name = name,
                    year = 2025,
                    semester = Semester.AUTUMN,
                )
            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
        }

        mvc
            .perform(
                get("/api/v1/timetables")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(timetableNames.size))
            .andExpect(jsonPath("$[0].user.id").value(user.id!!))
    }

    @Test
    fun `should retrieve timetable details`() {
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user)

        // 강의 2개 추가
        val lecture1 = dataGenerator.generateLecture(
            courseTitle = "알고리즘",
            credits = 3.0,
            sessions = listOf(
                DataGenerator.SessionInfo(dayOfWeek = 0, startTime = 600, endTime = 750), // 월 10:00-12:30
            ),
        )
        val lecture2 = dataGenerator.generateLecture(
            courseTitle = "데이터베이스",
            credits = 3.0,
            sessions = listOf(
                DataGenerator.SessionInfo(dayOfWeek = 2, startTime = 600, endTime = 750), // 수 10:00-12:30
            ),
        )

        // 시간표에 강의 추가
        mvc.perform(
            post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(AddLectureRequest(lecture1.id!!))),
        ).andExpect(status().isOk)

        mvc.perform(
            post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(AddLectureRequest(lecture2.id!!))),
        ).andExpect(status().isOk)

        // 시간표 상세 조회
        mvc
            .perform(
                get("/api/v1/timetables/{timetableId}", timetable.id)
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(timetable.id!!))
            .andExpect(jsonPath("$.name").value(timetable.name))
            .andExpect(jsonPath("$.totalCredits").value(6.0))
            .andExpect(jsonPath("$.lectures.length()").value(2))
    }

    @Test
    fun `should update timetable name`() {
        val (user, token) = dataGenerator.generateUser()

        val createRequest =
            CreateTimeTableRequest(
                name = "2025년 가을학기 시간표",
                year = 2025,
                semester = Semester.AUTUMN,
            )

        val timetableId =
            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createRequest)),
                ).andExpect(status().isOk)
                .andReturn()
                .response
                .getContentAsString(Charsets.UTF_8)
                .let { mapper.readTree(it).get("id").asLong() }

        val updateRequest = mapOf("name" to "수정된 시간표 이름")

        mvc
            .perform(
                patch("/api/v1/timetables/$timetableId")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(updateRequest)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(timetableId))
            .andExpect(jsonPath("$.name").value(updateRequest["name"]))
            .andExpect(jsonPath("$.year").value(createRequest.year))
            .andExpect(jsonPath("$.semester").value(createRequest.semester.name))
            .andExpect(jsonPath("$.user.id").value(user.id!!))
    }

    @Test
    fun `should not update a time table with a blank name`() {
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user)

        val updateRequest = mapOf("name" to " ")

        mvc
            .perform(
                patch("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(updateRequest)),
            ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should not update another user's timetable`() {
        val (owner, ownerToken) = dataGenerator.generateUser()
        val (attacker, attackerToken) = dataGenerator.generateUser()

        val timetable = dataGenerator.generateTimetable(user = owner)
        val updateRequest = mapOf("name" to "해킹 시도")

        mvc
            .perform(
                patch("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $attackerToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(updateRequest)),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `should delete a timetable`() {
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user)

        mvc
            .perform(
                delete("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isNoContent)

        mvc
            .perform(
                get("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `should not delete another user's timetable`() {
        val (owner, ownerToken) = dataGenerator.generateUser()
        val (attacker, attackerToken) = dataGenerator.generateUser()

        val timetable = dataGenerator.generateTimetable(user = owner)

        mvc
            .perform(
                delete("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $attackerToken"),
            ).andExpect(status().isForbidden)

        mvc
            .perform(
                get("/api/v1/timetables/${timetable.id}")
                    .header("Authorization", "Bearer $ownerToken"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(timetable.id!!))
    }

    @Test
    fun `should search for courses`() {
        // 강의 검색 테스트
        val year = "2025"
        val semester = Semester.AUTUMN

        val uniquePrefix = "SEARCH_TEST_${System.currentTimeMillis()}_"

        // 검색용 강의 생성 (키워드: "알고리즘")
        dataGenerator.generateLecture(
            courseTitle = "${uniquePrefix}알고리즘",
            year = year,
            semester = semester,
            professor = "홍길동",
        )
        dataGenerator.generateLecture(
            courseTitle = "${uniquePrefix}자료구조",
            year = year,
            semester = semester,
            professor = "김철수",
        )
        dataGenerator.generateLecture(
            courseTitle = "${uniquePrefix}알고리즘 응용",
            year = year,
            semester = semester,
            professor = "이영희",
        )

        val (_, token) = dataGenerator.generateUser()

        // 강의명으로 검색 ("알고리즘")
        mvc
            .perform(
                get("/api/v1/lectures/search")
                    .header("Authorization", "Bearer $token")
                    .param("year", year)
                    .param("semester", semester.name)
                    .param("keyword", "${uniquePrefix}알고리즘")
                    .param("page", "0")
                    .param("size", "10"),
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(2))  // "알고리즘", "알고리즘 응용"
            .andExpect(jsonPath("$.content[0].courseTitle").value(org.hamcrest.Matchers.containsString("알고리즘")))

        // 교수명으로 검색 ("홍길동")
        mvc
            .perform(
                get("/api/v1/lectures/search")
                    .header("Authorization", "Bearer $token")
                    .param("year", year)
                    .param("semester", semester.name)
                    .param("keyword", "홍길동")
                    .param("page", "0")
                    .param("size", "10"),
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].professor").value("홍길동"))
    }

    @Test
    fun `should add a course to timetable`() {
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user)
        val lecture =
            dataGenerator.generateLecture(
                courseTitle = "알고리즘",
                sessions =
                    listOf(
                        DataGenerator.SessionInfo(dayOfWeek = 0, startTime = 600, endTime = 750),
                    ),
            )

        mvc
            .perform(
                post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture.id!!))),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(1))
            .andExpect(jsonPath("$.lectures[0].id").value(lecture.id!!))
            .andExpect(jsonPath("$.totalCredits").value(3.0))
    }

    @Test
    fun `should return error when adding overlapping course to timetable`() {
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user)

        // 월요일 10:00-12:30 강의
        val lecture1 =
            dataGenerator.generateLecture(
                courseTitle = "알고리즘",
                sessions =
                    listOf(
                        DataGenerator.SessionInfo(dayOfWeek = 0, startTime = 600, endTime = 750),
                    ),
            )

        // 월요일 11:00-13:00 강의 (겹침!)
        val lecture2 =
            dataGenerator.generateLecture(
                courseTitle = "데이터베이스",
                sessions =
                    listOf(
                        DataGenerator.SessionInfo(dayOfWeek = 0, startTime = 660, endTime = 780),
                    ),
            )

        // 첫 번째 강의 추가
        mvc
            .perform(
                post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture1.id!!))),
            ).andExpect(status().isOk)

        // 두 번째 강의 추가 시도 (시간 겹침으로 실패)
        mvc
            .perform(
                post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture2.id!!))),
            ).andExpect(status().isConflict)
    }

    @Test
    fun `should not add a course to another user's timetable`() {
        val (owner, ownerToken) = dataGenerator.generateUser()
        val (attacker, attackerToken) = dataGenerator.generateUser()

        val timetable = dataGenerator.generateTimetable(user = owner)
        val lecture = dataGenerator.generateLecture()

        mvc
            .perform(
                post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                    .header("Authorization", "Bearer $attackerToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture.id!!))),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `should remove a course from timetable`() {
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user)
        val lecture = dataGenerator.generateLecture()

        // 강의 추가
        mvc
            .perform(
                post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture.id!!))),
            ).andExpect(status().isOk)

        // 강의 삭제
        mvc
            .perform(
                delete("/api/v1/timetables/{timetableId}/lectures/{lectureId}", timetable.id, lecture.id)
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isNoContent)

        // 삭제 확인
        mvc
            .perform(
                get("/api/v1/timetables/{timetableId}", timetable.id)
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(0))
    }

    @Test
    fun `should not remove a course from another user's timetable`() {
        val (owner, ownerToken) = dataGenerator.generateUser()
        val (attacker, attackerToken) = dataGenerator.generateUser()

        val timetable = dataGenerator.generateTimetable(user = owner)
        val lecture = dataGenerator.generateLecture()

        // 소유자가 강의 추가
        mvc
            .perform(
                post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                    .header("Authorization", "Bearer $ownerToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture.id!!))),
            ).andExpect(status().isOk)

        // 공격자가 강의 삭제 시도
        mvc
            .perform(
                delete("/api/v1/timetables/{timetableId}/lectures/{lectureId}", timetable.id, lecture.id)
                    .header("Authorization", "Bearer $attackerToken"),
            ).andExpect(status().isForbidden)
    }

    @Test
    @Disabled("실제 외부 API 호출은 수동 테스트 또는 별도의 통합 테스트에서 수행")
    fun `should fetch and save course information from SNU course registration site`() {
        // 이 테스트는 실제 서울대 수강신청 사이트에 요청을 보내므로
        // 자동화된 테스트에서는 비활성화하고, 필요시 수동으로 실행

        // 관리자 권한이 필요한 경우 별도 처리
        val year = "2025"
        val semester = Semester.AUTUMN

        // POST /admin/batch/import-lectures?year=2025&semester=AUTUMN
        val result = mvc
            .perform(
                post("/admin/batch/import-lectures")
                    .param("year", year)
                    .param("semester", semester.name),
            )
            .andExpect(status().isOk)
            .andReturn()
            .response
            .getContentAsString(Charsets.UTF_8)
            .let { mapper.readValue(it, LectureImportResult::class.java) as LectureImportResult }

        // 결과 확인
        assertTrue(result.successCount > 0, "최소 1개 이상의 강의가 임포트되어야 함")
        println("Import Result: totalCount=${result.totalCount}, successCount=${result.successCount}, failCount=${result.failCount}")

        // DB에 저장되었는지 확인
        mvc
            .perform(
                get("/api/v1/lectures/search")
                    .param("year", year)
                    .param("semester", semester.name)
                    .param("keyword", "")
                    .param("page", "0")
                    .param("size", "10"),
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(org.hamcrest.Matchers.greaterThan(0)))
    }

    @Test
    fun `should return correct course list and total credits when retrieving timetable details`() {
        val (user, token) = dataGenerator.generateUser()
        val timetable = dataGenerator.generateTimetable(user = user)

        val lecture1 = dataGenerator.generateLecture(courseTitle = "알고리즘", credits = 3.0)
        val lecture2 = dataGenerator.generateLecture(courseTitle = "데이터베이스", credits = 3.0)
        val lecture3 = dataGenerator.generateLecture(courseTitle = "운영체제", credits = 3.0)

        listOf(lecture1, lecture2, lecture3).forEach { lecture ->
            mvc.perform(
                post("/api/v1/timetables/{timetableId}/lectures", timetable.id)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AddLectureRequest(lecture.id!!))),
            ).andExpect(status().isOk)
        }

        mvc
            .perform(
                get("/api/v1/timetables/{timetableId}", timetable.id)
                    .header("Authorization", "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.lectures.length()").value(3))
            .andExpect(jsonPath("$.totalCredits").value(9.0))
    }

    @Test
    fun `should paginate correctly when searching for courses`() {
        // 강의 검색 페이지네이션 테스트
        val year = "2025"
        val semester = Semester.AUTUMN

        // 25개 강의 생성
        repeat(25) { index ->
            dataGenerator.generateLecture(
                courseTitle = "컴퓨터공학 $index",
                year = year,
                semester = semester,
            )
        }

        // 첫 번째 페이지 (size=10)
        mvc
            .perform(
                get("/api/v1/lectures/search")
                    .param("year", year)
                    .param("semester", semester.name)
                    .param("keyword", "컴퓨터공학")
                    .param("page", "0")
                    .param("size", "10"),
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(10))
            .andExpect(jsonPath("$.totalElements").value(25))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.number").value(0))  // 현재 페이지 번호

        // 두 번째 페이지
        mvc
            .perform(
                get("/api/v1/lectures/search")
                    .param("year", year)
                    .param("semester", semester.name)
                    .param("keyword", "컴퓨터공학")
                    .param("page", "1")
                    .param("size", "10"),
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(10))
            .andExpect(jsonPath("$.number").value(1))

        // 마지막 페이지 (5개만 있음)
        mvc
            .perform(
                get("/api/v1/lectures/search")
                    .param("year", year)
                    .param("semester", semester.name)
                    .param("keyword", "컴퓨터공학")
                    .param("page", "2")
                    .param("size", "10"),
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(5))
            .andExpect(jsonPath("$.number").value(2))
    }
}