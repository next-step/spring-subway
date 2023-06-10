## 2단계 리팩토링 요구사항
- [x] SectionService 부정형 조건문 !(a || b) 형태로 바꾸기
- [x] Station에서 id를 꺼내 외부에서 비교하지 말고 직접 물어보기
  - SectionService 개선
- [ ] Section 도메인 생성자 체이닝 적용
- [ ] test에서 필드끼리 비교할 때 usingRecursiveComparison() 사용해보기 
- [ ] LineDaoTest - hasSize(0) 을 isEmpty() 로 개선

## 2단계 기능 요구사항
- [ ] 프로덕션, 테스트용 profile 다르게 설정하기
  - 프로덕션 DB는 로컬에 저장, 테스트 DB는 인메모리로 동작하도록 설정 
- [ ] 경로 조회 API 구현
  - 1. 최단 거리 경로, 2. 거리 정보, 3. 해당 거리에 대한 요금 응답
  -  다른 노선으로의 환승도 고려해야 함
