package com.wafflestudio.spring2025.batch

import com.wafflestudio.spring2025.batch.repository.SugangSnuRepository
import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.timeTable.model.Semester
import org.apache.poi.hssf.usermodel.HSSFWorkbook // .xls용
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface SugangSnuFetchService {
    suspend fun fetchAndImportLectures(
        year: String,
        semester: Semester,
    ): ImportResult
}

@Service
@Transactional
class SugangSnuFetchServiceImpl(
    private val sugangSnuRepository: SugangSnuRepository,
    private val lectureRepository: LectureRepository,
) : SugangSnuFetchService {
    private val log = LoggerFactory.getLogger(javaClass)

    // 정원 파싱용 정규식: "50" 또는 "50 (45)" 형태
    private val quotaRegex = """(?<quota>\d+)(\s*\((?<quotaForNonFreshman>\d+)\))?""".toRegex()

    /**
     * 수강신청 사이트에서 강의 목록을 가져와 DB에 저장
     *
     * @param year 학년도 (예: "2025")
     * @param semester 학기
     * @return 저장 결과 통계
     */
    override suspend fun fetchAndImportLectures(
        year: String,
        semester: Semester,
    ): ImportResult {
        log.info("Starting to fetch lectures for $year-$semester")

        // 1. 엑셀 파일 다운로드
        val xlsBytes = sugangSnuRepository.downloadLectureXls(year, semester)

        // 2. 엑셀 파싱
        val workbook = HSSFWorkbook(xlsBytes.inputStream())
        val sheet = workbook.getSheetAt(0)

        // 3. 컬럼명-인덱스 매핑 (3번째 행이 헤더)
        val headerRow = sheet.getRow(2)
        val columnNameIndex =
            headerRow.associate { cell ->
                cell.stringCellValue to cell.columnIndex
            }

        // 4. 데이터 행 파싱 (4번째 행부터)
        val lectures = mutableListOf<Lecture>()
        var successCount = 0
        var failCount = 0

        for (rowIndex in 3 until sheet.physicalNumberOfRows) {
            try {
                val row = sheet.getRow(rowIndex)
                val lecture = convertRowToLecture(row, columnNameIndex, year, semester)
                lectures.add(lecture)
                successCount++
            } catch (e: Exception) {
                log.error("Failed to parse row $rowIndex", e)
                failCount++
            }
        }

        // 5. DB에 저장
        lectureRepository.saveAll(lectures)

        log.info("Import completed: success=$successCount, fail=$failCount")

        return ImportResult(
            totalCount = successCount + failCount,
            successCount = successCount,
            failCount = failCount,
        )
    }

    /**
     * 엑셀 행을 Lecture 엔티티로 변환
     *
     * 엑셀 컬럼 순서:
     * 교과구분, 개설대학, 개설학과, 이수과정, 학년, 교과목번호, 강좌번호, 교과목명,
     * 부제명, 학점, 강의, 실습, 수업교시, 수업형태, 강의실(동-호), 주담당교수,
     * 장바구니신청인원(전체), 재학생장바구니신청인원, 신입생장바구니신청인원,
     * 정원, 수강신청인원, 비고, 강의언어, 개설상태
     */
    private fun convertRowToLecture(
        row: org.apache.poi.ss.usermodel.Row,
        columnNameIndex: Map<String, Int>,
        year: String,
        semester: Semester,
    ): Lecture {
        // 헬퍼 함수: 컬럼명으로 셀 값 가져오기
        fun getCellValue(columnName: String): String? {
            val index = columnNameIndex[columnName] ?: return null
            return row
                .getCell(index)
                ?.stringCellValue
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
        }

        // 필수 필드 파싱
        val division = getCellValue("교과구분")
        val college = getCellValue("개설대학")
        val department = getCellValue("개설학과")
        val courseType = getCellValue("이수과정")
        val gradeStr = getCellValue("학년")
        val courseNumber = getCellValue("교과목번호")
        val lectureNumber = getCellValue("강좌번호")
        val courseTitle = getCellValue("교과목명")
        val subtitle = getCellValue("부제명")
        val creditsStr = getCellValue("학점")
        val classTimeStr = getCellValue("강의")
        val labTimeStr = getCellValue("실습")
        val professor = getCellValue("주담당교수")

        // 수강신청 관련 필드
        val preRegistrationCountStr = getCellValue("장바구니신청인원(전체)")
        val preRegistrationCountForNonFreshmanStr = getCellValue("재학생장바구니신청인원")
        val preRegistrationCountForFreshmanStr = getCellValue("신입생장바구니신청인원")
        val quotaStr = getCellValue("정원")
        val registrationCountStr = getCellValue("수강신청인원")

        // 기타 필드
        val remark = getCellValue("비고")
        val language = getCellValue("강의언어")
        val status = getCellValue("개설상태")

        // 정원 파싱: "50" 또는 "50 (45)" 형태
        val (quota, nonfreshmanQuota) = parseQuota(quotaStr)

        return Lecture(
            year = year,
            semester = semester,
            division = division,
            college = college,
            department = department?.replace("null", "")?.ifEmpty { college }, // null 문자열 처리
            courseType = courseType,
            grade = gradeStr?.toIntOrNull(),
            courseNumber = courseNumber,
            lectureNumber = lectureNumber,
            courseTitle = courseTitle,
            subtitle = subtitle,
            credits = creditsStr?.toDoubleOrNull(),
            classTime = classTimeStr?.toIntOrNull(),
            labTime = labTimeStr?.toIntOrNull(),
            professor = professor?.substringBeforeLast(" ("), // 괄호 안 정보 제거
            preRegistrationCount = preRegistrationCountStr?.toIntOrNull(),
            preRegistrationCountForNonFreshman = preRegistrationCountForNonFreshmanStr?.toIntOrNull(),
            preRegistrationCountForFreshman = preRegistrationCountForFreshmanStr?.toIntOrNull(),
            quota = quota,
            nonfreshmanQuota = nonfreshmanQuota,
            registrationCount = registrationCountStr?.toIntOrNull(),
            remark = remark,
            language = language,
            status = status,
        )
    }

    /**
     * 정원 문자열 파싱
     *
     * @param quotaStr "50" 또는 "50 (45)" 형태
     * @return (전체 정원, 재학생 정원)
     */
    private fun parseQuota(quotaStr: String?): Pair<Int, Int?> {
        if (quotaStr.isNullOrBlank()) return 0 to null

        val matchResult = quotaRegex.find(quotaStr) ?: return 0 to null

        val quota = matchResult.groups["quota"]?.value?.toIntOrNull() ?: 0
        val nonfreshmanQuota = matchResult.groups["quotaForNonFreshman"]?.value?.toIntOrNull()

        return quota to nonfreshmanQuota
    }
}

/**
 * 강의 임포트 결과
 */
data class ImportResult(
    val totalCount: Int, // 전체 처리된 행 수
    val successCount: Int, // 성공한 행 수
    val failCount: Int, // 실패한 행 수
)
