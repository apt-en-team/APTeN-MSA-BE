package com.apten.household.application.model.request;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HouseholdMatchBulkApproveReq {

    private List<Long> matchRequestIds;
}
