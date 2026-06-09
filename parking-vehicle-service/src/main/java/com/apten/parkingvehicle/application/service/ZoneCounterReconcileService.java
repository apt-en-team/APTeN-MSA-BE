package com.apten.parkingvehicle.application.service;

import com.apten.common.enums.ParkingType;
import com.apten.parkingvehicle.application.model.event.ZoneCounterChangedEvent;
import com.apten.parkingvehicle.domain.entity.ParkingSensor;
import com.apten.parkingvehicle.domain.entity.ParkingSetting;
import com.apten.parkingvehicle.domain.entity.ParkingZone;
import com.apten.parkingvehicle.domain.enums.SensorStatus;
import com.apten.parkingvehicle.domain.repository.ParkingSensorRepository;
import com.apten.parkingvehicle.domain.repository.ParkingSettingRepository;
import com.apten.parkingvehicle.domain.repository.ParkingZoneRepository;
import com.apten.parkingvehicle.infrastructure.redis.SensorStatusRepository;
import com.apten.parkingvehicle.infrastructure.redis.ZoneCounterChangePublisher;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// SENSOR 단지 zone 점유 카운터를 원본(활성 OCCUPIED 센서 수) 기준으로 재동기화하는 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class ZoneCounterReconcileService {

    // 재동기화 대상 단지(SENSOR) 선별용 설정 저장소
    private final ParkingSettingRepository parkingSettingRepository;

    // 활성 zone 목록 조회용 저장소
    private final ParkingZoneRepository parkingZoneRepository;

    // zone별 활성 센서 목록 조회용 저장소
    private final ParkingSensorRepository parkingSensorRepository;

    // zone 카운터 조회·보정 Redis 저장소
    private final SensorStatusRepository sensorStatusRepository;

    // 보정값 SSE 발행기
    private final ZoneCounterChangePublisher zoneCounterChangePublisher;

    // SENSOR 단지 전체의 zone 카운터를 재계산해 보정하고 변경분을 SSE로 발행한다. 보정 발생 zone 수 반환
    @Transactional(readOnly = true)
    public int reconcileAll() {
        List<ParkingSetting> sensorSettings = parkingSettingRepository.findByParkingType(ParkingType.SENSOR);
        int reconciledZones = 0;
        for (ParkingSetting setting : sensorSettings) {
            List<ParkingZone> activeZones = parkingZoneRepository.findByComplexIdAndIsActiveTrue(setting.getComplexId());
            for (ParkingZone zone : activeZones) {
                if (reconcileZone(setting.getComplexId(), zone)) {
                    reconciledZones++;
                }
            }
        }
        return reconciledZones;
    }

    // 단일 zone 점유 카운터를 활성 OCCUPIED 센서 수로 보정하고 표시값 변경 시 SSE 발행 (표시값 변경 시 true)
    private boolean reconcileZone(Long complexId, ParkingZone zone) {
        List<ParkingSensor> activeSensors = parkingSensorRepository
                .findByZoneIdAndIsActiveTrueAndIsDeletedFalse(zone.getId());
        List<String> sensorCodes = activeSensors.stream()
                .map(ParkingSensor::getSensorCode)
                .toList();

        // 활성 자리 점유 상태를 일괄 조회해 OCCUPIED 수를 실제 점유 수로 집계
        long occupied = sensorCodes.isEmpty()
                ? 0L
                : sensorStatusRepository.getStatusMap(sensorCodes).values().stream()
                        .filter(status -> status == SensorStatus.OCCUPIED)
                        .count();

        // 표시 기준 직전값 확보 (음수 클램프 적용된 값)
        long previous = sensorStatusRepository.getZoneOccupied(zone.getId());

        // 원본 기준값으로 카운터를 항상 정규화 (음수·드리프트 잔재 제거)
        sensorStatusRepository.setZoneOccupied(zone.getId(), occupied);

        // 표시값이 동일하면 SSE 발행 생략 (음수 잔재만 정규화하고 종료)
        if (previous == occupied) {
            return false;
        }

        // 표시값이 바뀐 경우에만 정합화된 값을 SSE로 발행
        zoneCounterChangePublisher.publish(ZoneCounterChangedEvent.builder()
                .complexId(complexId)
                .zoneId(zone.getId())
                .zoneOccupied((int) occupied)
                .zoneTotalSlots(sensorCodes.size())
                .changedAt(LocalDateTime.now())
                .build());
        log.info("[zone-counter-reconcile] zoneId={}, before={}, after={}", zone.getId(), previous, occupied);
        return true;
    }
}
