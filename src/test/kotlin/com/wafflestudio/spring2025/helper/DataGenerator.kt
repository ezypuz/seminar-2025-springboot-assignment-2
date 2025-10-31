package com.wafflestudio.spring2025.helper

import com.wafflestudio.spring2025.board.model.Board
import com.wafflestudio.spring2025.board.repository.BoardRepository
import com.wafflestudio.spring2025.comment.model.Comment
import com.wafflestudio.spring2025.comment.repository.CommentRepository
import com.wafflestudio.spring2025.lecture.model.ClassSession
import com.wafflestudio.spring2025.lecture.model.Lecture
import com.wafflestudio.spring2025.lecture.repository.ClassSessionRepository
import com.wafflestudio.spring2025.lecture.repository.LectureRepository
import com.wafflestudio.spring2025.post.model.Post
import com.wafflestudio.spring2025.post.repository.PostRepository
import com.wafflestudio.spring2025.timeTable.model.Semester
import com.wafflestudio.spring2025.timeTable.model.TimeTable
import com.wafflestudio.spring2025.timeTable.repository.TimeTableRepository
import com.wafflestudio.spring2025.user.JwtTokenProvider
import com.wafflestudio.spring2025.user.model.User
import com.wafflestudio.spring2025.user.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class DataGenerator(
    private val userRepository: UserRepository,
    private val boardRepository: BoardRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val timeTableRepository: TimeTableRepository,
    private val lectureRepository: LectureRepository,
    private val classSessionRepository: ClassSessionRepository,
) {
    fun generateUser(
        username: String? = null,
        password: String? = null,
    ): Pair<User, String> {
        val user =
            userRepository.save(
                User(
                    username = username ?: "user-${Random.Default.nextInt(1000000)}",
                    password = BCrypt.hashpw(password ?: "password-${Random.Default.nextInt(1000000)}", BCrypt.gensalt()),
                ),
            )
        return user to jwtTokenProvider.createToken(user.username)
    }

    fun generateBoard(name: String? = null): Board {
        val board =
            boardRepository.save(
                Board(
                    name = name ?: "board-${Random.Default.nextInt(1000000)}",
                ),
            )
        return board
    }

    fun generatePost(
        title: String? = null,
        content: String? = null,
        user: User? = null,
        board: Board? = null,
    ): Post {
        val post =
            postRepository.save(
                Post(
                    title = title ?: "title-${Random.Default.nextInt(1000000)}",
                    content = content ?: "content-${Random.Default.nextInt(1000000)}",
                    userId = (user ?: generateUser().first).id!!,
                    boardId = (board ?: generateBoard()).id!!,
                ),
            )
        return post
    }

    fun generateComment(
        content: String? = null,
        user: User? = null,
        post: Post? = null,
    ): Comment {
        val comment =
            commentRepository.save(
                Comment(
                    content = content ?: "content-${Random.Default.nextInt(1000000)}",
                    userId = (user ?: generateUser().first).id!!,
                    postId = (post ?: generatePost()).id!!,
                ),
            )
        return comment
    }

    /**
     * 시간표 생성
     */
    fun generateTimetable(
        name: String? = null,
        year: Int = 2025,
        semester: Semester = Semester.AUTUMN,
        user: User? = null,
    ): TimeTable {
        val owner = user ?: generateUser().first
        return timeTableRepository.save(
            TimeTable(
                name = name ?: "timetable-${Random.Default.nextInt(1000000)}",
                year = year,
                semester = semester,
                userId = owner.id!!,
            ),
        )
    }

    /**
     * 강의 생성 (세션 포함)
     */
    fun generateLecture(
        courseTitle: String? = null,
        credits: Double = 3.0,
        year: String = "2025",
        semester: Semester = Semester.AUTUMN,
        professor: String? = null,
        sessions: List<SessionInfo>? = null,
    ): Lecture {
        val lecture =
            lectureRepository.save(
                Lecture(
                    year = year,
                    semester = semester,
                    division = "전공",
                    college = "공과대학",
                    department = "컴퓨터공학부",
                    courseType = "전공필수",
                    grade = 3,
                    courseNumber = "M1522.${Random.Default.nextInt(1000, 9999)}",
                    lectureNumber = "001",
                    courseTitle = courseTitle ?: "강의-${Random.Default.nextInt(1000000)}",
                    subtitle = null,
                    credits = credits,
                    classTime = 3,
                    labTime = 0,
                    professor = professor ?: "교수-${Random.Default.nextInt(100)}",
                    preRegistrationCount = null,
                    preRegistrationCountForNonFreshman = null,
                    preRegistrationCountForFreshman = null,
                    quota = 40,
                    nonfreshmanQuota = 35,
                    registrationCount = 30,
                    remark = null,
                    language = "한국어",
                    status = "개설",
                ),
            )

        // 세션 정보가 제공되면 추가
        sessions?.forEach { sessionInfo ->
            classSessionRepository.save(
                ClassSession(
                    lectureId = lecture.id!!,
                    dayOfWeek = sessionInfo.dayOfWeek,
                    startTime = sessionInfo.startTime,
                    endTime = sessionInfo.endTime,
                    location = sessionInfo.location ?: "302-308",
                    courseFormat = "대면",
                ),
            )
        }

        return lecture
    }

    /**
     * 세션 정보를 담는 데이터 클래스
     */
    data class SessionInfo(
        val dayOfWeek: Int, // 0=월, 1=화, ...
        val startTime: Int, // 분 단위 (예: 600 = 10:00)
        val endTime: Int, // 분 단위 (예: 750 = 12:30)
        val location: String? = null,
    )
}