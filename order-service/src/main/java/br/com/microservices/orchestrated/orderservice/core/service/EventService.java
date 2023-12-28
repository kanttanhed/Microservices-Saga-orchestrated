package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.dto.EventFilters;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository repository;

    public void notifyEnding(Event event){
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Order {} with Saga notified! TransactionId: {}", event.getOrderId(),event.getTransactionId());
    }

    public List<Event> findAll(){
        return repository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Finds an event based on the provided filters.
     *
     * @param filters The {@code EventFilters} containing filter criteria. Should not be {@code null}.
     * @return The found event.
     * @throws ValidationException If the filters are invalid or no event is found.
     */
    public Event findByFilters(EventFilters filters) {
        validateEmptyFilters(filters);
        if (!StringUtils.isEmpty((filters.getOrderId()))) {
            return findByOrderId(filters.getOrderId());
        } else {
            return findByTransactionId(filters.getTransactionId());
        }
    }


    /**
     * Validates that either OrderID or TransactionID is informed in the provided filters.
     * Throws a ValidationException if both are empty or null.
     *
     * @param filters The {@code EventFilters} to be validated. Should not be {@code null}.
     * @throws ValidationException If both OrderID and TransactionID are empty or null.
     */
    private void validateEmptyFilters(EventFilters filters) {
        if ((filters.getOrderId() == null || filters.getOrderId().trim().isEmpty()) &&
                (filters.getTransactionId() == null || filters.getTransactionId().trim().isEmpty())) {
            throw new ValidationException("Either OrderID or TransactionID must be informed. Both are empty.");
        }
    }

    private Event findByTransactionId(String transactionId) {
        return repository
                .findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId)
                .orElseThrow(() -> new ValidationException("Event not found by TransactionID: "+transactionId));
    }

    private Event findByOrderId(String orderId) {
        return repository
                .findTop1ByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new ValidationException("Event not found by orderID. "+orderId));
    }

    public Event save(Event event){
        return repository.save(event);
    }
}
