package io.allink.tcp.koces.receipt.service;

import io.allink.tcp.koces.receipt.model.ReceiptEntity;
import io.allink.tcp.koces.receipt.repository.ReceiptRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReceiptService {
    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {this.receiptRepository = receiptRepository;}

    @Transactional
    public Long saveAndGetSerialNumber(ReceiptEntity receiptEntity) {
        System.out.println("✅ Request 저장 완료: ");

        ReceiptEntity savedEntity = receiptRepository.save(receiptEntity);

        return savedEntity.getSeq();
    }
}
