package br.com.microservices.orchestrated.orchestratorservice.core.service;

import br.com.microservices.orchestrated.orchestratorservice.core.dto.EventDto;
import br.com.microservices.orchestrated.orchestratorservice.core.dto.HistoryDto;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import br.com.microservices.orchestrated.orchestratorservice.core.producer.SagaOrchestratorProducer;
import br.com.microservices.orchestrated.orchestratorservice.core.saga.SagaExecutionController;
import br.com.microservices.orchestrated.orchestratorservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource.ORCHESTRATOR;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus.FAIL;
import static br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus.SUCCESS;

@Slf4j
@Service
@AllArgsConstructor
public class OrchestrationService   {

    private final SagaOrchestratorProducer producer;
    private final JsonUtil jsonUtil;
    private final SagaExecutionController sagaExecutionController;

    public void startSaga(EventDto eventDto) {
        eventDto.setSource(ORCHESTRATOR);
        eventDto.setStatus(SUCCESS);
        var topic = getTopic(eventDto);
        log.info("SAGA STARTED!");
        addHistory(eventDto, "Saga started!");
        sendToProducerWithTopic(eventDto, topic);
    }

    public void finishSagaSuccess(EventDto eventDto) {
        eventDto.setSource(ORCHESTRATOR);
        eventDto.setStatus(SUCCESS);
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}!", eventDto.getId());
        addHistory(eventDto, "Saga finished successfully!");
        notifyFinishedSaga(eventDto);
    }

    public void finishSagaFail(EventDto eventDto) {
        eventDto.setSource(ORCHESTRATOR);
        eventDto.setStatus(FAIL);
        log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}!", eventDto.getId());
        addHistory(eventDto, "Saga finished with errors!");
        notifyFinishedSaga(eventDto);
    }

    public void continueSaga(EventDto eventDto) {
        var topic = getTopic(eventDto);
        log.info("SAGA CONTINUING FOR EVENT {}", eventDto.getId());
        sendToProducerWithTopic(eventDto, topic);
    }

    private ETopics getTopic(EventDto eventDto) {
        return sagaExecutionController.getNextTopic(eventDto);
    }

    private void addHistory(EventDto eventDto, String message) {
        var history = HistoryDto
                .builder()
                .source(eventDto.getSource())
                .status(eventDto.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        eventDto.addToHistory(history);
    }

    private void sendToProducerWithTopic(EventDto eventDto, ETopics topic) {
        producer.sendEvent(jsonUtil.toJson(eventDto), topic.getTopic());
    }

    private void notifyFinishedSaga(EventDto eventDto) {
        producer.sendEvent(jsonUtil.toJson(eventDto), ETopics.NOTIFY_ENDING.getTopic());
    }
}