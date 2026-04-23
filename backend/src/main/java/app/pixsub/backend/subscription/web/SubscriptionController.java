package app.pixsub.backend.subscription.web;

import app.pixsub.backend.shared.PageResult;
import app.pixsub.backend.subscription.application.CreateSubscriptionService;
import app.pixsub.backend.subscription.application.ListSubscriptionsForStudentService;
import app.pixsub.backend.subscription.domain.Subscription;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public PageResult<SubscriptionResponse> listByStudent(
            @RequestParam("studentId") Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<Subscription> result = listSubscriptionsForStudentService.listByStudent(studentId, page, size);
        return new PageResult<>(result.content().stream().map(this::toResponse).toList(),
                result.totalElements(), result.totalPages(), result.page(), result.size());
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
