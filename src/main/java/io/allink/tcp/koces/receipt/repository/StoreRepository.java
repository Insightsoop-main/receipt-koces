package io.allink.tcp.koces.receipt.repository;

import io.allink.tcp.koces.receipt.model.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, String> {
//    Optional<StoreEntity> findTopByBusinessNoOrderByRegDateDesc(String businessNo);
}
