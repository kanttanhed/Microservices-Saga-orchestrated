package br.com.microservices.orchestrated.orchestratorservice.core.dto;

import br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private String id;
    private String transactionId;
    private String orderId;
    private OrderDto payload;
    private EEventSource source;
    private ESagaStatus status;
    private List<HistoryDto> eventHistory;
    private LocalDateTime createdAt;
}
