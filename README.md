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
- [ ] 구간 제거 기능
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