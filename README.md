# 지하철 노선도

## 소개

이번 미션은 지하철 노선도라는 요구사항을 가진 웹 애플리케이션을 구현하는 것 입니다.
제공되는 뼈대코드를 바탕으로 노선에 역을 등록하고 제거하는 기능을 객체지향적으로 설계하고 구현하는 것을 목표로 합니다.
이번 단계에서 구현되는 노선에 역 등록/제거 기능은 다음 단계의 요구사항인 경로 조회 기능을 위한 정보 관리 기능입니다.
비즈니스 규칙을 검증하는 테스트를 각각 구현합니다. 테스트를 이용하여 요구사항을 만족하는지를 확인합니다.
가급적 TDD로 구현합니다. 작성한 테스트를 통해 애플리케이션을 실행시키고 브라우저를 띄워서 확인하지 않아도 정상 동작 여부를 확인할 수 있습니다.

## 요구사항

### 기능 요구사항

요구사항 설명에서 제공되는 요구사항을 기반으로 지하철 구간 관리 기능을 구현하세요.
예외 케이스에 대한 검증도 포함하세요.

### 요구사항 설명

####

#### 구간 등록 기능

- [X] '구간' 있어야함
- [X] 구간에는 노선, 상행역과 하행역과 거리가 있어야한다.
- [X] 지하철 노선에 구간을 등록하는 기능을 구현
- [X] 새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.
    - [X] 노선에 하행 종점역을 구하는 Dao method 를 구현한다.
    - [X] 하행 종점역과 새로운 구간의 상행역이 다르면 예외를 던진다.
- [X] 새로운 구간의 하행역은 해당 노선에 등록되어있는 역일 수 없다.
    - [X] 구간 테이블에서 노선에 해당하는 하행역이 존재하는지 확인하는 Dao method 를 구현한다.
    - [X] 새로운 구간 하행역이 기존 노선에 존재하면 예외를 던진다.
- [X] 새로운 구간 등록시 위 조건에 부합하지 않는 경우 에러 처리한다.
- 구간 등록 request

```
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
"downStationId": "4",
"upStationId": "2",
"distance": 10
}
```

#### 구간 제거 기능

- [x] 지하철 노선에 구간을 제거하는 기능 구현
- [x] 지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있다. 즉, 마지막 구간만 제거할 수 있다.
- [x] 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.
- [x] 새로운 구간 제거시 위 조건에 부합하지 않는 경우 에러 처리한다.
- 지하철 구간 삭제 request

```
- DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```

#### 구간 관리 기능의 예외 케이스를 고려하기

- [ ] 구간 등록과 제거 기능의 예외케이스들에 대한 시나리오를 정의
- [ ] 인수 테스트를 작성하고 이를 만족시키는 기능을 구현
