# 요구사항

### 지하철 역 (Station)
지하철 역 하나의 정보

### 지하철 노선 (Line)
지하철 구간의 모음으로 구간에 포함된 지하철 역의 연결 정보

### 지하철 구간 (Section)
지하철 (상행 방향)역과 (하행 방향)역 사이의 연결 정보

- [x] 길이가 양수여야 한다.
- [x] 상행역과 하행역은 같을 수 없다.
- [x] 주어진 역이 자신의 상행역과 같은지 확인한다.
- [x] 주어진 역이 자신의 하행역과 같은지 확인한다.
- [x] 주어진 구간과 상행역 또는 하행역이 겹치는지 확인한다.
- [x] 주어진 구간의 길이가 같거나 긴 경우 자를 수 없다.
- [x] 구간의 상행역과 주어진 구간의 상행역이 같은 경우 구간의 윗부분을 잘라낸다.
- [x] 구간의 하행역과 주어진 구간의 하행역이 같은 경우 구간의 아랫부분을 잘라낸다.
- [x] 구간의 상행역, 하행역이 주어진 구간의 상행역, 하행역과 모두 같거나 모두 다른 경우 자를 수 없다.
- [x] 구간의 하행역과 주어진 구간의 상행역이 같은 경우 연장한다.
- [x] 구간의 하행역과 주어진 구간의 상행역이 다른 경우 연장할 수 없다.

### 지하철 구간들 (Sections)
지하철 구간 리스트를 포장하는 일급 컬렉션

- [x] 구간을 정렬한다.
- [x] 끊어진 구간들로 생성할 수 없다.
- [x] 순환된 구간으로 생성할 수 없다.
- [x] 널이거나 비어 있는 구간 리스트로 생성할 수 없다.
- [x] 다른 노선에 속한 구간들로 생성할 수 없다.

- [x] 역을 포함하는지 확인한다.
- [x] 노선의 첫 역인지 확인한다.
- [x] 노선의 마지막 역인지 확인한다.

### 구간 추가 관리자 (SectionAddManager)
- [x] 추가할 구간의 한 역만 기존 노선에 포함되어야 한다.
- [x] 추가할 구간의 길이가 대상 구간보다 같거나 길면 추가할 수 없다.
- [x] 노선의 특정 구간과 상행역만 같은 경우 노선에 추가한다.
- [x] 노선의 특정 구간과 하행역만 같은 경우 노선에 추가한다.
- [x] 추가할 구간의 하행역이 기존 노선의 상행 종점역과 같은 경우 노선의 맨 앞에 추가한다.
- [x] 추가할 구간의 상행역이 기존 노선의 하행 종점역과 같은 경우 노선의 맨 뒤에 추가한다.
- [x] 노선의 맨 앞이나 뒤에 구간을 추가하는 경우 변경 대상 구간이 존재하지 않는다.
- [x] 노선의 중간에 구간을 추가하는 경우 변경 대상 구간이 존재한다.

### 구간 삭제 관리자 (SectionRemoveManager)
- [x] 구간이 1개인 경우 삭제할 수 없다.
- [x] 노선에 등록되어 있지 않은 역을 제거할 수 없다.
- [x] 노선에서 중간에 위치한 역을 제거하는 경우 변경 대상 구간이 존재한다.
  - [x] 노선에 A-B-C 역이 연결되어 있을 때 B역을 제거할 경우 A-C로 재배치 된다.
  - [x] 거리는 두 구간의 거리의 합으로 정한다.
- [x] 노선에서 종점역을 제거하는 경우 변경 대상 구간이 존재하지 않는다.

### 최단 경로 탐색기 (PathFinder)
- [x] 출발역과 도착역이 같은 경우 경로를 구할 수 없다.
- [x] 출발역과 도착역이 연결되어 있지 않은 경우 경로를 구할 수 없다.
- [x] 출발역과 도착역을 연결하는 최단 경로를 구한다.
