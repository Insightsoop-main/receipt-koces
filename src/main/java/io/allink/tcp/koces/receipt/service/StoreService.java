package io.allink.tcp.koces.receipt.service;


import io.allink.tcp.koces.receipt.model.StoreEntity;
import io.allink.tcp.koces.receipt.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {this.storeRepository = storeRepository;}

    public StoreEntity findByBusinessNo(String businessNo) {
        return storeRepository.findByBusinessNo(businessNo)
                .orElseThrow(() -> new RuntimeException("storeEntity not found for businessNo: " + businessNo));
    }
}
