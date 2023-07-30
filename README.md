# jwp-subway-path

--- 

## 도메인 후보

지하철 Station
지하철 Station 속성:
이름(name)
지하철 Section
지하철 (상행 방향)Station과 (하행 방향)Station 사이의 연결 정보
지하철 Section 속성:
길이(distance)
지하철 Line
지하철 Section의 모음으로 Section에 포함된 지하철 Station의 연결 정보
지하철 Line 속성:
Line 이름(name)
Line 색(color)

## 요구사항

### 도메인

- [x] Line
  - [x] Line 이름은 중복될 수 없다.
- [x] 구간 삭제
  - [x] Line에 하나의 Section만 있을때, 삭제할 수 없다.
  - [x] Line에서 Station을 삭제할때, Station이 Line에 존재하지 않는다면, 예외를 던진다.
  - [x] 하행 종점 Section을 삭제할 수 있다.
  - [x] 중간 Section을 삭제할 수 있다.
  - [x] 상행 종점 Section을 삭제할 수 있다.
- [x] Section
  - [x] Station과 Station을 연결할 수 있다.
  - [x] 연결된 Section을 알고있다.
  - [x] 라인과 연결할 수 있다.
  - [x] 특정 라인의 상행 Section, 하행 Section을 알 수 있다.
  - [x] Section과 Station이 Line에 포함되어있는지 알 수 있다.
    - [x] Section이 추가될 때, 추가되는 Section의 상행Station이 자신의 하행Station과 동일한지 확인한다.
    - [x] 새로운 Section의 하행Station은 해당 Line에 등록되어있는 Station일 수 없다. (상행은 됨)
- [x] Station
  - [x] 이름을 표현한다.
  - [x] 이름이 중복되면 예외를 던진다.
- [ ] PathFinder
  - [ ] 출발 Station과 도착 Station의 최단 거리 경로와 거리를 조회한다.
  - [ ] 출발 Station과 도착 Station이 같은 경우 조회할 수 없다.
  - [ ] 출발 Station과 도착Station이 연결되지 않은 경우 조회할 수 없다.
  - [ ] 존재하지 않는 출발 Station이나 도착 Station일 경우 조회할 수 없다.

lineId, upStationId, downStationId;

1. [x] lineId기준 DB에서 모든 section 조회
2. [x] 조회된 section에서 upStationId가 하행 section이 맞는지 확인
3. [x] 하행 section이랑 연결

### 애플리케이션 서비스

- [x] SectionService
  - [x] UpStationId, DownStationId를 통해 Section을 생성한다.

- [x] LineService
  - [x] LineId랑 SectionDto를 받아서, Section을 Line에 추가할 수 있다.

### Dao

- [x] SectionDao
  - [x] Section을 받아 저장한다.

### Section 등록 기능

- [x] 새로운 Section의 시작Station은 기존 Section의 끝 Station과 같으면 등록 가능 하다.
- [x] 새로운 Section의 시작Station은 기존 Section의 끝 Station과 다르면 예외를 던진다.
- [x] 새로운 Section의 끝Station은 기존 Section에 등록되어 있으면 예외를 던진다.
- [x] 새로운 Section의 상행, 하행이 Line의 하행 혹은 상행과 일치한다면 삽입될수있다
  - [x] 중간에 삽입하는 Section의 길이가,기존 역 사이의 길이보다 크거나 같으면, 삽입할 수 없음
  - [x] 상행과, 하행이 모두 Section에 있다면, 추가할 수 없음
  - [x] 상행과 하행이 둘 다 포함되지 않다면, 추가할 수 없음

### Section 제거 기능

- [x] Line에 하나의 Section만 있을때, 삭제할 수 없다.
- [x] Line에서 Station을 삭제할때, Station이 Line에 존재하지 않는다면, 예외를 던진다.

### 경로 조회 기능

- [ ] 출발역으로부터 도착역까지의 경로에 있는 역의 목록과 거리를 조회한다.
  - [ ] 출발역과 도착역이 같은 경우 조회할 수 없음
  - [ ] 출발역과 도착역이 연결되지 않은 경우 조회할 수 없음
  - [ ] 존재하지 않는 출발역이나 도착열일 경우 조회할 수 없음
  
### 학습 테스트

- [X] 다익스트라 알고리즘 라이브러리 학습 테스트를 진행한다.

### 프로그래밍 요구사항

- [X] 데이터베이스 설정을 프로덕션(로컬)과 테스트(인메모리)를 다르게 한다.