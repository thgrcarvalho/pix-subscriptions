package app.pixsub.backend.subscription.web;

import app.pixsub.backend.subscription.application.CreateSubscriptionService;
import app.pixsub.backend.subscription.application.ListSubscriptionsForStudentService;
import app.pixsub.backend.subscription.domain.Subscription;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final CreateSubscriptionService createSubscriptionService;
    private final ListSubscriptionsForStudentService listSubscriptionsForStudentService;

    public SubscriptionController(CreateSubscriptionService createSubscriptionService,
                                  ListSubscriptionsForStudentService listSubscriptionsForStudentService) {
        this.createSubscriptionService = createSubscriptionService;
        this.listSubscriptionsForStudentService = listSubscriptionsForStudentService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody SubscriptionCreateRequest request) {
        Subscription subscription = createSubscriptionService.create(
                request.getStudentId(),
                request.getPlanId()
        );

        SubscriptionResponse response = toResponse(subscription);
        return ResponseEntity
                .created(URI.create("/api/subscriptions/" + subscription.getId()))
                .body(response);
    }

    @GetMapping
    public List<SubscriptionResponse> listByStudent(@RequestParam("studentId") Long studentId) {
        return listSubscriptionsForStudentService.listByStudent(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private SubscriptionResponse toResponse(Subscription s) {
        return new SubscriptionResponse(
                s.getId(),
                s.getStudentId(),
                s.getPlanId(),
                s.getStatus(),
                s.getNextPaymentDate(),
                s.getCreatedAt()
        );
    }
}
