package br.com.microservices.orchestrated.productvalidationservice.core.service;

import br.com.microservices.orchestrated.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.EventDto;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.HistoryDto;
import br.com.microservices.orchestrated.productvalidationservice.core.dto.OrderProductsDto;
import br.com.microservices.orchestrated.productvalidationservice.core.model.Validation;
import br.com.microservices.orchestrated.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

import static br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus.ROLLBACK_PENDING;
import static br.com.microservices.orchestrated.productvalidationservice.core.enums.ESagaStatus.SUCCESS;

@Slf4j
@Service
@AllArgsConstructor
public class ProductValidationService {

    private static final String CURRENT_SOURCE= "PRODUCT_VALIDATION_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private final ProductRepository productRepository;
    private final ValidationRepository validationRepository;

    public void validateExistingProducts(EventDto eventDto) {
        try {
            checkCurrentValidation(eventDto);
            createValidation(eventDto, true);
            handleSuccess(eventDto);
        } catch (Exception ex) {
            log.error("Error trying to validate product: ", ex);
            handleFailCurrentNotExecuted(eventDto, ex.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(eventDto));
    }

    private void validateProductsInformed(EventDto eventDto) {
        if (ObjectUtils.isEmpty(eventDto.getPayload()) || ObjectUtils.isEmpty(eventDto.getPayload().getProducts())) {
            throw new ValidationException("Product list is empty!");
        }
        if (ObjectUtils.isEmpty(eventDto.getPayload().getId()) || ObjectUtils.isEmpty(eventDto.getTransactionId())) {
            throw new ValidationException("OrderID and TransactionID must be informed!");
        }
    }

    private void checkCurrentValidation(EventDto eventDto) {
        validateProductsInformed(eventDto);
        if (validationRepository.existsByOrderIdAndTransactionId(
                eventDto.getOrderId(), eventDto.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
        eventDto.getPayload().getProducts().forEach(product -> {
            validateProductInformed(product);
            validateExistingProduct(product.getProduct().getCode());
        });
    }

    private void validateProductInformed(OrderProductsDto product) {
        if (ObjectUtils.isEmpty(product.getProduct()) || ObjectUtils.isEmpty(product.getProduct().getCode())) {
            throw new ValidationException("Product must be informed!");
        }
    }

}
