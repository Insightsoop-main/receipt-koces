package io.allink.tcp.koces.receipt.service;


import io.allink.tcp.koces.receipt.model.Store;
import io.allink.tcp.koces.receipt.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Store getStore(String storeUid, String deviceId) {
        return storeRepository.findByMerchantStoreIdAndDeviceId(storeUid, deviceId).orElse(null);
//        return storeRepository.findTopByStoreUidLikeAndBusinessNo(storeUid)
//            businessNo.replaceAll("(\\d{3})(\\d{2})(\\d{5})", "$1-$2-$3")).orElse(null);
    }

    /*public StoreEntity findTopByBusinessNoOrderByRegDateDesc(String businessNo) {
        return storeRepository.findTopByBusinessNoOrderByRegDateDesc(businessNo).orElse(null);
    }*/
}
