# IntoTheHeaven

---

## 프로젝트에 관하여

- Hexagonal Architecture로 개발되었습니다.
- CQRS 패턴을 사용하여, Read Model과 Write Model을 분리하였습니다.
- 공부중인 DDD를 적용하였습니다.

---

## 로컬 셋팅

1. 로컬 테스트 DB 셋팅
    1. docker 설치
    2. mysql 8.0 image 설치
       ```
       docker pull mysql:8.0
       ```
    3. docker mysql container 백그라운드 실행
       ```
       docker run --name into_the_heaven -e MYSQL_ROOT_PASSWORD=intotheheaven -e MYSQL_DATABASE=into_the_heaven -d -p 3306:3306 mysql:8.0
       ```

2. 서버 구동
    1. profile 분리하여 실행 (intelliJ)
       ```
       // application-local.yml 파일을 읽게 설정합니다.   
       active profiles >> local
       ```
3. 초기 Data seeding
    - 개발 예정

---

## 협업툴

1. SwaggerUI
    - Frontend <-> Backend 소통을 위해서 만들었습니다.
    - https://github.com/mitl-feedmysheep/api-spec
    - 해당 프로젝트의 README를 읽어보시면 사용방법을 알 수 있습니다.
2. Postman
    - 함께 사용할 수 있는 Workspace가 있습니다.
    - 초기작업 / 앱 / 어드민 각 폴더가 분리되어 있으며 도메인별로 하위 폴더가 존재합니다.
3. ERD
    - https://www.erdcloud.com/d/7PhCjKPXwjPcS5uiP
4. 지라
    - 태스크를 관리해요.
5. 노션
    - 각종 문서작업을 해요.
6. 슬랙
    - 소통할 때 사용해요.
7. 구글밋 or 줌
    - 일주일에 한번 회의할 때 사용해요.
      **따로 요청하시면 됩니다!**

---

## 포맷팅

- google-styleguide를 사용합니다.
    - https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml
- https://kangyb.tistory.com/26 참고

---

## 기타 로직

1. 토큰 로직
    - 로그인 시, "refreshToken"과 "accessToken"을 함께 발행해준다.
    - accessToken 완료될 시
        - invalidToken이라는 status와 토큰이 완료되었다라는 메시지를 함께 보낸다.
        - /app/token 으로 refresh 토큰을 보내면, 새로운 refreshToken과 accessToken을 발급해준다.
        - 다시 요청한다.
    - refreshToken 완료될 시
        - 로그인을 다시해야한다.

---

## 테스트 케이스

코어한 도메인 로직만 작성 예정입니다.