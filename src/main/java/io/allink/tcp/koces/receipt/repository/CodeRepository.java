package io.allink.tcp.koces.receipt.repository;

import io.allink.tcp.koces.receipt.model.CodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, Long> {

    Optional<CodeEntity> findByCdGrpAndCdTypeAndCdVal(String cdGrp, String cdType, String cdVal);
}
