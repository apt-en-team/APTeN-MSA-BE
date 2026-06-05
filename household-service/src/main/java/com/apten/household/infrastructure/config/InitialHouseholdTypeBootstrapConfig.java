package com.apten.household.infrastructure.config;

import com.apten.household.domain.entity.HouseholdType;
import com.apten.household.domain.repository.HouseholdTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

// 세대 타입 기본 데이터를 서버 시작 시 자동으로 세팅한다.
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.bootstrap.household-type", name = "enabled", havingValue = "true", matchIfMissing = true)
public class InitialHouseholdTypeBootstrapConfig {

    private final HouseholdTypeRepository householdTypeRepository;

    // 애플리케이션 시작 시 세대 타입 기본 데이터가 없으면 자동 생성한다.
    @Bean
    public CommandLineRunner initialHouseholdTypeBootstrapRunner() {
        return args -> {
            for (HouseholdTypeEntry entry : HouseholdTypeEntry.values()) {
                createHouseholdTypeIfNotExists(entry);
            }
        };
    }

    private void createHouseholdTypeIfNotExists(HouseholdTypeEntry entry) {
        if (householdTypeRepository.existsByTypeCode(entry.typeCode)) {
            return;
        }

        householdTypeRepository.save(HouseholdType.builder()
                .typeCode(entry.typeCode)
                .typeName(entry.typeName)
                .exclusiveAreaM2(entry.area)
                .isActive(true)
                .build());

        log.info("세대 타입 기본 데이터를 생성했습니다. typeCode={}, typeName={}", entry.typeCode, entry.typeName);
    }

    private enum HouseholdTypeEntry {
        AREA_39 ("AREA_39",  "전용 39㎡ (17평)",   "39.00"),
        AREA_43 ("AREA_43",  "전용 43㎡ (18평)",   "43.00"),
        AREA_45 ("AREA_45",  "전용 45㎡ (19평)",   "45.00"),
        AREA_49 ("AREA_49",  "전용 49㎡ (21평)",   "49.00"),
        AREA_51 ("AREA_51",  "전용 51㎡ (22평)",   "51.00"),
        AREA_53 ("AREA_53",  "전용 53㎡ (23평)",   "53.00"),
        AREA_55 ("AREA_55",  "전용 55㎡ (24평)",   "55.00"),
        AREA_59 ("AREA_59",  "전용 59㎡ (25평)",   "59.00"),
        AREA_62 ("AREA_62",  "전용 62㎡ (26평)",   "62.00"),
        AREA_65 ("AREA_65",  "전용 65㎡ (27평)",   "65.00"),
        AREA_70 ("AREA_70",  "전용 70㎡ (29평)",   "70.00"),
        AREA_74 ("AREA_74",  "전용 74㎡ (30평)",   "74.00"),
        AREA_80 ("AREA_80",  "전용 80㎡ (32평)",   "80.00"),
        AREA_84 ("AREA_84",  "전용 84㎡ (34평)",   "84.00"),
        AREA_92 ("AREA_92",  "전용 92㎡ (37평)",   "92.00"),
        AREA_99 ("AREA_99",  "전용 99㎡ (39평)",   "99.00"),
        AREA_101("AREA_101", "전용 101㎡ (40평)", "101.00"),
        AREA_114("AREA_114", "전용 114㎡ (45평)", "114.00"),
        AREA_125("AREA_125", "전용 125㎡ (50평)", "125.00"),
        AREA_135("AREA_135", "전용 135㎡ (54평)", "135.00");

        final String typeCode;
        final String typeName;
        final BigDecimal area;

        HouseholdTypeEntry(String typeCode, String typeName, String area) {
            this.typeCode = typeCode;
            this.typeName = typeName;
            this.area = new BigDecimal(area);
        }
    }
}
