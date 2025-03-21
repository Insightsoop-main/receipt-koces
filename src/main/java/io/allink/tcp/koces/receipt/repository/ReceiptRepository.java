package io.allink.tcp.koces.receipt.repository;

import io.allink.tcp.koces.receipt.model.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long> {
}
