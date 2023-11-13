[프로젝트 특이사항]

1. 상품관리, 게시물관리 기능에 시큐리티 추가 버전
2. Remember-Me 설정시 필요한 테이블
CREATE TABLE persistent_logins (
username VARCHAR(64) NOT NULL,
series VARCHAR(64) PRIMARY KEY,
token VARCHAR(64) NOT NULL,
last_used TIMESTAMP NOT NULL
);