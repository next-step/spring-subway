# 지하철 노선도

## 1단계: 구간 관리 기능

### 📝 도메인 정의

#### 역 (Station)

- 이름

#### 구간 (Section)

- 길이
- 상행 역
- 하행 역

#### 노선 (line)

- 이름
- 색

### ✅ 기능 정리

#### 지하철 노선에서 구간 등록

- 요청
- [x] POST ```/lines/{노선 id}/sections``` 로 보낸다.
- [x] body는 json 형식으로 ```상행 역, 하행 역, 길이``` 를 보낸다.
  - [x] 각 요소들은 모두 하나의 원자값만 가진다.
  - [x] 상행 역, 하행 역 id의 타입은 문자열이고, 길이의 타입은 숫자이다.
  - [x] 길이는 0 초과의 정수이다.
  - [x] 하행 역의 노선은 위 url의 노선과 같다.
- 응답
- [x] 성공적으로 구간이 등록되면 Status Code는 201, body는 구간 정보를 보낸다.
합- 제약 사항
- [x] 새로운 구간의 상행 역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.
- [x] 새로운 구간의 하행 역은 해당 노선에 등록되어있는 역일 수 없다.

#### 지하철 노선에서 구간 삭제

- 요청 
- [x] DELETE ```/lines/{노선 id}/sections``` 로 보낸다.
- [x] query parameter로 ```역의 id(stationId)``` 를 요청한다.
- 응답
- [x] 성공적으로 구간이 삭제되면 Status Code는 200을 보낸다.
- [x] 올바르지 않은 요청으로 삭제 실패시 Status Code 400과 에러 메세지를 보낸다.
- 제약 사항
- [x] 지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있다.
- [x] 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우 역을 삭제할 수 없다.

#### 지하철 노선 등록

- 요청
  - [x] 상행 종점, 하행 종점, 거리를 추가로 보낸다.
  - [x] 노선 등록 시 상행 종점과 하행 종점을 연결하는 구간도 같이 등록한다.

## 2단계 : 구간 추가 기능 심화

### 노선 조회
- 응답
  - [x] 노선에 해당하는 역과 구간을 순서대로 반환한다. (상행 종점 -> 하행 종점) 

### 구간 등록
- 변경사항
  - [x] 상행 종점과 하행 종점이 아니더라도 구간 추가가 가능하다.
- 제약사항
  - [x] 새로운 구간의 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정한다.
  - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.
  - [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다. 
  - [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.

## 3단계 : 구간 제거 기능 심화

### 예외처리
- [x] request dto 에 예외처리를 추가한다.

### 구간 제거
- 변경사항
  - [x] 위치에 상관없이 구간 제거가 가능하다. 
- 제약사항
  - [x] 종점역이 제거될 경우 다음으로 오던 역이 종점역이 된다.
  - [x] 중간역이 제거될 경우 재배치를 한다.
  - [x] 거리는 두 구간의 합으로 결정한다.
  - [x] 구간이 하나인 노선에서는 마지막 구간을 제거할 수 없다.
  - [x] 노선에 등록되어 있지 않은 역은 제거할 수 없다.

## 4단계 : 경로 조회 기능

### 데이터베이스 설정
- [x] 프로덕션의 데이터베이스는 로컬에 저장될 수 있도록 설정한다.
- [x] 테스트용 데이터베이스는 인메모리로 동작할 수 있도록 설정한다.

### 경로 조회 기능
- 요청
  - [x] 출발역 id (source)
  - [x] 도착역 id (target)
- 응답
  - [x] 출발역으로부터 도착역까지의 경로에 있는 역 목록을 반환한다.
  - [x] 조회한 경로 구간의 거리를 반환한다.
- 제약사항
  - [x] 출발역과 도착역은 달라야 한다.
  - [x] 출발역과 도착역이 연결되어 있어야 한다.
  - [x] 출발역과 도착역은 존재하는 역이어야 한다.