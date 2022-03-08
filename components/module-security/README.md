# module-security

Gateway에 추가될 보안 모듈.

Jwt + redis를 사용하여 각 서비스 요청에 필요한 정보를 
토큰에 연계하여 제공한다.

## 기본 제공 기능
* 토큰 발급
* 토큰 연장 요청
* 토큰 폐기
* api요청 차단

## 연계 모듈
- support-token
- library-security