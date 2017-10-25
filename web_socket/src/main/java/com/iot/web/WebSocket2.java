package com.iot.web;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value="/websocket2")
public class WebSocket2{
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //접속자 관리(세션을 중복없이 일렬로 줄을 세우겠다)
    private static Set<Session> clients1 = Collections.synchronizedSet(new HashSet<Session>());
    //서버 접속시
    @OnOpen
    public void onOpen(Session session) throws Exception{
        clients1.add(session);
        logger.info("서버접속 : "+session.getId());
        logger.info("접속자 수 : "+clients1.size());
        String msg="우리 방에 오신 걸 환영 합니다.";
        systemMsg("시스템 : 새로운 사람이 입장 했습니다. 현재 접속자 수("+clients1.size()+")");
        pushMessage(session,msg);
    }
    //서버 종료시
    @OnClose
    public void onClose(Session session) throws Exception{
        logger.info("서버 종료");
        clients1.remove(session);
        logger.info("접속자 수 : "+clients1.size());
        systemMsg("시스템 : 누군가가 퇴장 했습니다. 현재 접속자 수("+clients1.size()+")");
    }
    //메시지 수신시(클라이언트 -> 서버)
    @OnMessage
    public void onMessage(String msg, Session session) throws Exception{
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(msg);
        logger.info("object : "+obj);
        //{"nick":"김지훈","msg":"안녕"}
        String nick = (String) obj.get("nick");
        String message = (String) obj.get("msg");        
        logger.info(nick+"> "+message);        
        String text = nick+"> "+message;
        //순차적으로 처리하라
        synchronized(clients1){
            //clients1에 있는 각각의 세션을 client에 담는다.
            for(Session client : clients1){
                //나 이외의 다른 클라이언트에게 보내는것
                if(!client.equals(session)){//나 이외에 다른 놈들한테 보내라
                    client.getBasicRemote().sendText(text);    
                }
            }
        }        
    }
    //메시지 발신(서버->클라이언트)
    public void pushMessage(Session session, String msg) throws Exception{        
        session.getBasicRemote().sendText(msg);        
    }
    //시스템 메시지
    public void systemMsg(String msg) throws Exception{
        //순차적으로 처리하라
        synchronized(clients1){
            //clients1에 있는 각각의 세션을 client에 담는다.
            for(Session client : clients1){
                client.getBasicRemote().sendText(msg);    
            }
        }
    }
}


