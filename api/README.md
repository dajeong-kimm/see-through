# Java Version

- Eclipse Temurin 17.0.13

# IDE Setting

## 네이버 코딩 컨벤션 적용

0. [Naver Formatter 다운로드](https://github.com/naver/hackday-conventions-java/blob/master/rule-config/naver-intellij-formatter.xml)
1. File → Settings (Alt + Ctrl + S)
2. Editor → Code Style → Java
3. Scheme 항목의 오른쪽 톱니바퀴 아이콘 선택
4. Import Scheme → Intellij IDEA Code Style XML
5. 다운받은 Formatter xml 선택
6. (선택) `To`에 저장할 이름 설정
7. 아래 설정 체크
    - Wrapping and Braces 탭 → 'if()' statement → `'else' on new line`

## 자동 임포트 설정

1. File → Settings (Alt + Ctrl + S)
2. Editor → General → Auto Import
3. 아래 설정 체크
    - `Add unambiguous imports on the fly`
    - `Optimize imports on the fly`

## 코딩 컨벤션 자동 적용 설정

1. File → Settings (Alt + Ctrl + S)
2. Tools → Actions on Save
3. 아래 설정 체크
    - `Reformat code`
    - `Optimize imports`