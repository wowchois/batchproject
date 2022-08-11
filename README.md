# Batch Test Project
Spring Batch에서 구현 가능한 기능을 테스트하는 프로젝트.

## Job
- TestConfigJob
- TestCsvJobConfig
- TestExcelJobConfig
- UserManageJob


---------------------



## ERROR 정리

### [Ljava.lang.Object; cannot be cast to   
Entity vo 접근 에러, iterator로 접근해서 Object[] 형변환.  
 정리 : https://choisblog.tistory.com/90

### lock wait timeout exceeded; try restarting transaction. 
원인 :   
chunk단위로 처리하는데, 외부 api를 호출하는 과정에서 발생.    
외부 api 응답을 기다리는 동안, chunk size 만큼 데이터 row를  lock으로 잡고 있다가, 다른쪽에서 해당 row를 수정할 경우 lock wait timeout 발생한다.   

추천 해결방법 :   
1. lock으로 잡는 chunk size가 많을수 있으므로 chunk size를 줄인다
2. 트래픽 증가로 인한 외부 통신 네트워크 이슈 확인
3. 외부 api 호출을 transaction과 분리하기
