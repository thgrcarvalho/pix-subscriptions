package app.pixsub.backend.trainer.web;

import app.pixsub.backend.shared.error.DomainValidationException;
import app.pixsub.backend.shared.error.GlobalExceptionHandler;
import app.pixsub.backend.trainer.application.RegisterTrainerService;
import app.pixsub.backend.trainer.domain.Trainer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainerController.class)
@Import(GlobalExceptionHandler.class)
class TrainerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterTrainerService registerTrainerService;

    @Test
    void register_whenValid_returns200WithTrainerResponse() throws Exception {
        Trainer trainer = new Trainer(
                1L,
                "thiago@example.com",
                "HASHED",
                "Thiago",
                "pix-key-123",
                Instant.now(),
                Instant.now()
        );

        given(registerTrainerService.register(anyString(), anyString(), anyString(), any()))
                .willReturn(trainer);

        String body = """
                {
                  "email": "thiago@example.com",
                  "password": "secret123",
                  "name": "Thiago",
                  "pixKey": "pix-key-123"
                }
                """;

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("thiago@example.com"))
                .andExpect(jsonPath("$.name").value("Thiago"))
                .andExpect(jsonPath("$.pixKey").value("pix-key-123"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void register_whenEmailAlreadyUsed_returns422WithErrorBody() throws Exception {
        given(registerTrainerService.register(anyString(), anyString(), anyString(), any()))
                .willThrow(new DomainValidationException("Email already in use"));

        String body = """
                {
                  "email": "thiago@example.com",
                  "password": "secret123",
                  "name": "Thiago",
                  "pixKey": "pix-key-123"
                }
                """;

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value(containsString("Email already in use")))
                .andExpect(jsonPath("$.path").value("/api/trainers/register"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
