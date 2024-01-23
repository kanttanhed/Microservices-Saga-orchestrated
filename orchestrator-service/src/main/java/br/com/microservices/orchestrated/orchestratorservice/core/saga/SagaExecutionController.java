package br.com.microservices.orchestrated.orchestratorservice.core.saga;

import br.com.microservices.orchestrated.orchestratorservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orchestratorservice.core.dto.EventDto;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static br.com.microservices.orchestrated.orchestratorservice.core.saga.SagaHandler.*;
import static java.lang.String.format;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {
    private static final String SAGA_LOG_ID = "ORDER ID: %s | TRANSACTION ID %s | EVENT ID %s";

    public ETopics getNextTopic(EventDto eventDto) {
        if (isEmpty(eventDto.getSource()) || isEmpty(eventDto.getStatus())) {
            throw new ValidationException("Source and status must be informed.");
        }
        var topic = findTopicBySourceAndStatus(eventDto);
        logCurrentSaga(eventDto, topic);
        return topic;
    }

    private ETopics findTopicBySourceAndStatus(EventDto eventDto) {
        return (ETopics) (Arrays.stream(SAGA_HANDLER)
                .filter(row -> isEventSourceAndStatusValid(eventDto, row))
                .map(i -> i[TOPIC_INDEX])
                .findFirst()
                .orElseThrow(() -> new ValidationException("Topic not found!")));
    }

    private boolean isEventSourceAndStatusValid(EventDto eventDto,
                                                Object[] row) {
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[SAGA_STATUS_INDEX];
        return source.equals(eventDto.getSource()) && status.equals(eventDto.getStatus());
    }

    private void logCurrentSaga(EventDto eventDto, ETopics topic) {
        var sagaId = createSagaId(eventDto);
        var source = eventDto.getSource();
        switch (eventDto.getStatus()) {
            case SUCCESS -> log.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
            case ROLLBACK_PENDING -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
            case FAIL -> log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
        }
    }

    private String createSagaId(EventDto eventDto) {
        return format(SAGA_LOG_ID,
                eventDto.getPayload().getId(),
                eventDto.getTransactionId(),
                eventDto.getId());
    }
}