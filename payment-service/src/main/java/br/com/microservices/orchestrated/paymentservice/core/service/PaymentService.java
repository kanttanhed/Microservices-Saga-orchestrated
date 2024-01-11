package br.com.microservices.orchestrated.paymentservice.core.service;

import br.com.microservices.orchestrated.paymentservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.paymentservice.core.dto.EventDto;
import br.com.microservices.orchestrated.paymentservice.core.dto.HistoryDto;
import br.com.microservices.orchestrated.paymentservice.core.dto.OrderProductsDto;
import br.com.microservices.orchestrated.paymentservice.core.enums.EPaymentStatus;
import br.com.microservices.orchestrated.paymentservice.core.model.Payment;
import br.com.microservices.orchestrated.paymentservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.core.repository.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.paymentservice.core.enums.ESagaStatus.SUCCESS;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
    private static final Double REDUCE_SUM_VALUE = 0.0;
    private static final Double MIN_VALUE_AMOUNT = 0.1;

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final PaymentRepository paymentRepository;

    public void realizePayment(EventDto eventDto) {
        try {
            checkCurrentValidation(eventDto);
            createPendingPayment(eventDto);
            handleSuccess(eventDto);
        } catch (Exception ex) {
            log.error("Error trying to make payment: ", ex);
        }
        producer.sendEvent(jsonUtil.toJson(eventDto));
    }

    private void checkCurrentValidation(EventDto eventDto) {
        if (paymentRepository.existsByOrderIdAndTransactionId(eventDto.getPayload().getId(), eventDto.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private void createPendingPayment(EventDto eventDto) {
        var totalAmount = calculateAmount(eventDto);
        var totalItems = calculateTotalItems(eventDto);
        var payment = Payment
                .builder()
                .orderId(eventDto.getPayload().getId())
                .transactionId(eventDto.getTransactionId())
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .build();
        save(payment);
        setEventAmountItems(eventDto, payment);
    }

    private double calculateAmount(EventDto eventDto) {
        return eventDto
                .getPayload()
                .getProducts()
                .stream()
                .map(product -> product.getQuantity() * product.getProduct().getUnitValue())
                .reduce(REDUCE_SUM_VALUE, Double::sum);
    }

    private int calculateTotalItems(EventDto eventDto) {
        return eventDto
                .getPayload()
                .getProducts()
                .stream()
                .map(OrderProductsDto::getQuantity)
                .reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
    }

    private void setEventAmountItems(EventDto event, Payment payment) {
        event.getPayload().setTotalAmount(payment.getTotalAmount());
        event.getPayload().setTotalItems(payment.getTotalItems());
    }

    private void validateAmount(double amount) {
        if (amount < MIN_VALUE_AMOUNT) {
            throw new ValidationException("The minimal amount available is ".concat(String.valueOf(MIN_VALUE_AMOUNT)));
        }
    }

    private void changePaymentToSuccess(Payment payment) {
        payment.setStatus(EPaymentStatus.SUCCESS);
        save(payment);
    }
    private void handleSuccess(EventDto eventDto) {
        eventDto.setStatus(SUCCESS);
        eventDto.setSource(CURRENT_SOURCE);
        addHistory(eventDto, "Payment realized successfully!");
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

    private void save(Payment payment) {
        paymentRepository.save(payment);
    }
}
