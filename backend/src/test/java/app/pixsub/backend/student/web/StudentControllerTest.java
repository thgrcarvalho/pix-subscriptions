package app.pixsub.backend.student.web;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.shared.error.GlobalExceptionHandler;
import app.pixsub.backend.test.WebMvcTestSecurityConfig;
import app.pixsub.backend.student.application.CreateStudentService;
import app.pixsub.backend.student.application.ListStudentsForTrainerService;
import app.pixsub.backend.student.domain.Student;
import app.pixsub.backend.student.domain.StudentStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import({GlobalExceptionHandler.class, WebMvcTestSecurityConfig.class})
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateStudentService createStudentService;

    @MockitoBean
    private ListStudentsForTrainerService listStudentsForTrainerService;

    @Test
    void create_whenValidRequest_returns201AndBodyAndLocation() throws Exception {
        long trainerId = 10L;
        String name = "John Doe";
        String contact = "whatsapp:+550000000";

        Instant createdAt = Instant.parse("2025-01-01T12:00:00Z");

        Student created = new Student(
                5L,
                trainerId,
                name,
                contact,
                StudentStatus.ACTIVE,
                createdAt,
                createdAt
        );

        given(createStudentService.create(trainerId, name, contact))
                .willReturn(created);

        String body = """
                {
                  "trainerId": 10,
                  "name": "John Doe",
                  "contact": "whatsapp:+550000000"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/students/5"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.trainerId").value(10))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.contact").value("whatsapp:+550000000"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(createStudentService).create(trainerId, name, contact);
    }

    @Test
    void create_whenMissingName_returns400AndDoesNotCallService() throws Exception {
        String body = """
                {
                  "trainerId": 10,
                  "contact": "whatsapp:+550000000"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(createStudentService, never())
                .create(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void listByTrainer_returnsStudentsFromService() throws Exception {
        long trainerId = 10L;

        Instant ts1 = Instant.parse("2025-01-01T10:00:00Z");
        Instant ts2 = Instant.parse("2025-01-02T11:00:00Z");

        Student s1 = new Student(1L, trainerId, "Alice", "alice@example.com",
                StudentStatus.ACTIVE, ts1, ts1);
        Student s2 = new Student(2L, trainerId, "Bob", "bob@example.com",
                StudentStatus.PAUSED, ts2, ts2);

        given(listStudentsForTrainerService.list(trainerId, 0, 20))
                .willReturn(new PageResult<>(List.of(s1, s2), 2, 1, 0, 20));

        mockMvc.perform(get("/api/students")
                        .param("trainerId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Bob"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(listStudentsForTrainerService).list(eq(trainerId), eq(0), eq(20));
    }
}
