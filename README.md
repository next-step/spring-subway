# jwp-subway-path

## 요구사항

### Step 1. 구간(section) 관리기능

- 지하철 구간(section)
    - 상행역과 하행역 사이의 연결 정보.
    - 길이(distance) 속성을 가짐.
- [x] 구간 등록 기능
    - `POST` /lines/{id}/sections
      ```json
      {
        "downStationId" : "4",
        "upStationId" : "2",
        "distance" : 10
      }
      ```
    - 지하철 노선에 구간 등록
    - 새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.
        - 하행 방향으로만 새로운 역 추가가 가능하다.
    - 새로운 구간의 하행역은 해당 노선에 등록되어있는 역일 수 없다.
    - 위 조건에 부합하지 않는 경우 에러 처리한다.
- [x] 구간 제거 기능
    - `DELETE` /lines/{id}/sections?stationId={station_id}
    - 하행 종점역만 제거할 수 있다.
    - 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우 역을 삭제할 수 없다.
        - 구간이 1개인 경우 삭제할 수 없다.
    - 위 조건에 부합하지 않는 경우 에러 처리한다.
- [x] 기존 노선 생성 방식 변경
    - 리액트 프론트엔드 참고하여 노선 생성 시 반드시 구간을 함께 생성하도록 변경.
    - `POST` /lines
      ```json
      {
        "name": "2호선",
        "color": "green",
        "downStationId": 2,
        "upStationId": 3,
        "distance": 10
      }
      ```
    - 노선 생성 시 반드시 1개의 구간을 포함하여 생성하도록 한다.

### Step 2. 구간 추가 기능 심화

- [x] 역 사이에 새로운 역을 등록
    - [x] 노선에 새로운 구간의 두 역 중 하나도 없으면 오류
    - [x] 노선에 새로운 구간의 두 역이 모두 있으면 오류
    - 노선에 상행역이 이미 있다면
        - [x] 상행역이 기존 노선의 하행 종점역인 경우에는 별도 검증 없이 추가 가능
        - [x] 상행역이 하행 종점역이 아닌 경우 원래 구간과 거리 비교
    - 노선에 하행역이 이미 있다면
        - [x] 하행역이 기존 노선의 상행 종점역인 경우 별도 검증 없이 추가 가능
        - [x] 하행역이 상행 종점역이 아닌 경우 원래 구간과 거리 비교
- [x] LineRequest에서 정렬된 stations를 반환

### Step 3 이후 고려 사항

- [ ] 노선 삭제 시, 연관된 구간 모두 삭제
    - `Step 3`가 삭제 기능 추가 과제라 그 때 논의함

### Step 3 구간 삭제 기능 심화
- [x] 구간 삭제 시 ,위치에 상관 없이 삭제가 가능하도록 수정
  - [x] 종점이 제거될 경우, 다음으로 오던 역이 종점이 됨 
  - [x] 중간역이 제거될 경우 재배치를 함. 거리는 두 구간의 합.
- 제거가 안되는 경우
  - [x] 구간이 하나인 노선일 경우.
  - [x] 제거하려는 역이 노선의 구간에 포함되어있지 않는 경우.
- [x] 노선 삭제 시, 연관된 구간 모두 삭제

  

## 커밋 컨벤션

커밋 메시지는 아래 포맷에 맞게 설정한다.

```shell
  <commit_type> : <commit_message>
```

커밋 타입은 아래 항목 중 하나로 한다.

- feature
- test
- refactor
- docs
- fix
- chore