package com.apten.auth.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

// Application 클래스에 @EnableJpaRepositories가 직접 선언되어 @WebMvcTest 사용 불가
// Spring 컨텍스트 없이 standaloneSetup + 순수 Mockito로 구성
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
abstract class AuthDocsTestBase {

    protected MockMvc mockMvc;
    protected final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
}
