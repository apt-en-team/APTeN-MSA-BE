# APTeN-MSA-BE

## 프로젝트 소개

APTeN은 입주민과 관리자가 함께 사용하는 스마트 아파트 통합 관리 시스템입니다.

1차 프로젝트에서는 Spring Boot 단일 서버 구조로 방문차량, 주차, 시설예약, 게시판, 세대 관리 기능을 구현했습니다.
2차 프로젝트에서는 기존 기능을 도메인별 서비스로 분리하고, Gateway, Kafka, Redis, WebSocket, FCM 등을 적용하여 MSA 구조로 확장했습니다.

입주민은 차량 등록, 방문차량 등록, 시설 예약, 게시판 이용, 알림 확인 등을 사용할 수 있고, 관리자는 세대 승인, 차량 승인, 시설/GX 프로그램 관리, 주차 현황 확인, 공지사항 관리 등을 수행할 수 있습니다.

---

## 배포 링크

| 사용자/관리자                                   | 마스터 관리자                                            |
| ----------------------------------------- | -------------------------------------------------- |
| [APTeN 바로가기](https://tc.greenart.n-e.kr/) | [마스터 로그인](https://tc.greenart.n-e.kr/master/login) |

### 테스트 계정

| 구분  | 이메일                                                 | 비밀번호        |
| --- | --------------------------------------------------- | ----------- |
| 관리자 | [manager33@apten.com](mailto:manager11@apten.com)   | test1234!   |
| 입주민 | [resident31@apten.com](mailto:resident11@apten.com) | test1234!   |
| 마스터 | [master@apten.com](mailto:master@apten.com)         | master1234! |

---

## 주요 기능

### 공통 / Gateway

* 클라이언트 요청을 서비스별 API로 라우팅
* JWT 검증 및 인증 예외 경로 관리
* 인증된 사용자 정보를 내부 서비스로 전달

  * `X-User-Id`
  * `X-Role`
  * `X-Complex-Id`
* Redis 기반 토큰 블랙리스트 관리
* 서비스별 API 경로 통합 관리

### 인증 / 사용자

* 일반 로그인 및 회원가입
* JWT 기반 인증/인가
* OAuth2 소셜 로그인

  * Google
  * Kakao
  * Naver
* 이메일 인증 및 비밀번호 재설정
* SMS 인증
* 관리자 계정 연동 API 제공

### 단지 / 세대

* 아파트 단지 정보 관리
* 입주민 소속 단지 검증
* 세대 등록, 조회, 승인/거절
* 세대원 관리
* 관리비 스케줄러 처리
* 세대/세대원 정보 변경 이벤트 발행

### 차량 / 주차

* 입주민 차량 등록 및 승인
* 방문차량 등록 및 관리
* 주차 구역 관리
* 센서 기반 주차 상태 처리
* SSE 기반 실시간 주차 현황 제공
* Redis를 활용한 센서 상태 및 주차 구역 카운터 캐시
* 주차 구역 카운터 재동기화 스케줄러 처리

### 시설 예약

* 시설 목록 조회 및 예약
* 좌석형 시설 예약 처리
* Redis 기반 좌석 임시 선점
* 임시 선점 TTL 만료 처리
* GX 프로그램 신청 및 관리자 승인/거절
* 예약/GX 상태 자동 완료 스케줄러
* DB Unique 제약과 Redis Key를 활용한 중복 예약 방지

### 알림

* 알림 DB 저장
* 알림 목록 조회
* 미읽음 알림 수 조회
* 알림 읽음 처리
* WebSocket 기반 인앱 알림
* Firebase FCM 기반 웹 푸시 알림
* FCM 토큰 자동 비활성화 및 정리 스케줄러
* 관리자 알림 이벤트 구조 정의

### 게시판

* 공지사항 관리
* 자유게시판
* 댓글
* 투표
* 파일 업로드
* 게시글/댓글/투표 관련 이벤트 발행

---

## 아키텍처

```text
Client
  |
  v
Spring Cloud Gateway
  |
  +-- auth-service
  +-- notification-service
  +-- apartment-complex-service
  +-- parking-vehicle-service
  +-- household-service
  +-- board-service
  +-- facility-reservation-service
  |
  +-- common module

Kafka
  |
  +-- 서비스 간 이벤트 발행/구독
  +-- 캐시 동기화
  +-- 알림 요청 이벤트
  +-- Outbox 기반 이벤트 발행 안정화

Redis
  |
  +-- JWT 블랙리스트
  +-- 좌석 임시 선점 TTL
  +-- 주차 센서 상태 캐시
  +-- ShedLock 저장소

MariaDB
  |
  +-- 서비스별 도메인 데이터 저장
```

---

## 서비스 구성

| 서비스                          |   포트 | 역할                                         |
| ---------------------------- | ---: | ------------------------------------------ |
| gateway-service              | 9000 | API Gateway, JWT 검증, 라우팅, Redis 블랙리스트      |
| auth-service                 | 9080 | 인증/인가, OAuth2, SMS, 이메일                    |
| notification-service         | 9081 | 알림 저장, FCM, WebSocket                      |
| apartment-complex-service    | 9082 | 단지 정보, 입주민 검증, 관리자 계정 연동                   |
| parking-vehicle-service      | 9083 | 차량/방문차량 관리, 주차 센서, SSE 실시간 주차 현황           |
| household-service            | 9084 | 세대 관리, 관리비 스케줄러, ShedLock                  |
| board-service                | 9085 | 게시판, 공지사항, 투표, 파일 업로드                      |
| facility-reservation-service | 9086 | 시설 예약, GX 신청/승인, Redis 좌석 임시선점             |
| common                       |    - | 공통 응답, 예외, Security Context, Kafka, Outbox |

---

## 기술 스택

### Backend

* Java 21
* Spring Boot 3.3.5
* Spring Cloud 2023.0.3
* Spring Security
* Spring Cloud Gateway
* Spring Data JPA
* MyBatis
* Gradle Multi Module

### Database / Infra

* MariaDB
* Redis
* Apache Kafka
* Firebase Cloud Messaging
* CoolSMS
* ShedLock
* TSID

### Communication

* REST API
* Kafka Event
* WebSocket
* SSE
* RestClient
* FCM Push

---

## Gateway 라우팅

| 경로                                                                        | 대상 서비스                       |
| ------------------------------------------------------------------------- | ---------------------------- |
| `/api/auth/**`, `/login/**`, `/oauth2/**`                                 | auth-service                 |
| `/api/admin/apartment-complexes/**`, `/api/resident/apartment-complex/**` | apartment-complex-service    |
| `/api/vehicles/**`, `/parking-vehicles/**`                                | parking-vehicle-service      |
| `/api/facilities/**`, `/api/reservations/**`, `/api/gx-programs/**`       | facility-reservation-service |
| `/boards/**`, `/notices/**`                                               | board-service                |
| `/api/notifications/**`, `/ws/notifications/**`                           | notification-service         |

Gateway에서 JWT를 검증한 뒤 내부 서비스로 사용자 정보를 헤더로 전달합니다.

```text
Client Request
  → Gateway JWT 검증
  → X-User-Id / X-Role / X-Complex-Id 헤더 추가
  → 내부 서비스 라우팅
```

---

## Kafka 이벤트 구조

서비스 간 직접 의존도를 줄이기 위해 Kafka 기반 이벤트 구조를 적용했습니다.

공통 이벤트 구조는 `EventEnvelope`로 관리합니다.

```text
EventEnvelope
├── eventId
├── eventType
├── version
├── occurredAt
├── producer
└── payload
```

### 주요 토픽

#### 캐시 동기화

* `user.v1`
* `apartment-complex.v1`
* `household.v1`
* `household-member.v1`
* `vehicle.v1`
* `facility-usage.v1`
* `visitor-usage.v1`

#### 도메인 이벤트

* `vehicle.status.changed`
* `facility.fee.calculated`
* `notice.created`
* `post.created`
* `comment.created`
* `vote.created`
* `vote.closed`
* `notification-request.v1`

---

## Outbox 패턴

DB 저장과 Kafka 발행 사이에서 이벤트 유실이 발생하지 않도록 Outbox 패턴을 적용했습니다.

일반적으로 아래와 같이 DB 저장과 Kafka 발행을 분리하면, DB 저장은 성공했지만 Kafka 발행은 실패하는 문제가 발생할 수 있습니다.

```java
reservationRepository.save(reservation);
kafkaTemplate.send(topic, payload);
```

이를 방지하기 위해 비즈니스 데이터 저장과 Outbox 이벤트 저장을 하나의 트랜잭션으로 처리했습니다.

```text
1. 도메인 로직 처리
2. 같은 트랜잭션에서 Outbox 이벤트 저장
3. OutboxRelay가 INIT 상태 이벤트 조회
4. PROCESSING 상태로 변경
5. Kafka 발행
6. 성공 시 삭제, 실패 시 FAILED 처리
7. 장시간 PROCESSING 상태인 이벤트는 INIT으로 복구
```

### 적용 서비스

* auth-service
* apartment-complex-service
* household-service
* parking-vehicle-service
* facility-reservation-service
* board-service

### OutboxRelay 동작

* 10초마다 `INIT` 상태 이벤트 최대 100개 조회
* 발행 전 `PROCESSING` 상태로 변경하여 중복 발행 방지
* 발행 성공 시 Outbox 행 삭제
* 발행 실패 시 `FAILED` 상태 처리
* 2분 이상 `PROCESSING` 상태인 이벤트는 `INIT`으로 복구

---

## 시설 예약 핵심 설계

### GX 신청/승인 흐름

```text
관리자 GX 프로그램 생성
  → 입주민 GX 신청
  → PENDING 상태 및 대기번호 부여
  → 관리자 승인/거절
  → APPROVED / REJECTED 상태 변경
  → 알림 발송
```

GX 예약은 승인 대기 상태로 신청되며, 관리자가 승인 또는 거절할 수 있습니다.

또한 스케줄러를 통해 종료 시간이 지난 GX 예약은 자동 완료 처리됩니다.

### 좌석 임시 선점

좌석형 시설은 사용자가 예약을 완료하기 전까지 Redis TTL을 활용해 임시 선점 상태를 관리합니다.

```text
좌석 선택
  → ReservationTempHold 저장
  → Redis Key 생성
  → TTL 5분 설정
  → 결제/예약 완료 시 CONFIRMED
  → TTL 만료 시 EXPIRED
```

동일 좌석, 동일 시간대 중복 선점을 막기 위해 Redis Key와 DB Unique 제약을 함께 사용했습니다.

```text
complexId:facilityId:seatId:reservationDate:timeRange
```

### 동시성 제어

* Redis Key 기반 동일 좌석/시간 중복 선점 방지
* DB Unique Index 기반 최종 중복 예약 방지
* ShedLock 기반 스케줄러 중복 실행 방지

---

## 실시간 주차 현황

주차 상태는 SSE를 통해 실시간으로 전달됩니다.

```text
Parking Sensor
  → Redis 상태 저장
  → Zone Counter 갱신
  → SSE Event 전송
  → Client 실시간 반영
```

### 주요 처리

* `SseEmitterRegistry`에서 단지별 연결 관리
* Heartbeat 30초 주기 전송
* 연결 타임아웃 30분 설정
* 센서 모의 스케줄러 7초 주기 실행
* Zone 재동기화 스케줄러 30초 주기 실행
* 센서 상태와 Zone 카운터 불일치 시 보정

---

## 알림 구조

알림은 DB 저장, WebSocket, FCM을 함께 사용합니다.

```text
도메인 서비스
  → notification-request.v1 이벤트 또는 내부 요청
  → notification-service
  → DB 저장
  → WebSocket 인앱 알림
  → FCM 웹 푸시 알림
```

### 주요 처리

* 알림 목록 조회
* 미읽음 알림 수 조회
* 단건 읽음 처리
* 전체 읽음 처리
* WebSocket 실시간 알림
* FCM 토큰 등록/갱신/비활성화
* 사용자당 토큰 2개 초과 시 오래된 토큰 자동 비활성화
* 매일 03:00 비활성 토큰 정리

---

## 공통 모듈

`common` 모듈에는 여러 서비스에서 반복적으로 사용하는 기능을 분리했습니다.

```text
common/src/main/java/com/apten/common
├── outbox
├── kafka
├── security
├── entity
├── exception
├── response
├── constants
├── enumcode
├── enums
└── config
```

### 주요 역할

* 공통 응답 형식
* 공통 예외 처리
* 공통 Kafka Topic / EventType 관리
* EventEnvelope 구조 관리
* Outbox 엔티티 및 Relay 제공
* UserContext / UserContextHolder 관리
* 공통 Header 상수 관리
* BaseEntity 제공

---

## 트러블슈팅

### 1. SSE 연결 중 DB 세션 점유 문제

SSE 연결은 장시간 유지되는 특성이 있습니다.
OSIV가 활성화된 상태에서는 SSE 연결 중 DB 세션이 불필요하게 유지될 수 있었습니다.

이를 해결하기 위해 `open-in-view: false` 설정을 적용하여 요청 처리 이후 DB 세션이 계속 점유되지 않도록 개선했습니다.

---

### 2. Outbox 트랜잭션 프록시 미동작 문제

Outbox 처리 과정에서 self-invocation으로 인해 트랜잭션 프록시가 적용되지 않는 문제가 발생했습니다.

이를 해결하기 위해 트랜잭션이 필요한 메서드를 별도 메서드로 분리하여 Spring AOP 프록시가 정상 동작하도록 수정했습니다.

---

### 3. 주차 Zone 카운터 음수 노출 문제

센서 이벤트 처리 과정에서 Zone 카운터가 음수로 내려가는 문제가 발생했습니다.

이를 해결하기 위해 음수 값 방어 로직을 추가하고, 센서 상태 기준으로 Zone 카운터를 주기적으로 재동기화하는 스케줄러를 강화했습니다.

---

### 4. FCM 토큰 중복으로 인한 다중 발송 문제

동일 사용자의 FCM 토큰이 여러 개 누적되면서 알림이 중복 발송되는 문제가 발생했습니다.

이를 해결하기 위해 사용자당 활성 토큰 수를 제한하고, 2개를 초과하는 오래된 토큰은 자동으로 비활성화하도록 처리했습니다.

---

### 5. Kafka 역직렬화 오류

서비스별 이벤트 구조가 일관되지 않아 Kafka 메시지 역직렬화 오류가 발생했습니다.

이를 해결하기 위해 공통 `EventEnvelope` 구조를 정의하고, 이벤트 메타데이터와 payload 구조를 일원화했습니다.

---

### 6. 좌석 임시 선점 UX 개선

좌석 임시 선점 시간이 길 경우 사용자가 이탈했을 때 좌석이 불필요하게 오래 점유되는 문제가 있었습니다.

이를 개선하기 위해 임시 선점 TTL을 10분에서 5분으로 단축하여 좌석 회전율과 사용자 경험을 개선했습니다.

---

## 실행 방법

### 1. 인프라 실행

```bash
docker compose up -d
```

실행 대상 예시:

* MariaDB
* Redis
* Kafka
* Zookeeper

### 2. 서비스 실행

```bash
./gradlew :gateway-service:bootRun
./gradlew :auth-service:bootRun
./gradlew :notification-service:bootRun
./gradlew :apartment-complex-service:bootRun
./gradlew :parking-vehicle-service:bootRun
./gradlew :household-service:bootRun
./gradlew :board-service:bootRun
./gradlew :facility-reservation-service:bootRun
```

### 3. 접속

```text
사용자/관리자: https://tc.greenart.n-e.kr/
마스터 관리자: https://tc.greenart.n-e.kr/master/login
```

---

## 미구현 / 개선 예정

아래 항목은 구조 정의 또는 일부 준비가 완료되었으나, 최종 구현이 완료되지 않았거나 개선 예정인 항목입니다.

* 예약 완료 비용 집계 연동
* 예약 완료 알림/이벤트 발행
* 차량 승인/거절 Kafka 발행 연결
* 관리자 알림 Consumer 구현
* Board 서비스의 공통 EventEnvelope 구조 통일
* apartment-complex-service 외부 호출 실패 시 보상 트랜잭션 전략
* OpenFeign 적용 범위 확장

---

## 팀원

| 이름  | 담당                                       |
| --- | ---------------------------------------- |
| 박소영 | 공통 모듈, Kafka Outbox, 단지/세대, 시설예약, 알림 |
| 김가은 | 인증/인가, OAuth2, Gateway, 차량/방문차량, 주차      |
| 손지혜 | 관리자 기능, 시설 관리, 차량 관리, 주차 관리 화면 연동        |
| 이윤주 | 게시판, 공지사항, 댓글, 투표, 입주민 화면 연동             |
