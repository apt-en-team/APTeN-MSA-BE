package com.apten.apartmentcomplex.domain.entity;

import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단지 원본 테이블을 표현하는 엔티티
// 단지 기본 정보는 이 서비스가 직접 관리한다
@Entity
@Table(
        name = "complex",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_complex_code", columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_complex_status", columnList = "status")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentComplex extends BaseEntity {

    // 단지 내부 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 외부 노출용 단지 코드
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    // 단지명
    @Column(name = "name", nullable = false)
    private String name;

    // 대표 주소
    @Column(name = "address", nullable = false)
    private String address;

    // 상세 주소
    @Column(name = "address_detail")
    private String addressDetail;

    // 우편번호
    @Column(name = "zip_code")
    private String zipCode;

    // 단지 상태는 converter를 통해 DB에는 code로 저장된다
    @Column(name = "status", nullable = false)
    private ApartmentComplexStatus status;

    // 단지 설명
    @Column(name = "description")
    private String description;

    // 단지 기본 정보를 수정할 때 사용한다
    public void update(
            String name,
            String address,
            String addressDetail,
            String zipCode,
            ApartmentComplexStatus status,
            String description
    ) {
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipCode = zipCode;
        this.status = status;
        this.description = description;
    }
}
