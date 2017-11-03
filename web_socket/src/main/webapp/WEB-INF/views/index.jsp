<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.0.0.min.js"></script>
</head>
<style>
textarea {
	width: 200px;
	height: 200px;
}
</style>
<body>
	<h3>웹 소켓 페이지</h3>
	<!-- 액션이 없다. -->
	<form>
		내이름 : <input id="nick" type="text" /> <br /> 상태창 :
		<textarea id="monitor"></textarea>
		<br /> <input type="button" value="소켓연길" onClick="openCon()" /> 
		<input type="button" value="연결신청하기" onClick="sendMsg()" /> <input
			type="button" value="웹소켓종료해버리기" onClick="disConn()" />
	</form>
</body>
<script>
	var generateRandom = function(min, max) {
		var ranNum = Math.floor(Math.random() * (max - min + 1)) + min;
		return ranNum;
	}
	//접속 URL
	var webSocket;
	var content = document.getElementById("monitor");
	
	function openCon(){
		var id = $("#nick").val();
		var url = "ws://${pageContext.request.contextPath}/localhost/websocket/" + id
		webSocket = new WebSocket(url);//웹 소켓 객체 생성    
		//웹소켓 연결됐을 때
		webSocket.onopen = function(e) {
			console.log(e);
			content.value += "웹소켓 연결...\n";
		}
		//웹소켓 끊겼을 때
		webSocket.onclose = function(e) {
			console.log(e);
			content.value += "웹소켓 끊김...\n";
		}
		//메시지 수신
		webSocket.onmessage = function(e) {
			if (confirm(e.data + "님과 정말 연결하시겠습니까???") == true) { //확인
//	 			document.getElementById("#라벨태크채팅방ID값준다음").val("#신청유저NUM값");
//	 		    createButtonClick();

			}
		}
		
	}
	//서버에 메시지 전송
	function sendMsg() {
		var obj = {};
		obj.nick = $("#nick").val();
		webSocket.send(JSON.stringify(obj));
// 		document.getElementById("#라벨태크채팅방ID값준다음").val("#신청유저NUM값");
// 	    createButtonClickEvent();
	}
	//웹소켓 종료
	function disConn() {
		webSocket.close();
	}
</script>
<script>
     function signOut() {
        document.location.href = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost/";
     }
</script>
</html>
