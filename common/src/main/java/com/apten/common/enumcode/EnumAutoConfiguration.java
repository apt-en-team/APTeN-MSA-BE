package com.apten.common.enumcode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

// com.apten 기준 enum과 추가 scan package의 enum code/value 목록을 자동 등록한다.
@Configuration
public class EnumAutoConfiguration {

    // 기본 com.apten 패키지 외에 추가로 스캔할 enum package 목록이다.
    @Value("${constants.enum.scan-package:}")
    private String scanPackage;

    // 애플리케이션 시작 시 enum 목록을 스캔해 EnumMapper bean으로 제공한다.
    @Bean
    public EnumMapper enumMapper() {
        List<String> basePackages = resolveBasePackages();
        return new EnumMapperScanner().scan(basePackages);
    }

    // 기본 com.apten 패키지와 설정으로 들어온 추가 패키지를 하나의 목록으로 합친다.
    private List<String> resolveBasePackages() {
        if (scanPackage == null || scanPackage.isBlank()) {
            return List.of("com.apten");
        }
        List<String> extraPackages = Arrays.stream(scanPackage.split(","))
                .map(String::trim)
                .filter(packageName -> !packageName.isBlank())
                .toList();
        return java.util.stream.Stream.concat(List.of("com.apten").stream(), extraPackages.stream())
                .distinct()
                .toList();
    }
}
