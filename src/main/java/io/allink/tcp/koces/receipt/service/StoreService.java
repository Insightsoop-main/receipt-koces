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

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public StoreEntity getStore(String storeUid) {
        return storeRepository.findById(storeUid).orElse(null);
//        return storeRepository.findTopByStoreUidLikeAndBusinessNo(storeUid)
//            businessNo.replaceAll("(\\d{3})(\\d{2})(\\d{5})", "$1-$2-$3")).orElse(null);
    }

    /*public StoreEntity findTopByBusinessNoOrderByRegDateDesc(String businessNo) {
        return storeRepository.findTopByBusinessNoOrderByRegDateDesc(businessNo).orElse(null);
    }*/
}
