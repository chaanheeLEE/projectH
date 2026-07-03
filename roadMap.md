***

## 전체 구조

| 단계 | 기간 | 핵심 목표 | 산출물 |
|---|---|---|---|
| **1단계** | 1~3주 | DDD 설계 + 커머스 코어 구현 | 컨텍스트 맵, 멀티모듈 구조, 상품/장바구니/주문 API |
| **2단계** | 4~6주 | 결제 + 정산 배치 | 결제 상태 머신, PG 연동, Spring Batch 정산 파이프라인 |
| **3단계** | 7~9주 | 검색 + MSA 전환 | Elasticsearch 검색 서비스, Kafka 도메인 분리, API Gateway |
| **4단계** | 10~12주 | 배포 인프라 + 운영 관측성 | GitHub Actions CI/CD, K8s 무중단 배포, Prometheus/Grafana |

***

## 1단계 (1~3주): DDD 설계와 커머스 코어

DDD 학습과 실제 코드 작성을 병행하는 가장 중요한 구간입니다. 서브도메인 분리와 유비쿼터스 언어를 먼저 정의해야 이후 설계가 흔들리지 않습니다.

**1주 — DDD 이론 + 도메인 모델링**
- 에릭 에반스 DDD 핵심 개념 정리: Entity, VO, Aggregate, Repository, Domain Service
- 서브도메인 분리: 상품/장바구니/주문/결제/정산/회원/검색
- Bounded Context 경계 확정 + 컨텍스트 맵 작성
- 유비쿼터스 언어 용어 사전 작성 (예: "결제 승인 완료" vs "주문 확정"의 차이)
- 산출물: 컨텍스트 맵 다이어그램, 용어 사전 문서

**2주 — Spring Multi-Module 구조 + 상품/장바구니**
- 멀티모듈 프로젝트 설계: `domain`, `application`, `infrastructure`, `api` 레이어 분리
- 상품 도메인: 상품 Entity, 카테고리 VO, 재고 VO, 판매상태 전이
- 장바구니 도메인: 장바구니 Aggregate, 아이템 추가/제거, 가격 스냅샷 정책
- JPA 매핑: Entity ↔ Domain 객체 분리 원칙 적용
- 산출물: 멀티모듈 프로젝트 초기 구조, 상품/장바구니 REST API

**3주 — 주문 도메인 + 도메인 이벤트**
- 주문 도메인: 주문 Aggregate, 주문항목 VO, 주문상태 전이 다이어그램 작성
- Spring ApplicationEvent로 내부 이벤트 발행: `OrderPlacedEvent`, `StockDeductedEvent`
- 트랜잭션 경계 설계: 주문 생성 → 재고 차감 → 결제 요청 흐름의 보상 트랜잭션 설계
- 낙관적 락을 이용한 재고 동시성 제어
- 산출물: 주문 API, 이벤트 흐름도, 상태 전이 다이어그램

***

## 2단계 (4~6주): 결제 + 정산 배치

가장 난이도 높은 구간입니다. 결제는 외부 PG 호출이 포함되어 로컬 트랜잭션만으로 안전을 보장할 수 없고, 정산은 대용량 데이터 처리 + 정확성이 핵심입니다.

**4주 — PG 결제 연동 + 결제 상태 머신**
- 포트원(구 아임포트) 테스트 계정 생성, PG 연동 흐름 파악
- 결제 상태 머신 설계: `INITIATED → PG_REQUESTED → APPROVED → COMPLETED / FAILED / CANCELLED`
- Idempotency Key 설계: 중복 결제 방지, PG 재시도 안전성 확보
- 결제 완료 콜백 검증 로직: PG 서버 측 금액/상품 재검증
- 산출물: 결제 상태 전이 다이어그램, 결제 도메인 API

**5주 — 결제 보안 + 데이터 보호**
- 민감 데이터 암호화: 카드 마지막 4자리, 주문자 이름 암호화 저장
- 감사 로그: 결제 시도/승인/실패 모든 이력 append-only로 기록
- Spring Security + 역할 기반 접근 제어 (관리자 정산 API 분리)
- 결제 실패 시 보상 흐름: 주문 취소 이벤트 발행 → 재고 복구
- 산출물: 결제 이력 API, 감사 로그 테이블 설계

**6주 — Spring Batch 정산 파이프라인**
- 정산 도메인 설계: 정산대상, 수수료 정책(정률/정액), 정산주기, 정산상태 
- Spring Batch Job 설계: `ItemReader(결제 완료 건 조회)` → `ItemProcessor(수수료 계산)` → `ItemWriter(정산 원장 저장)`
- Chunk 크기 튜닝, Skip/Retry 정책 설정, Job 재시작 가능성 설계
- 정산 오류 처리: 실패 건 Dead Letter 처리, 정산 로그 기록
- 산출물: 정산 배치 Job/Step 설계 문서, 정산 API

***

## 3단계 (7~9주): 검색 + MSA 전환

기술 범위가 가장 넓은 구간입니다. Elasticsearch와 Kafka는 로컬에서 Docker Compose로 먼저 띄우고 연동부터 시작해야 학습 속도가 빠릅니다.

**7주 — Elasticsearch 검색 서비스**
- Docker Compose로 Elasticsearch + Kibana 로컬 환경 구성
- 상품 인덱스 설계: 한국어 형태소 analyzer, 동의어 사전(synonym filter), ngram 자동완성
- 상품 색인 파이프라인: 상품 생성/수정 이벤트 발행 → 검색 서비스 색인
- 키워드 랭킹: 검색어 로그 집계 → 실시간 인기 검색어
- 산출물: 검색 마이크로서비스, 자동완성/랭킹 API, 인덱스 설계 문서

**8주 — Kafka 기반 MSA 분리**
- Docker Compose로 Kafka 클러스터 로컬 구성
- 회원/인증 서비스 분리: 커머스 서비스에서 JWT 검증을 auth-service로 위임
- Kafka 토픽 설계: `order.placed`, `payment.completed`, `stock.deducted`, `settlement.ready`
- Consumer Group 설계, 메시지 스키마(Avro 또는 JSON), 파티션 전략
- 장애 대응: Dead Letter Queue, 멱등 소비자 패턴
- 산출물: Kafka 토픽 설계 문서, 서비스 간 비동기 흐름도

**9주 — API Gateway + Service Discovery**
- Spring Cloud Gateway 설정: 라우팅, 인증 필터, Rate Limiting
- Eureka 또는 Kubernetes DNS 기반 Service Discovery 선택
- 분산 추적: Spring Cloud Sleuth + Zipkin (또는 OpenTelemetry) 연동
- 서비스 간 장애 격리: Circuit Breaker (Resilience4j) 적용
- 산출물: API Gateway 설정, 서비스 아키텍처 다이어그램

***

## 4단계 (10~12주): 배포 인프라 + 운영 관측성

찬희님이 기존에 AWS·EC2·Docker·GitHub Actions 경험이 있어서 진입은 빠르지만, Kubernetes와 Prometheus 스택은 처음부터 천천히 쌓아야 합니다.

**10주 — Docker 멀티서비스 + GitHub Actions CI/CD**
- 각 서비스 Dockerfile 최적화: 멀티스테이지 빌드, 레이어 캐싱
- GitHub Actions 파이프라인: 빌드 → 테스트 → 이미지 빌드 → ECR 푸시 → 배포
- 환경별 분리: dev/staging/prod 프로파일, Secret 관리 (GitHub Secrets + AWS Secrets Manager)
- 산출물: GitHub Actions workflow 파일, 배포 자동화 파이프라인

**11주 — Kubernetes 배포 + 무중단 배포**
- Kubernetes 리소스 설계: Deployment, Service, Ingress, ConfigMap, Secret, HPA
- 무중단 배포 전략: Rolling Update + Readiness/Liveness Probe 설정
- Helm Chart 또는 Kustomize로 환경별 배포 관리
- 데이터베이스 마이그레이션 전략: Flyway + K8s Job으로 안전한 스키마 변경
- 산출물: K8s 매니페스트, 배포 전략 문서

**12주 — 모니터링 + 문서화**
- Prometheus + Grafana 구성: 서비스 메트릭, JVM 메트릭, Kafka Consumer Lag
- Loki 또는 ELK 기반 로그 수집 파이프라인
- Grafana 대시보드: 주문 처리량, 결제 성공률, 정산 배치 실행 현황, 검색 응답시간
- 알림 설정: Grafana Alerting으로 이상 지표 Slack 연동
- README 및 아키텍처 문서 최종 정리, 포트폴리오 정리
- 산출물: Grafana 대시보드 캡처, 시스템 아키텍처 문서, 포트폴리오 README

***

## 주차별 체크포인트

각 주 마지막에 아래 3가지를 확인하면 진도를 잃지 않습니다.

- **코드**: 이번 주 기능이 로컬에서 E2E로 동작하는가
- **문서**: 설계 결정 이유가 ADR(Architecture Decision Record) 또는 README에 기록됐는가
- **테스트**: 핵심 비즈니스 로직에 단위 테스트가 있는가

포트폴리오 관점에서 기술 구현보다 "왜 이 구조를 선택했는가"를 문서로 남기는 것이 취업 면접에서 가장 큰 차별점이 됩니다. 특히 결제 상태 머신, 정산 재처리 전략, 검색 정합성 허용 범위 같은 선택지 설명은 주니어 개발자 면접에서 바로 활용할 수 있는 내용이 됩니다.