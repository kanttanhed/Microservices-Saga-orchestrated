package br.com.microservices.orchestrated.productvalidationservice.core.dto;

import br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String source;
    private ESagaStatus status;
    private List<HistoryDto> eventHistory;
    private LocalDateTime createdAt;

    public void addToHistory(HistoryDto history) {
        if (ObjectUtils.isEmpty(eventHistory)){
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }
}
