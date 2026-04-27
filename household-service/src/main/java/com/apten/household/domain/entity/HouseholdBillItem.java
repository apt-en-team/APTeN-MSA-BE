package com.apten.household.domain.entity;

import com.apten.common.entity.BaseEntity;
import com.apten.household.domain.enums.HouseholdBillItemType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 세대 청구 상세 항목을 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "household_bill_item",
        indexes = {
                @Index(name = "idx_household_bill_item_bill_id", columnList = "bill_id"),
                @Index(name = "idx_household_bill_item_type", columnList = "item_type")
        }
)
public class HouseholdBillItem extends BaseEntity {

    // 상세 항목 PK
    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    // 청구 ID
    @Column(name = "bill_id", nullable = false)
    private Long billId;

    // 항목 유형
    @Column(name = "item_type", nullable = false, length = 30)
    private HouseholdBillItemType itemType;

    // 항목명
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    // 항목 금액
    @Builder.Default
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    // 계산 기준 메모
    @Column(name = "calc_memo", length = 255)
    private String calcMemo;

    // 청구 상세 항목 내용을 수정한다
    public void apply(HouseholdBillItemType itemType, String itemName, BigDecimal amount, String calcMemo) {
        this.itemType = itemType;
        this.itemName = itemName;
        this.amount = amount;
        this.calcMemo = calcMemo;
    }
}
