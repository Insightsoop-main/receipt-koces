package io.allink.tcp.koces.receipt.service;

import io.allink.tcp.koces.receipt.model.CodeEntity;
import io.allink.tcp.koces.receipt.repository.CodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CodeService {
    private final CodeRepository codeRepository;

    public CodeService(CodeRepository codeRepository) {this.codeRepository = codeRepository;}

    public String findByCdGrpAndCdTypeAndCdVal(String cdGrp, String cdType, String cdVal) {
        Optional<CodeEntity> codeEntity = codeRepository.findByCdGrpAndCdTypeAndCdVal(cdGrp, cdType, cdVal);

        return codeEntity.map(CodeEntity::getCdNm).orElse(null);
    }

}
