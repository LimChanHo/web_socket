package com.iot.web;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@ServerEndpoint(value="/websocket/{clientId}")
public class WebSocketController{
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, List<String>> params;
    //접속자 관리(세션을 중복없이 일렬로 줄을 세우겠다)
    private static Map<String, Session> clients = Collections.synchronizedMap(new HashMap<String,Session>());
    //서버 접속시
    @OnOpen
    public void onOpen(@PathParam("clientId") String clientId,Session session) throws Exception{
    	params = session.getRequestParameterMap();
    	System.out.println(params);
        clients.put(clientId,session);
        System.out.println("session"+session);
    }
    //서버 종료시
    @OnClose
    public void onClose(Session session) throws Exception{
        clients.remove(session);
    }
    //메시지 수신시(클라이언트 -> 서버)
    @OnMessage
    public void onMessage(String msg, Session session) throws Exception{
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(msg);
        logger.info("object : "+obj);
        String nick = (String) obj.get("nick");
        synchronized(clients){
            //clients에 있는 각각의 세션을 client에 담는다.
        	
            for(String clientId : clients.keySet()){
            	final Session client = clients.get("clientId");
                //나 이외의 다른 클라이언트에게 보내는것
                if(!client.equals(session)){//나 이외에 다른 놈들한테 보내라
                      logger.info("clientId" + clientId);
                	  client.getBasicRemote().sendText(nick); 
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
        synchronized(clients){
            //clients에 있는 각각의 세션을 client에 담는다.
            for(String clientId : clients.keySet()){
            	final Session client = clients.get("clientId");
                client.getBasicRemote().sendText(msg);    
            }
        }
    }
}
