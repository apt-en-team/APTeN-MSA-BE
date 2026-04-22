package com.apten.apartmentcomplex.domain.entity;

import com.apten.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// apartment-complex-service의 단지 집합 루트를 대표하는 최소 엔티티
// BaseEntity 상속 규칙과 공통 시간 필드 사용 규칙을 이 파일에서 시작한다
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ApartmentComplex extends BaseEntity {

    // 다음 단계에서 TSID 규칙으로 확장할 기본 식별자
    @Id
    private Long id;
}
