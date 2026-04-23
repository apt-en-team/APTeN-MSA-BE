package com.apten.common.enumcode;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.List;

// 지정한 패키지에서 EnumMapperType을 구현한 enum을 찾아 EnumMapper에 등록한다.
public class EnumMapperScanner {

    // enum 클래스 후보를 찾기 위해 Spring classpath scanner를 사용한다.
    private final ClassPathScanningCandidateComponentProvider scanner;

    // EnumMapperType 구현체만 후보로 찾도록 scanner를 초기화한다.
    public EnumMapperScanner() {
        this.scanner = new ClassPathScanningCandidateComponentProvider(false);
        this.scanner.addIncludeFilter(new AssignableTypeFilter(EnumMapperType.class));
    }

    // 여러 base package를 돌며 enum 목록을 찾아 EnumMapper에 등록한다.
    public EnumMapper scan(List<String> basePackages) {
        EnumMapper enumMapper = new EnumMapper();
        basePackages.forEach(basePackage -> scanPackage(basePackage, enumMapper));
        return enumMapper;
    }

    // 하나의 package에서 enum 타입만 골라 camelCase key로 등록한다.
    private void scanPackage(String basePackage, EnumMapper enumMapper) {
        scanner.findCandidateComponents(basePackage)
                .forEach(beanDefinition -> registerEnum(beanDefinition.getBeanClassName(), enumMapper));
    }

    // 클래스명을 실제 Class로 로딩한 뒤 enum이면 code/value 목록으로 변환해 등록한다.
    @SuppressWarnings("unchecked")
    private void registerEnum(String className, EnumMapper enumMapper) {
        try {
            Class<?> targetClass = Class.forName(className);
            if (!targetClass.isEnum() || !EnumMapperType.class.isAssignableFrom(targetClass)) {
                return;
            }

            Class<? extends EnumMapperType> enumClass = (Class<? extends EnumMapperType>) targetClass;
            String key = Introspector.decapitalize(enumClass.getSimpleName());
            enumMapper.put(key, toMapperValues(enumClass));
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("enum class를 로딩할 수 없습니다. className=" + className, exception);
        }
    }

    // enum 상수 배열을 FE가 읽기 쉬운 code/value 목록으로 바꾼다.
    private List<EnumMapperValue> toMapperValues(Class<? extends EnumMapperType> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(enumValue -> new EnumMapperValue(enumValue.getCode(), enumValue.getValue()))
                .toList();
    }
}
