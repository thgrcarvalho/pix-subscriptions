package app.pixsub.backend.shared.error;

import app.pixsub.backend.shared.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/test-path");
    }

    @Test
    void handleNotFound_returns404AndBody() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Payment", 99L);

        ResponseEntity<GlobalExceptionHandler.ApiError> response =
                handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        GlobalExceptionHandler.ApiError body = response.getBody();
        assertThat(body).isNotNull();

        assertThat(body.status).isEqualTo(404);
        assertThat(body.error).isEqualTo("Not Found");
        assertThat(body.message).isEqualTo(ex.getMessage());
        assertThat(body.path).isEqualTo("/test-path");
        assertThat(body.timestamp).isNotNull();
    }

    @Test
    void handleIllegalArgument_returns400AndBody() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad argument");

        ResponseEntity<GlobalExceptionHandler.ApiError> response =
                handler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        GlobalExceptionHandler.ApiError body = response.getBody();
        assertThat(body).isNotNull();

        assertThat(body.status).isEqualTo(400);
        assertThat(body.error).isEqualTo("Bad Request");
        assertThat(body.message).isEqualTo("Bad argument");
        assertThat(body.path).isEqualTo("/test-path");
        assertThat(body.timestamp).isNotNull();
    }

    @Test
    void handleDomainValidation_returns422AndBody() {
        DomainValidationException ex = new DomainValidationException("Validation failed");

        ResponseEntity<GlobalExceptionHandler.ApiError> response =
                handler.handleDomainValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        GlobalExceptionHandler.ApiError body = response.getBody();
        assertThat(body).isNotNull();

        assertThat(body.status).isEqualTo(422);
        assertThat(body.error).isEqualTo("Unprocessable Entity");
        assertThat(body.message).isEqualTo("Validation failed");
        assertThat(body.path).isEqualTo("/test-path");
        assertThat(body.timestamp).isNotNull();
    }

    @Test
    void handleGeneric_returns500WithGenericMessage() {
        RuntimeException ex = new RuntimeException("Boom!");

        ResponseEntity<GlobalExceptionHandler.ApiError> response =
                handler.handleGeneric(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        GlobalExceptionHandler.ApiError body = response.getBody();
        assertThat(body).isNotNull();

        assertThat(body.status).isEqualTo(500);
        assertThat(body.error).isEqualTo("Internal Server Error");
        // important: generic message, not leaking internal exception detail
        assertThat(body.message).isEqualTo("Unexpected error");
        assertThat(body.path).isEqualTo("/test-path");
        assertThat(body.timestamp).isNotNull();
    }
}
