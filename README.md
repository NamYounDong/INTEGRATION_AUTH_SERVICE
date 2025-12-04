# CloudAuthService

Google OAuth2를 활용한 클라우드 인증 서비스입니다. 여러 서비스에서 공통으로 사용할 수 있는 중앙화된 인증 시스템을 제공합니다.

## 기술 스택

### Backend
- **Java 17** - 프로그래밍 언어
- **Spring Boot 3.5.4** - 애플리케이션 프레임워크
- **Spring Security** - 보안 및 인증/인가 처리
- **Spring OAuth2 Client** - OAuth2 클라이언트 지원
- **MyBatis 3.0.5** - SQL 매퍼 프레임워크
- **Gradle** - 빌드 도구

### Database & Cache
- **PostgreSQL** - 관계형 데이터베이스
- **Redis** - 세션 저장소 (Spring Session)

### Frontend
- **JSP** - 서버 사이드 뷰 템플릿
- **JSTL** - JSP 표준 태그 라이브러리

### 기타
- **Lombok** - 보일러플레이트 코드 자동 생성
- **Jackson** - JSON 직렬화/역직렬화
- **Logback** - 로깅 프레임워크

## 주요 기능 설명

### 1. Google OAuth2 인증
- Google 계정을 통한 소셜 로그인 지원
- OAuth2 인증 플로우 자동 처리
- 인증 성공/실패 핸들러를 통한 커스텀 처리

### 2. 사용자 관리
- OAuth2 인증 후 사용자 정보 자동 저장
- 이메일 기반 사용자 조회 및 중복 방지 (ON CONFLICT 처리)
- 사용자 정보 세션 저장 (Redis 기반)

### 3. 서비스별 콜백 URL 관리
- 여러 서비스에서 인증 후 각각의 콜백 URL로 리다이렉트
- 서비스 시퀀스(srvcSeq)를 통한 동적 콜백 URL 처리
- OAuth2 state 파라미터를 활용한 서비스 정보 전달

### 4. 세션 관리
- Redis를 통한 분산 세션 관리
- 세션 정보를 LinkedHashMap 형태로 저장하여 다른 서비스(FASTAPI, Node.js 등)에서 활용 가능
- 최대 1개 세션 제한 (중복 로그인 방지)

### 5. 보안 설정
- CSRF, CORS 비활성화 (인증 서비스 특성상)
- 정적 리소스 및 JSP 직접 접근 허용
- 인증된 사용자만 접근 가능한 엔드포인트 보호

## 소스 플로우

### 인증 플로우

```
1. 사용자 접근
   └─> /pg/login 페이지 요청
       └─> PageController.selectSrvcList() 호출
           └─> 로그인 페이지에 서비스 목록 표시

2. Google OAuth2 로그인 시작
   └─> /oauth2/authorization/google?srvcSeq={서비스번호} 요청
       └─> SecurityConfig.authorizationRequestResolver()
           └─> srvcSeq를 state 파라미터에 Base64 인코딩하여 저장
       └─> Google OAuth2 인증 페이지로 리다이렉트

3. Google 인증 완료
   └─> /auth 콜백 엔드포인트 호출
       └─> OAuth2SuccessHandler.onAuthenticationSuccess() 실행
           ├─> state 파라미터에서 srvcSeq 디코딩
           ├─> OidcUser에서 사용자 정보 추출
           ├─> UserService.findUser() - 기존 사용자 조회
           │   └─> 없으면 UserService.saveUser() - 신규 사용자 저장
           ├─> 세션에 사용자 정보 저장 (LinkedHashMap 형태)
           └─> 콜백 URL 결정
               ├─> srvcSeq가 있으면 → SrvcService.findSrvcClbckUrl() 조회 후 리다이렉트
               └─> 없으면 → /pg/home으로 리다이렉트
```

### 데이터 플로우

```
Controller Layer
    ↓
Service Layer (UserService, SrvcService)
    ↓
Mapper Interface (UserMapper, SrvcMapper)
    ↓
MyBatis XML Mapper
    ↓
PostgreSQL Database
```

## 디렉터리 구조

```
CloudAuthService/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── shop/dm_nyd/cloudAuthService/
│   │   │       ├── CloudAuthServiceApplication.java    # 메인 애플리케이션 클래스
│   │   │       ├── config/                             # 설정 클래스
│   │   │       │   ├── SecurityConfig.java            # Spring Security 설정
│   │   │       │   ├── OAuth2SuccessHandler.java      # OAuth2 인증 성공 핸들러
│   │   │       │   ├── OAuth2FailHandler.java         # OAuth2 인증 실패 핸들러
│   │   │       │   ├── RedisConfig.java               # Redis 연결 설정
│   │   │       │   └── WebMvcConfig.java              # Web MVC 설정
│   │   │       ├── controllers/                        # 컨트롤러
│   │   │       │   ├── PageController.java            # 페이지 컨트롤러
│   │   │       │   └── UserController.java            # 사용자 컨트롤러
│   │   │       ├── service/                           # 서비스 인터페이스
│   │   │       │   ├── UserService.java
│   │   │       │   └── SrvcService.java
│   │   │       ├── service/impl/                      # 서비스 구현체
│   │   │       │   ├── UserServiceImpl.java
│   │   │       │   └── SrvcServiceImpl.java
│   │   │       ├── mapper/                            # MyBatis 매퍼 인터페이스
│   │   │       │   ├── UserMapper.java
│   │   │       │   └── SrvcMapper.java
│   │   │       └── vo/                                # Value Object
│   │   │           ├── BaseVo.java
│   │   │           ├── UserVo.java
│   │   │           ├── SrvcVo.java
│   │   │           └── Role.java
│   │   ├── resources/
│   │   │   ├── application.properties                 # 애플리케이션 설정
│   │   │   ├── logback-spring.xml                     # 로깅 설정
│   │   │   ├── mapper/                                # MyBatis XML 매퍼
│   │   │   │   ├── UserMapper.xml
│   │   │   │   └── SrvcMapper.xml
│   │   │   └── static/                                # 정적 리소스
│   │   │       └── index.html
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── views/                             # JSP 뷰 파일
│   │               ├── home.jsp
│   │               └── login.jsp
│   └── test/                                           # 테스트 코드
├── build.gradle                                        # Gradle 빌드 설정
├── settings.gradle                                     # Gradle 프로젝트 설정
└── README.md                                           # 프로젝트 문서
```

## 설치 및 실행

### 사전 요구사항

- **Java 17** 이상
- **PostgreSQL** 데이터베이스
- **Redis** 서버
- **Gradle** (또는 Gradle Wrapper 사용)

### 1. 데이터베이스 설정

PostgreSQL에서 다음 테이블을 생성합니다:

```sql
-- 사용자 테이블
CREATE TABLE users (
    user_id VARCHAR(255) PRIMARY KEY,
    user_nm VARCHAR(255),
    picture TEXT,
    email VARCHAR(255) UNIQUE,
    phon_no VARCHAR(50),
    role VARCHAR(50)
);

-- 서비스 테이블
CREATE TABLE srvc (
    srvc_seq SERIAL PRIMARY KEY,
    srvc_nm VARCHAR(255),
    clbck_url TEXT
);
```

### 2. 설정 파일 수정

`src/main/resources/application.properties` 파일을 환경에 맞게 수정합니다:

```properties
# 데이터베이스 설정
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your_password

# Redis 설정
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=your_redis_password

# OAuth2 설정 (Google)
spring.security.oauth2.client.registration.google.client-id=your_client_id
spring.security.oauth2.client.registration.google.client-secret=your_client_secret
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/auth
```

### 3. Google OAuth2 설정

1. [Google Cloud Console](https://console.cloud.google.com/)에 접속
2. 프로젝트 생성 및 OAuth 2.0 클라이언트 ID 생성
3. 승인된 리디렉션 URI에 `http://localhost/auth` 추가 (또는 배포 환경에 맞게 수정)
4. 생성된 Client ID와 Client Secret을 `application.properties`에 설정

### 4. 빌드 및 실행

#### Gradle Wrapper 사용 (권장)

```bash
# Windows
gradlew.bat build
gradlew.bat bootRun

# Linux/Mac
./gradlew build
./gradlew bootRun
```

#### 직접 실행

```bash
gradle build
gradle bootRun
```

### 5. 접속

애플리케이션 실행 후 브라우저에서 다음 URL로 접속:

- 로그인 페이지: `http://localhost/pg/login`
- 홈 페이지: `http://localhost/pg/home` (인증 필요)

## API 엔드포인트

### 공개 엔드포인트
- `GET /pg/login` - 로그인 페이지 (서비스 목록 포함)
- `GET /` - 루트 경로
- `GET /auth` - OAuth2 콜백 엔드포인트

### 인증 필요 엔드포인트
- `GET /pg/home` - 홈 페이지
- 기타 모든 엔드포인트는 인증 필요

## 세션 정보 구조

인증 성공 후 세션에 저장되는 사용자 정보 구조:

```java
{
    "userId": "user@example.com",
    "email": "user@example.com",
    "userNm": "사용자 이름",
    "picture": "프로필 이미지 URL",
    "role": "USER"
}
```

이 정보는 `request.getSession().getAttribute("USER")`로 접근 가능하며, 다른 서비스에서도 활용할 수 있도록 LinkedHashMap 형태로 저장됩니다.

## 환경 변수 설정 (선택사항)

프로덕션 환경에서는 민감한 정보를 환경 변수로 관리하는 것을 권장합니다:

```bash
export DB_PASSWORD=your_password
export REDIS_PASSWORD=your_redis_password
export GOOGLE_CLIENT_ID=your_client_id
export GOOGLE_CLIENT_SECRET=your_client_secret
```

`application.properties`에서 다음과 같이 참조:

```properties
spring.datasource.password=${DB_PASSWORD}
spring.redis.password=${REDIS_PASSWORD}
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
```

## 로깅

- **Spring Security**: DEBUG 레벨로 설정되어 인증 과정 추적 가능
- **MyBatis**: DEBUG 레벨로 설정되어 SQL 쿼리 및 파라미터 로그 출력
- 로그는 Logback을 통해 관리되며, `logback-spring.xml`에서 설정 가능

## 주의사항

1. **보안**: 프로덕션 환경에서는 CSRF, CORS 설정을 재검토하고 적절히 구성해야 합니다.
2. **세션 관리**: Redis 연결이 끊기면 세션 정보가 유지되지 않으므로 Redis 가용성을 보장해야 합니다.
3. **OAuth2 설정**: Google OAuth2 클라이언트 설정 시 리디렉션 URI가 정확히 일치해야 합니다.
4. **데이터베이스**: 사용자 정보는 `user_id`를 기준으로 중복 방지되며, ON CONFLICT로 업데이트됩니다.

## 개발 환경

- **IDE**: IntelliJ IDEA, Eclipse 등
- **JDK**: OpenJDK 17 또는 Oracle JDK 17
- **빌드 도구**: Gradle 7.x 이상

## 라이선스

이 프로젝트는 내부 사용을 위한 프로젝트입니다.

## 기여

프로젝트 개선을 위한 제안이나 버그 리포트는 이슈로 등록해주세요.

