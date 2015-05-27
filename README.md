### 2. 서버가 시작하는 시점에 부모 ApplicationContext와 자식 ApplicationContext가 초기화되는 과정에 대해 구체적으로 설명해라.
* 톰캣이 실행되면 web.xml을 읽어서 contextLoaderListener를 설정 하였는지 확인한다. contextListener의 contextInitialized method를 통해 value로 지정된 xml파일을 통해 부모 ApplicationContext를 생성한다.
* 부모 context를 정의하는 applicationContext.xml에는 모든 서비스에서 사용하는 bean 객체를 만들거나 설정을 한다.
* 그 다음 web.xml의 DispatcherServlet 설정을 확인, [servlet이름]-servlet.xml 혹은 지정한 이름의 xml 파일을 통해 자식 ApplicationContext를 생성한다. 



### 3. 서버 시작 후 http://localhost:8080으로 접근해서 질문 목록이 보이기까지 흐름에 대해 최대한 구체적으로 설명하라. 
* 모든 요청은 next DispatcherServlet이 가져오고, 이 서블릿의 context에는 User, Answer, Question Controller들이 RequestMapping annotation의 value에 맞게 연결되어 관리되고 있다.
* localhost:8080으로 접근하면 next DispatcherServlet은 URI를 "/"로 인식하고, handlerMapping을 통해 RequestMapping value가 "/"이거나 "/question"인 QuestionController의 인스턴스를 전달받는다. 
* QuestionController에서 list method가 URI="/" method="GET"으로 설정되어 있으므로, list method가 실행되qnaService의 findAll method를 호출한다. 
* qnaService는 questionDao의 findAll method를 호출하고, questionDao는 QUESTIONS 테이블의 모든 question을 id의 내림차순으로 검색한 result set을 list로 리턴한다.
* questionController는 리턴 받은 question의 list를 setAttribute하고, view로 "qna/list"를 리턴한다.
* 부모 context에서 설정된 viewResolver는 /WEB-INF/jsp/qna/list.jsp를 리턴하고, 클라이언트는 list.jsp에 attribute로 설정된 question list의 데이터를 넣어서 화면에 질문목록을 보여준다. 



### 9. UserService와 QnaService 중 multi thread에서 문제가 발생할 가능성이 있는 소스는 무엇이며, 그 이유는 무엇인가?
* QnaService의 scope는 prototype으로 요청이 있을 때마다 인스턴스를 새로 생성하지만, 이 QnaService를 inject하는 QnaController의 scope는 기본 scope이기 때문에 새로 만들어진 QnaService가 inject 되지 않고, 계속 처음 만든 QnaController 인스턴스만 사용하게 된다. 따라서, 전역변수인 Question이 멀티스레드 상황에서 이전 요청의 question을 보여주는 문제가 발생한다.
QnaService의 scope도 prototype으로 바꾸어 요청에 따라 새로운 인스턴스로 사용하면 이런 문제를 해결할 수 있지만, 요청이 많아질 경우 너무 많은 인스턴스를 생성하는 성능상의 이슈가 발생한다. (그러나 여러가지 방법을 알았으므로 적용해봄)

* scope prototype으로 발생하는 성능상의 이슈를 해결할 수 있는 방법은 UserService에서처럼 existedUser를 클래스의 전역 변수 대신, 메소드의 지역 변수로 사용하는 것이다. 이럴경우 메소드가 끝남과 동시에 User가 소멸되면서 멀티 스레드 상황에서도 existedUser 변수의 공유가 없기 때문에 요청에 맞는 User를 return할 수 있으며, 싱글톤과 같이 계속 같은 UserService 인스턴스를 사용하기 때문에 성능상의 이슈도 발생하지 않는다.