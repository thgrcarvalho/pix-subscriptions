package app.pixsub.backend.trainer.application;

import app.pixsub.backend.shared.error.DomainValidationException;
import app.pixsub.backend.trainer.domain.Trainer;
import app.pixsub.backend.trainer.domain.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterTrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private RegisterTrainerService service;

    @Test
    void register_whenEmailNotUsed_savesTrainerWithEncodedPassword() {
        String name = "Thiago";
        String email = "thiago@example.com";
        String rawPassword = "secret";
        String encoded = "ENCODED";

        when(trainerRepository.existsByEmail(email)).thenReturn(false);
        when(encoder.encode(rawPassword)).thenReturn(encoded);
        // just echo the passed trainer
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = service.register(email, rawPassword, name, null);

        // verify repository interactions
        verify(trainerRepository).existsByEmail(email);

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(trainerCaptor.capture());

        Trainer saved = trainerCaptor.getValue();
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getName()).isEqualTo(name);
        assertThat(saved.getPasswordHash()).isEqualTo(encoded);

        // service returns whatever repository returns
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPasswordHash()).isEqualTo(encoded);
    }

    @Test
    void register_whenEmailAlreadyInUse_throwsDomainValidationException() {
        String name = "Thiago";
        String email = "thiago@example.com";

        when(trainerRepository.existsByEmail(email)).thenReturn(true);

        // again, correct parameter order: (email, rawPassword, name, pixKey)
        assertThatThrownBy(() -> service.register(email, "whatever", name, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Email already in use");

        // existsByEmail was checked
        verify(trainerRepository).existsByEmail(email);
        // but no save, and no encoding
        verify(trainerRepository, never()).save(any());
        verifyNoInteractions(encoder);
    }
}
