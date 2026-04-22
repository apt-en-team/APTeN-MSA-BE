# APTeN-MSA-BE

## 프로젝트 상태

현재 MSA 전환을 위한 전체 서비스 기본 구조 세팅이 완료된 상태.

## 완료된 범위

다음 서비스에 대해 공통 구조를 통일하여 구성완료.

- auth-service
- apartment-complex-service
- household-service
- board-service
- facility-reservation-service
- parking-vehicle-service
- notification-service

각 서비스는 다음 항목까지 구현 완료.

- Controller (API 시그니처 정의)
- Service (메서드 시그니처 + TODO)
- Request / Response DTO
- Entity (JPA 매핑 완료)
- Enum 분리
- Mapper 인터페이스 (placeholder)
- build 성공 확인

## 현재 상태

- 비즈니스 로직 미구현
- DB 연결 미구현
- Query / Repository 미구현
- Kafka 실제 로직 미구현

현재는 각 서비스에서 로직 구현을 바로 시작할 수 있는 상태.

## 설계 기준

### MSA 원칙
- 서비스 간 직접 참조 금지
- FK 대신 Long ID 기반 참조 사용
- Kafka 기반 캐시 구조 사용

### Entity 규칙
- JPA 연관관계 사용 금지 (@ManyToOne, @OneToMany 등)
- 단순 ID 필드로만 참조 유지
- enum은 EnumType.STRING 사용

### 구조 기준
- Controller → Service → Repository/Mapper 구조
- Service는 도메인 기준으로 분리

## 개발 가이드

### 가능
- DTO 필드 추가
- Entity 컬럼 보완
- enum 값 추가
- Service 내부 로직 구현
- Repository / Query Mapper 구현

### 주의
- 공통 상태값 변경 금지
- 이벤트 payload 구조 변경 금지
- API 경로 임의 변경 금지
- 타 서비스와 연결된 필드 변경 시 공유 필요

## 작업 방법

1. develop 브랜치 최신화
2. feature 브랜치 생성
3. 각 서비스 로직 구현
4. PR 생성 후 리뷰 진행

## 한 줄 정리

전체 서비스 골격은 완료되었으며, 각 서비스의 비즈니스 로직 구현 단계입니다.
