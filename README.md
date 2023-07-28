

# 요구사항

### 지하철 역(station)

### 지하철 구간(section)
지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보

- [x] 길이가 양수여야 한다.
- [x] 상행역과 하행역은 같을 수 없다.
- [x] 추가할 구간의 크기가 같거나 큰 경우 합칠 수 없다.
- [x] 자신의 하행역과 주어진 구간의 상행역이 같은지 확인한다.
- [x] 해당 역은 구간의 상행역과 같다.
- [x] 해당 역은 구간의 하행역과 같다.
- [x] 구간은 주어진 역을 포함한다.
- [x] 구간의 하행역이 연결할 구간의 상행역과 다르면 중간역을 삭제할 수 없다.
- [x] 구간은 주어진 구간과 동일한 상행역 혹은 하행역을 가진다.
- [x] 구간은 주어진 라인의 소속이다.
- [x] 추가할 구간의 크기가 같거나 큰 경우 합칠 수 없다.
- [x] 구간의 상행역과 추가할 구간의 상행역이 같은 경우 합친다.
- [x] 구간의 하행역과 추가할 구간의 하행역이 같은 경우 합친다.
- [x] 구간의 상행역, 하행역이 추가할 구간의 상행역, 하행역과 모두 같거나 모두 다른 경우 합칠 수 없다.
- [x] 구간의 하행역이 연결할 구간의 상행역과 다르면 중간역을 삭제할 수 없다.
- [x] 현재구간과 상대구간의 중간 역을 삭제한다

### 지하철 구간들(sections)
- [x] 정렬된 구간들을 생성한다.
- [x] 순환된 구간으로 생성할 수 없다.
- [x] 널이거나 비어 있는 구간 리스트로 생성할 수 없다.
- [x] 끊어진 구간들로 생성할 수 없다.
- [x] 삭제할 역이 노선의 하행 종점역이 아니면 삭제할 수 없다
- [x] 구간이 1개인 경우 구간을 삭제할 수 없다.
- [x] 첫 구간을 삭제한다
- [x] 중간 구간을 삭제한다
- [x] 마지막 구간을 삭제한다
- [x] 구간들의 역 목록을 순서대로 가져온다.

### 지하철 노선(line)
지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보

### 지하철 노선구간(lineSections)
지하철 노선 구간을 관리한다.

- [x] 노선을 생성할 때 한 구간을 생성한다.
- [x] 구간들이 특정 노선에 속하는지 확인한다.
- [x] 다른 노선에 속한 구간들로 생성할 수 없습니다.
- [x] 다른 노선에 속한 구간은 추가할 수 없습니다.
