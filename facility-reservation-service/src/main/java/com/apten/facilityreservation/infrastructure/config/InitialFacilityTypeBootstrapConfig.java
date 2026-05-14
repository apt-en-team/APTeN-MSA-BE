package com.apten.facilityreservation.infrastructure.config;

import com.apten.facilityreservation.domain.entity.FacilityType;
import com.apten.facilityreservation.domain.enums.FacilityTypeCode;
import com.apten.facilityreservation.domain.repository.FacilityTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 시설 타입 기본 데이터를 서버 시작 시 자동으로 세팅한다.
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.bootstrap.facility-type", name = "enabled", havingValue = "true", matchIfMissing = true)
public class InitialFacilityTypeBootstrapConfig {

    private final FacilityTypeRepository facilityTypeRepository;

    // 애플리케이션 시작 시 시설 타입 기본 데이터가 없으면 자동 생성한다.
    @Bean
    public CommandLineRunner initialFacilityTypeBootstrapRunner() {
        return args -> {
            for (FacilityTypeCode typeCode : FacilityTypeCode.values()) {
                createFacilityTypeIfNotExists(typeCode);
            }
        };
    }

    // 시설 타입 코드가 이미 있으면 중복 생성하지 않고, 없을 때만 저장한다.
    private void createFacilityTypeIfNotExists(FacilityTypeCode typeCode) {
        if (facilityTypeRepository.existsByTypeCode(typeCode)) {
            log.info("시설 타입 기본 데이터가 이미 존재하여 생성을 건너뜁니다. typeCode={}, typeName={}",
                    typeCode.name(), typeCode.getValue());
            return;
        }

        FacilityType facilityType = FacilityType.builder()
                .typeCode(typeCode)
                .typeName(typeCode.getValue())
                .description(typeCode.getValue() + " 시설 타입")
                .isActive(true)
                .build();

        facilityTypeRepository.save(facilityType);

        log.info("시설 타입 기본 데이터를 생성했습니다. typeCode={}, typeName={}",
                typeCode.name(), typeCode.getValue());
    }
}