# 📋 프로젝트 개발 투드리스트 & AI 지시사항 가이드

이 문서는 [roadMap.md](file:///C:/Users/dlcksgml/Desktop/CS/project/projectH/roadMap.md)를 바탕으로 구축된 **단계별/주차별 개발 투드리스트**, **핵심 학습 리스트**, 그리고 개발 진행 과정에서 발생하는 **시행착오 및 트레이드오프(Trade-off)를 체계적으로 기록하기 위한 AI용 지시사항**을 담고 있습니다.

---

## 1. 📅 단계별 개발 투드리스트 (Todo List)

### 1단계 (1~3주): DDD 설계와 커머스 코어
DDD(도메인 주도 설계) 학습과 코어 API 구현을 목표로 합니다.
- [x] **1주차: DDD 이론 + 도메인 모델링**
  - [x] 에릭 에반스 DDD 핵심 개념 정리 (Entity, VO, Aggregate, Repository, Domain Service)
  - [x] 서브도메인 분리 (상품, 장바구니, 주문, 결제, 정산, 회원, 검색)
  - [x] Bounded Context 경계 확정 및 컨텍스트 맵(Context Map) 작성
  - [x] 유비쿼터스 언어(Ubiquitous Language) 용어 사전 작성 (예: "결제 승인 완료" vs "주문 확정" 차이 정리)
  - [x] *산출물*: 컨텍스트 맵 다이어그램, 용어 사전 문서 완료
- [x] **2주차: Spring Multi-Module 구조 + 상품/장바구니 API**
  - [x] Spring 멀티모듈 구조 설계 (`domain`, `application`, `infrastructure`, `api` 레이어 분리)
  - [x] 상품 도메인 구현 (상품 Entity, 카테고리 VO, 재고 VO, 판매상태 전이 로직)
  - [x] 장바구니 도메인 구현 (장바구니 Aggregate, 아이템 추가/제거, 가격 스냅샷 정책)
  - [x] JPA 매핑 시 Entity ↔ Domain 객체 분리 원칙 적용
  - [x] *산출물*: 멀티모듈 프로젝트 초기 구조 코드, 상품/장바구니 REST API
- [ ] **3주차: 주문 도메인 + 도메인 이벤트 + API 유효성 검증**
  - [ ] 주문 도메인 구현 (주문 Aggregate, 주문항목 VO, 주문상태 전이 다이어그램 작성)
  - [ ] Spring ApplicationEvent 기반 내부 이벤트 발행 (`OrderPlacedEvent`, `StockDeductedEvent`)
  - [ ] 트랜잭션 경계 설계 (주문 생성 → 재고 차감 → 결제 요청 흐름의 보상 트랜잭션 설계)
  - [ ] 낙관적 락(Optimistic Lock)을 이용한 재고 동시성 제어 적용
  - [ ] API 유효성 검증(Bean Validation) 도입 (기존 상품/장바구니 DTO 보완 및 주문 DTO 검증 적용)
  - [ ] *산출물*: 주문 API, 이벤트 흐름도, 상태 전이 다이어그램, DTO 유효성 검증 규칙


### 2단계 (4~6주): 결제 + 정산 배치
외부 연동 및 트랜잭션 안전성 확보, 대용량 정산 처리를 목표로 합니다.
- [ ] **4주차: PG 결제 연동 + 결제 상태 머신**
  - [ ] 포트원(구 아임포트) 테스트 계정 생성 및 API 연동 흐름 파악
  - [ ] 결제 상태 머신 설계 (`INITIATED` → `PG_REQUESTED` → `APPROVED` → `COMPLETED` / `FAILED` / `CANCELLED`)
  - [ ] 중복 결제 방지를 위한 멱등성 키(Idempotency Key) 설계 및 적용
  - [ ] 결제 완료 콜백 검증 로직 구현 (PG 서버 측 금액 및 상품 정보 재검증)
  - [ ] *산출물*: 결제 상태 전이 다이어그램, 결제 도메인 API
- [ ] **5주차: 결제 보안 + 데이터 보호**
  - [ ] 민감 정보 암호화 적용 (카드 정보 암호화, 주문자 개인정보 등)
  - [ ] 결제 시도/승인/실패 이력을 누적하는 감사 로그(Audit Log) 테이블 설계 (Append-Only)
  - [ ] Spring Security + 역할 기반 접근 제어 (RBAC) 적용 (관리자용 정산 API 분리)
  - [ ] 결제 실패 시 보상 트랜잭션 흐름 구현 (주문 취소 이벤트 발행 및 재고 복구)
  - [ ] *산출물*: 결제 이력 API, 감사 로그 테이블 DDL 및 아키텍처 문서
- [ ] **6주차: Spring Batch 정산 파이프라인**
  - [ ] 정산 도메인 설계 (정산 대상, 정률/정액 수수료 정책, 정산 주기 및 정산 상태)
  - [ ] Spring Batch Job 설계 (`ItemReader` -> `ItemProcessor` -> `ItemWriter` 구조)
  - [ ] 대용량 처리를 위한 Chunk 크기 튜닝 및 Skip/Retry 정책 설정 (Job 재시작 가능성 보장)
  - [ ] 정산 실패 건에 대한 Dead Letter 처리 및 에러 로그 기록
  - [ ] *산출물*: 정산 배치 Job/Step 설계 문서, 정산 API 및 배치 코드

### 3단계 (7~9주): 검색 + MSA 전환
서비스 확장성 확보를 위해 검색 엔진 도입 및 도메인 분리(비동기 메시징)를 목표로 합니다.
- [ ] **7주차: Elasticsearch 검색 서비스**
  - [ ] Docker Compose 기반 Elasticsearch + Kibana 로컬 개발 환경 구성
  - [ ] 상품 검색용 인덱스 설계 (한국어 형태소 분석기 Nori 적용, 동의어 사전, ngram 자동완성 설정)
  - [ ] 상품 색인 파이프라인 구축 (상품 도메인 이벤트 발행 → 검색 서비스 수신 후 색인 반영)
  - [ ] 실시간 인기 검색어 및 키워드 랭킹 기능 구현 (검색 로그 집계)
  - [ ] *산출물*: 검색 마이크로서비스 소스코드, 자동완성/랭킹 API, 인덱스 매핑 설정 문서
- [ ] **8주차: Kafka 기반 MSA 분리**
  - [ ] Docker Compose 기반 Apache Kafka 클러스터 로컬 구성
  - [ ] 회원 및 인증(Auth) 서비스 분리 (JWT 토큰 검증 및 발급 역할을 auth-service로 위임)
  - [ ] Kafka 핵심 토픽(Topic) 설계 (`order.placed`, `payment.completed`, `stock.deducted`, `settlement.ready`)
  - [ ] Consumer Group 구성, 메시지 스키마 설계 및 파티션 분배 전략 수립
  - [ ] 메시지 유실/중복 방지 (DLQ - Dead Letter Queue 구성 및 Idempotent Consumer 패턴 적용)
  - [ ] *산출물*: Kafka 토픽 설계서, 서비스 간 비동기 메시지 흐름도
- [ ] **9주차: API Gateway + Service Discovery**
  - [ ] Spring Cloud Gateway 설정 (라우팅 룰 설정, 전역 인증 필터, API Rate Limiting)
  - [ ] Service Discovery 구축 (로컬/K8s 환경에 따른 Eureka 또는 DNS 라우팅 선택)
  - [ ] 분산 추적(Distributed Tracing) 연동 (Spring Cloud Sleuth / OpenTelemetry + Zipkin)
  - [ ] 서비스 장애 전파 방지를 위한 서킷 브레이커(Circuit Breaker, Resilience4j) 적용
  - [ ] *산출물*: API Gateway 설정 파일, 마이크로서비스 아키텍처 다이어그램

### 4단계 (10~12주): 배포 인프라 + 운영 관측성
실제 상용 수준의 배포 및 모니터링 환경 구축을 목표로 합니다.
- [ ] **10주차: Docker 멀티서비스 + GitHub Actions CI/CD**
  - [ ] 각 서비스별 Dockerfile 최적화 (Multi-stage Build 적용, 빌드 이미지 경량화, 레이어 캐싱)
  - [ ] GitHub Actions 워크플로우 파이프라인 구축 (빌드 -> 테스트 -> 도커 이미지 빌드 -> AWS ECR 푸시 -> 배포)
  - [ ] 환경별 설정 분리 (dev/staging/prod) 및 민감한 Secret 정보 암호화 관리 (AWS Secrets Manager 연동)
  - [ ] *산출물*: GitHub Actions 워크플로우 YAML 파일, 환경별 설정 파일
- [ ] **11주차: Kubernetes 배포 + 무중단 배포**
  - [ ] Kubernetes 리소스 정의 (Deployment, Service, Ingress, ConfigMap, Secret, HPA)
  - [ ] 무중단 배포 전략 수립 (Rolling Update 적용 및 서비스 헬스 체크를 위한 Liveness/Readiness Probe 설정)
  - [ ] Helm Chart 또는 Kustomize를 통한 다중 환경 매니페스트 관리
  - [ ] 데이터베이스 마이그레이션 자동화 (Flyway integration + K8s DB 마이그레이션용 일회성 Job 실행)
  - [ ] *산출물*: K8s YAML 파일 세트, 무중단 배포 전략 문서
- [ ] **12주차: 모니터링 + 문서화**
  - [ ] Prometheus + Grafana 구성 (애플리케이션 메트릭, JVM 메트릭, Kafka Consumer Lag 대시보드 연동)
  - [ ] 중앙 집중식 로그 수집 파이프라인 구축 (Loki 혹은 ELK 스택 연동)
  - [ ] Grafana 실시간 모니터링 대시보드 구축 및 Slack 연동 알림 규칙 설정
  - [ ] 프로젝트 README.md 최종 작성 및 시스템 아키텍처 문서화 완료
  - [ ] *산출물*: Grafana 모니터링 대시보드 캡처, 전체 아키텍처 정의 문서, 최종 포트폴리오용 README

---

## 2. 📚 주차별 필수 학습 체크리스트 (Learning Curriculum)

각 개발 단계의 깊이 있는 구현을 위해 필요한 핵심 이론 지식 리스트입니다. 구현 전에 반드시 학습하고 넘어갑니다.

### [1단계] DDD & Spring Multi-Module
- **Domain-Driven Design**:
  - Entity와 Value Object(VO)의 개념적 차이 (식별성 유무) 및 불변성(Immutability) 유지 이유
  - Aggregate의 개념과 Aggregate Root를 통한 트랜잭션 경계 설정 기준
  - Domain Service와 Application Service의 책임 분리 방법
- **Spring Multi-Module**:
  - 계층 간 의존성 방향 설계 (의존성 역전 원칙 - DIP 적용 및 상위 모듈이 하위 모듈에 의존하지 않도록 제한)
  - JPA Entity와 도메인 모델(Pure Java)의 분리 설계 및 이에 따른 매핑 비용 분석
- **동시성 제어**:
  - 비관적 락(Pessimistic Lock)과 낙관적 락(Optimistic Lock)의 메커니즘 및 성능적 영향도 비교

### [2단계] 트랜잭션 보장 & 배치 처리
- **분산 트랜잭션 & 보상 트랜잭션**:
  - 외부 PG 연동 시 발생할 수 있는 네트워크 예외(Time-out 등) 시나리오 및 해결책
  - 로컬 DB 트랜잭션과 외부 API 요청 간의 생명주기 불일치 문제 해결 기법
- **멱등성(Idempotency)**:
  - API 설계에서 멱등성의 중요성 및 멱등키(Idempotency Key)를 활용한 API 중복 요청 필터링 구현 원리
- **Spring Batch**:
  - Spring Batch의 주요 도메인 모델 (`Job`, `Step`, `JobInstance`, `JobExecution`, `StepExecution`)
  - Chunk 지향 처리의 장점 및 커밋 단위 튜닝 방식
  - 배치 실패 시 롤백 범위 설정 및 오류 복구(Skip/Retry) 정책 수립 방안

### [3단계] 분산 아키텍처 & 비공기 메시징
- **Elasticsearch**:
  - 역색인(Inverted Index) 구조 및 형태소 분석기(Nori) 작동 방식
  - 데이터 유실 방지를 위한 RDBMS ↔ Elasticsearch 간 동기화 기법 (CDC 혹은 Event-Driven 방식)
- **Apache Kafka**:
  - Event-Driven Architecture의 장단점 및 느슨한 결합(Loose Coupling)의 효과
  - Kafka의 Topic, Partition, Offset 개념 및 Consumer Group 간의 리밸런싱(Rebalancing) 메커니즘
  - 메시지 신뢰성 보장 전략 (At-least-once, At-most-once, Exactly-once delivery)
- **Spring Cloud & MSA**:
  - API Gateway의 역할 및 라우팅, Rate Limiting 알고리즘 (Token Bucket 등)
  - 서킷 브레이커(Resilience4j)의 상태 전이 (Closed -> Open -> Half-Open) 및 장애 격리 메커니즘
  - 분산 추적(Distributed Tracing)에서 Trace ID와 Span ID를 통한 로그 추적 원리

### [4단계] 인프라 자동화 & 모니터링
- **Docker & Container**:
  - 멀티 스테이지 빌드(Multi-stage build)를 통한 최종 이미지 경량화 원리
  - 컨테이너 이미지 레이어 캐싱 최적화 방법
- **Kubernetes**:
  - 쿠버네티스 아키텍처 및 Pod, Deployment, Service(ClusterIP, NodePort, LoadBalancer)의 연동 원리
  - Ingress 컨트롤러를 통한 외부 트래픽 라우팅
  - 롤링 업데이트(Rolling Update) 적용 시 트래픽 유실 방지를 위한 Readiness/Liveness Probe의 정확한 세팅
- **Observability (관측성)**:
  - Metric, Log, Trace의 3대 관측성 요소 및 상호 연계 필요성
  - Prometheus의 Pull 모델 작동 방식 및 Grafana를 통한 메트릭 시각화 기법

---

## 3. 🤖 AI를 위한 개발 의사결정 및 시행착오(Trade-off) 정리 지시사항

개발을 함께 진행하는 AI(Antigravity)는 프로젝트 진행 중 아키텍처 결정 사항이나 시행착오가 발생할 때마다 아래 가이드를 준수하여 마크다운 문서에 기록을 누적하고, 사용자가 향후 포트폴리오나 면접 준비 시 직접 활용할 수 있도록 문서화해야 합니다.

### 💡 AI 핵심 지침
1. **의사결정 상황 사전 감지**: 대안이 2개 이상 존재하는 설계 단계(예: 동시성 제어 기법 선택, 트랜잭션 경계 설정 등)가 오면, 코드를 바로 작성하지 말고 **대안별 장단점(Trade-off)**을 정리해 사용자에게 먼저 제시할 것.
2. **주차별 로그 및 보고서 표준 파일명 및 저장 순서**:
   매 주차 개발이 완료되면 프로젝트 내 주차별 로그 디렉토리([docs/weekly-logs/](file:///C:/Users/dlcksgml/Desktop/CS/project/projectH/docs/weekly-logs/)) 하위의 해당 주차 폴더(예: `week02/`, `week03/` 등) 내에 반드시 아래 **3가지 마크다운 문서**를 순서대로 작성 및 누적하여 저장할 것.
   - 📂 **01.learning_concepts.md**: 주차별 필수 학습 이론 개념, 기술적 원칙 및 핵심 키워드 정리 문서
   - 📂 **02.trade_offs.md**: 개발 중 마주한 대안별 장단점(Trade-off), 최종 결정 사유(ADR), 디버깅 및 시행착오 극복 과정 기록 문서
   - 📂 **03.development_report.md**: 구체적인 이번 주차 개발 내용 요약, 모듈/패키지 구조 변경 내역, REST API 명세, 단위/통합 테스트 검증 결과 보고서
3. **도메인 특화 추가 정리 문서 유연성 확보**:
   - 1주차의 '용어 사전(Ubiquitous Language)'이나 '컨텍스트 맵(Context Map)'처럼, 주차별 구현 내용 외에 **비즈니스 상태 머신 다이어그램, 도메인 이벤트 흐름도, 상세 스키마 정의서 등**의 도메인 특화적인 추가적인 설계 및 분석 정리가 필요한 경우, `04.파일명.md` 이후의 번호 순번을 매겨 유연하게 문서를 추가로 작성하여 저장할 것.

---

### 📝 1. 필수 학습 개념 기록 템플릿 (01.learning_concepts.md)
각 주차에 진행되는 학습 내용과 핵심 개념을 기록하는 포맷입니다.

```markdown
# 📚 [XX주차] 필수 이론 학습 및 개념 정리 노트

## 1. 🧩 핵심 개념 이론 정리
- 주차별 필수 학습 리스트의 이론 지식을 항목별로 요약 설명합니다.

## 2. 📂 아키텍처 및 아키텍처 규칙 설계
- 계층 관계 및 설계 사상(예: DIP, 클린 아키텍처 등)을 정리합니다.
```

---

### 📝 2. 시행착오 및 트레이드오프 기록 템플릿 (02.trade_offs.md)
모든 의사결정과 시행착오는 아래의 표준화된 마크다운 포맷으로 작성되어 누적되어야 합니다.

```markdown
### [ADR-00X] 의사결정/시행착오 제목

- **관련 단계**: (예: 1단계 3주차 주문 도메인 개발 중)
- **상황 및 배경 (Context)**
  - 어떤 기능이나 시스템을 구현하고 있었는지 설명합니다.
  - 당면한 문제점이나 제약사항을 설명합니다.

- **비교 분석 및 대안 (Options & Trade-offs)**
  각 대안에 대한 설명과 장단점(Pros/Cons)을 투명하게 비교합니다.
  
  | 대안 | 장점 (Pros) | 단점 (Cons) | 비고 |
  | :--- | :--- | :--- | :--- |
  | **대안 A** | ... | ... | 선택/미선택 사유 |
  | **대안 B** | ... | ... | 선택/미선택 사유 |

- **최종 결정 (Decision & Rationale)**
  - 최종적으로 선택한 해결책과 그 구체적인 이유(Rationale)를 서술합니다.

- **시행착오 및 극복 과정 (Lessons Learned)**
  - 구현 중 겪었던 실제 에러나 예외 발생(예: 순환 참조 등)에 대해 작성합니다.
  - 해당 문제를 디버깅하고 최종 해결하기 위해 적용한 해결 기법을 작성합니다.
```

---

### 📝 3. 주차별 개발 결과 보고서 템플릿 (03.development_report.md)
각 주차 완료 시 작성되는 개발 내용 및 결과 마크다운 양식입니다.

```markdown
# 📝 [XX주차] 개발 결과 보고서

## 1. 🛠️ 이번 주차 개발 요약
- 이번 주차에 도달한 개발 목표와 구현 완료한 기능들의 요약을 작성합니다.

## 2. 📂 모듈 및 디렉토리 구조 변경 내역
- 추가되거나 변경된 주요 모듈, 패키지, 소스 파일의 위치를 기술합니다.

## 3. ⚙️ 핵심 비즈니스 로직 및 구현 상세
- 구현된 도메인 모델, 유스케이스 서비스의 비즈니스 규칙과 설계 관점을 설명합니다.

## 4. 🌐 REST API 명세
- 구현된 REST API의 엔드포인트, HTTP 메서드, 주요 요청/응답 예시를 기록합니다.

## 5. 🧪 테스트 수행 및 검증 결과
- 작성된 단위/통합 테스트 항목과 `./gradlew test` 테스트 성공 로그나 결과를 명시합니다.
```



---

## 4. 📈 주차별 체크포인트 및 품질 지침

AI는 각 주차가 끝날 때마다 아래 기준에 맞게 개발 산출물을 함께 평가하고, 미비점이 있다면 사용자에게 개선을 제안해야 합니다.

*   **동작성 (E2E Working)**: 주요 기능이 로컬 또는 테스트 환경에서 포스트맨/Swagger 등으로 실행 가능한가?
*   **아키텍처 결정 기록 (ADR/Trade-off)**: 이번 주 설계에서 고민했던 설계 결정 사유가 해당 주차 폴더 내 `XX.trade_offs.md`에 제대로 기록되었는가?
*   **테스트 커버리지 (Testing)**: 비즈니스 핵심 도메인 로직 및 엣지 케이스에 대한 단위 테스트가 작성되었는가?
