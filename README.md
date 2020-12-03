# 2020 서울시립대학교 컴퓨터과학부 Capstone

OCR을 활용한 메뉴판 번역 어플리케이션

1. 위 프로젝트는 Android 6.0(Marshmallow) 환경에서 작성되었습니다.

2. 위 프로젝트는 opencv 4.4.0을 사용합니다. 만약 프로젝트 opencv가 연동되지 않는다면 새로 opencv를 설치하고 sdk에 등록하세요.

   이후 CMakeList.txt 파일의 경로를 수정이 필요합니다.
   
   set(pathPROJECT C:/Users/user/AndroidStudioProjects/openCVOCRProject) 해당 경로를 프로젝트를 저장한 경로로 수정하세요.
   
   
   이후 연동한 opencv의 org.opencv.android.CameraBridgeViewBase의 deliverAndDrawFrame 메소드 수정이 필요합니다. (openCV의 기본 화면을 회전시키는 용도입니다.)
   
  
3. 네이버 외부 Gateway 및 OCR 사용 신청이 필요합니다. 아래의 주소를 참고하세요.

   https://docs.ncloud.com/ko/ocr/ocr-1-4.html

   이후 MainActivity의 다음 변수에 자신의 전용 게이트웨이 주소 및 인증키를 등록하세요.

   // 네이버 CLOVA OCR API 사용 전용 게이트웨이 및 인증키

   final String ocrApiGwUrl = "";

   final String ocrSecretKey = "";

4. 위 프로젝트는 Google Translation API를 사용합니다.

5. Google Custom Search API를 사용하여 이미지 검색 기능을 사용합니다.

    Custom Search Key가 필요합니다. 아래의 주소를 참고하세요.

    https://developers.google.com/custom-search/v1/overview

    이후 SearchClient의 API Key 에 자신의 인증키를 등록하세요.
    

6. 위 프로젝트는 Firebase 데이터베이스를 사용합니다.
    
7. 위 프로젝트는 Facebook 로그인 연동을 지원합니다.

ScreenShot
=============================
![image](https://user-images.githubusercontent.com/30149272/98258485-47961000-1fc4-11eb-8dce-c87603ea6d44.png)

![image](https://user-images.githubusercontent.com/30149272/98444411-e3568600-2154-11eb-8103-96a35ccfe599.png)
