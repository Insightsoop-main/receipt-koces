package io.allink.tcp.koces.receipt.service;


import java.util.List;

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

    public Store findAllByBusinessNoAndDeviceId(String businessNo, String deviceId) {
        // 신 환경(Supabase): store.business_no는 하이픈 없는 10자리 숫자로 저장됨
        // KOCES가 보내는 businessNo도 동일 형식이라 변환 불필요
        final List<Store> stores = storeRepository.findAllByBusinessNoAndDeviceId(businessNo, deviceId);
        if (stores.isEmpty()) {
            return null;
        }
        return stores.get(0);
    }
}
