# APTeN-MSA-BE

> 아파트 통합 주거관리 플랫폼 — 백엔드 (MSA)

입주민과 관리자가 함께 사용하는 스마트 아파트 통합 관리 시스템입니다.  
1차 프로젝트(Spring Boot 단일 서버)를 도메인별 서비스로 분리하고, Gateway·Kafka·Redis·WebSocket·FCM을 적용해 MSA 구조로 확장했습니다.

---

## 배포 링크

| 구분 | 링크 |
|---|---|
| 사용자 / 관리자 | [APTeN 바로가기](https://tc.greenart.n-e.kr) |
| 마스터 관리자 | [마스터 로그인](https://tc.greenart.n-e.kr/admin/master) |

### 테스트 계정

| 구분 | 이메일 | 비밀번호 |
|---|---|---|
| 관리자 | manager33@apten.com | test1234! |
| 입주민 | resident31@apten.com | test1234! |
| 마스터 | master@apten.com | master1234! |

---

## 팀원 및 담당 도메인

| 이름 | 담당 |
|---|---|
| 박소영 | 공통 모듈, Kafka Outbox, 단지 관리, 시설예약, GX, 알림 |
| 김가은 | 인증/인가, OAuth2(Google·Kakao·Naver), Gateway, 차량·방문차량, 주차 |
| 손지혜 | 세대 관리, 동/호수 등록, 입주민 자동·수동 매칭, 관리비 |
| 이윤주 | 게시판, 공지사항, 댓글, 투표, 파일 업로드 |

---

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 3.3.5, Spring Cloud 2023.0.3 |
| Gateway | Spring Cloud Gateway (WebFlux) |
| Security | Spring Security, JWT (jjwt 0.12.6) |
| ORM | Spring Data JPA (Hibernate), MyBatis |
| DB | MariaDB, Redis |
| Message Broker | Apache Kafka |
| 분산 락 | ShedLock + Redis |
| 실시간 알림 | WebSocket, FCM (firebase-admin 9.4.3) |
| SMS | CoolSMS (nurigo SDK 4.3.0) |
| PK 전략 | TSID (hypersistence-utils-hibernate-63 3.7.0) |
| 빌드 | Gradle 멀티 모듈 |

---

## 서비스 구성

| 서비스 | 포트 | 역할 |
|---|---|---|
| gateway-service | 9000 | API Gateway, JWT 검증, 라우팅, Redis 블랙리스트 |
| auth-service | 9080 | 인증/인가, OAuth2(Google·Kakao·Naver), SMS, 이메일 |
| notification-service | 9081 | 알림 저장, FCM, WebSocket |
| apartment-complex-service | 9082 | 단지 정보, 기능 설정, 관리자 계정 연동 |
| parking-vehicle-service | 9083 | 차량·방문차량 관리, 주차 센서, SSE 실시간 |
| household-service | 9084 | 세대 관리, 관리비 스케줄러 |
| board-service | 9085 | 게시판, 공지, 투표, 파일 업로드 |
| facility-reservation-service | 9086 | 시설 예약, GX 신청/승인, Redis 좌석 임시선점 |
| common | — | 공통 라이브러리 (Outbox, Kafka, Security, 응답/예외) |

---

## 아키텍처

**인증 흐름**
```
클라이언트 → Gateway (JWT 검증 + Redis 블랙리스트)
  → X-User-Id / X-User-Role / X-Complex-Id 헤더 전달
  → 각 도메인 서비스 (헤더 기반 사용자 컨텍스트 복원)
```
MASTER는 여러 단지를 오가며 작업하므로 `X-Selected-Complex-Id` 전용 헤더를 별도로 전달합니다.

**Kafka + Outbox 패턴**  
도메인 로직과 Outbox 행 저장을 같은 트랜잭션에서 처리해 이벤트 유실을 방지합니다.  
OutboxRelay가 10초 주기로 INIT 상태 이벤트를 Kafka로 발행합니다.

**Database per Service**  
각 서비스는 독립 DB를 사용하며, 타 서비스 데이터는 Kafka 이벤트로 동기화된 로컬 Cache Table을 통해 조회합니다.

**단지별 기능 토글**  
시설예약·주차현황·전자투표 ON/OFF는 `complex_feature_cache`로 관리하며, 설정 변경 시 Kafka 이벤트로 각 서비스 캐시를 갱신합니다.

---

## 핵심 구현 포인트

**박소영 — 시설예약 / 알림**
- Redis SET NX 기반 좌석 임시선점으로 동시 중복 선택 방지, DB TempHold와 이중 관리
- GX 대기순번 직렬화: 낙관적 락·Redis 분산락 비교 후 JPA PESSIMISTIC_WRITE 락 선택, 취소 시 waitNo 자동 재정렬
- 알림 DB 선저장 후 WebSocket·FCM 발송, FCM 장애 격리로 인앱 알림 정상 보존

**김가은 — 인증 / 주차**
- OAuth2(Google·Kakao·Naver) 소셜 로그인, JWT 발급·갱신·블랙리스트 처리
- Redis Pub/Sub + SSE fan-out으로 주차 센서 상태 변화를 연결된 모든 클라이언트에 실시간 전달
- ShedLock + Redis로 센서 스케줄러 다중 인스턴스 중복 실행 차단

**손지혜 — 세대 / 관리비**
- 입주예정자 명부 기반 세대 자동 매칭, 정보 불일치 시 관리자 수동 승인 대기 흐름
- 차량·방문차량·시설 비용을 Kafka 이벤트로 수신해 세대별 관리비에 통합 집계

**이윤주 — 게시판 / 투표**
- Tiptap 에디터 기반 리치 텍스트 게시글, 이미지·파일 첨부 지원
- 투표 세대 단위 중복 참여 방지 (세대주 1인 1표 정책 적용)
