package app.pixsub.backend.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Writes structured audit events as JSON log lines.
 *
 * <p>In production, route these log lines to a separate appender or log
 * aggregation system (e.g. Loki, Elasticsearch) for audit trail queries.</p>
 */
@Component
public class AuditLogger {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");
    private final ObjectMapper mapper = new ObjectMapper();

    public void log(String event, Map<String, Object> context) {
        try {
            Map<String, Object> entry = new java.util.LinkedHashMap<>();
            entry.put("timestamp", Instant.now().toString());
            entry.put("event", event);
            entry.putAll(context);
            log.info(mapper.writeValueAsString(entry));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit event: {}", event, e);
        }
    }
}
