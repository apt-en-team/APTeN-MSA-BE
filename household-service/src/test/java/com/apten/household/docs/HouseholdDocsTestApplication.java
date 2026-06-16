package com.apten.household.docs;

import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableJpaRepositories 미포함 — docs 패키지에서 우선 감지되어 HouseholdServiceApplication 대체
// scanBasePackages 로 컨트롤러 패키지를 탐색하되, WebMvcTypeExcludeFilter가 HouseholdServiceApplication 제외
@SpringBootApplication(scanBasePackages = "com.apten.household")
class HouseholdDocsTestApplication {
}
