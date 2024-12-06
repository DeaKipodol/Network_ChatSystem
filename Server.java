// 1: setupActionListeners();
// 3: Socket 설정
// 4: 데이터 수신 및 송신
// 5: multi client
// 6: client 정보를 객체로 관리

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.StringTokenizer;
import java.net.Socket;

import java.util.Vector;


public class Server extends ServerGUI implements ActionListener {
//버전관리+ 전송시 잘받았는지 확인
   private static final long serialVersionUID = 1L;//

   // 네트워크 전역 변수
   ServerSocket ss;
   private Socket clientSocket;
   private int port = 12345; // 기본 포트 번호
   //클라이언트 관리
   //클라이언트단에는 클라이언트들을 보여 줘야한다-
   //->id를 등록한다->id는 키니까 클라이언트 각각 구분가능

   private Vector<ClientInfo> clientVC=new Vector<ClientInfo>();
   private Vector<RoomInfo> roomVC = new Vector<RoomInfo>(); // 방 정보 저장 벡터

   //분석용 변수
   StringTokenizer st;
   //프로토컬 새로 클라이언트가 접속하는경우 1단계 id를 전송=>이때는 프로토콜이 없다
   // 1.가존에 거압된 신입클라이언트의 접속을 하려한다고 알림
   // "NewClient/"+clientID
   //2.신규 클라이언트에게는 기존 멤버를 알린다 
   //"OldClient/"+c.clientID
   //클라이언트 a가b에게 메세지를 보낼때 
   //"Note/"+ clientID+"/"+note
   //"Note/"+ clientID+"/"+note
   public Server() {
      super();
      setupActionListeners();
   }

   // 1: setupActionListeners();
   public void setupActionListeners() {
      startBtn.addActionListener(this);
      stopBtn.addActionListener(this);
   }
      

   @Override
   public void actionPerformed(ActionEvent e) {

      if (e.getSource() == startBtn) {
         System.out.println("start Button clicked");
         startServer(); // 3
      } else if (e.getSource() == stopBtn) {
         System.out.println("stop Button clicked");
         //추가 서버정지
         stopServer();
      } 
      

   }

   //  socket 설정
   public void startServer() {
      
      try {
         int port = Integer.parseInt(port_tf.getText().trim());
         ss = new ServerSocket(port);

         textArea.append("서버가 포트 " + port + "에서 시작되었습니다.\n");
         //서버시작을 알리는 로그출력
         startBtn.setEnabled(false); 
         // ** Disable start button after starting the server.
         port_tf.setEditable(false);
         // ** Disable port input after starting.포트입력을 막는다
         stopBtn.setEnabled(true); 
         // ** Enable stop button after starting.
         waitForClientConnection(); // 5

      } catch (IOException e) {
         textArea.append("서버 시작 오류: " + e.getMessage() + "\n"); // ** Log server start error.
      }catch (NumberFormatException e) { 
         textArea.append("잘못된 포트 번호입니다.\n");
          // ** Log invalid port number error.
   }
   }
   private void stopServer() { 
      for (ClientInfo c : clientVC) { 
         c.sendMsg("ServerShutdown/Bye"); 
         // ** Notify clients of server shutdown.
         try { 
            c.closeStreams(); // ** Close client streams.
        } catch (IOException e) { 
         e.printStackTrace();
          // ** Log any exceptions during closing process.
      }
  }
  try { 
   if (ss != null && !ss.isClosed()) { 
      ss.close(); // ** Close the server socket.
      textArea.append("서버가 포트 " + port + "에서 중지되었습니다.\n"); // ** Log server stop message.

   } 
   roomVC.removeAllElements(); 
   // ** Clear room information.
       } catch (IOException e) { 
           e.printStackTrace(); // ** Log any exceptions during closing process.
       }
       startBtn.setEnabled(true); // ** Enable start button again.
       port_tf.setEditable(true); // ** Allow editing of the port field again.
       stopBtn.setEnabled(false); // ** Disable stop button after stopping the server.
   }

   // 5:
   public void waitForClientConnection() {

      // Thread 생성
      Thread th = new Thread(new Runnable() {
         

         @Override
         public void run() {
            while (!ss.isClosed()) {            // 6: 무한 loop
               try {
                  textArea.append("클라이언트 Socket 접속 대기중\n");
                  clientSocket = ss.accept(); // 사용자 접속 대기, 무한 대기
                  textArea.append("클라이언트 Socket 접속 완료\n");

                  // 6: ClientInfo 객체 생성 
                  ClientInfo client = new ClientInfo(clientSocket);//이때 소통이라는 동작이가능한 클라이언트 객체를 만드는 것.
                  client.start(); // 객체의 스레드 실행

               } catch (IOException e) {
                   if (!ss.isClosed()) {  
               textArea.append("클라이언트 연결 수락 중 오류 발생: " + e.getMessage() + "\n");  
                  e.printStackTrace();
               }
            }
            }// while 끝
         } 
      });

      th.start(); // 쓰레드 실행

   }
//위의 th.start()가 실행되면 클라이언트 인포가 생성된다-> 생성되면서 클라이언트의 정보/지금 생성된 클라이언트id-프로토콜로 수신/연결된 소켓 스트림정보//서버와 클라이언트가 할수있는 동작정의

   public class ClientInfo extends Thread {
      
      DataInputStream dis;
      DataOutputStream dos;
      private Socket clientSocket;  
      private String clientID = "";
      private String roomID = "";

      public ClientInfo(Socket socket) {//클라이어언트1의 소텟 클라이언트2의 소켓...
         
         initStream(socket);
      }

      public void initStream(Socket socket) {
         try {
            this.clientSocket = socket;
            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());
            initNewClient(); 
         } catch (IOException e) {  
            textArea.append("Error in communication: " + e.getMessage() + "\n");
         }
      }
      private void initNewClient() {   
         while (true) {
            try {
            //첫번째에 오는 단어는 id
               clientID = dis.readUTF(); 
            //id중복여부 확인
               boolean isDuplicate = false;
                // clientVC의 각 요소를 순회   
           for (ClientInfo c : clientVC) {
               if (c.clientID.equals(clientID)) { // clientID 비교
                     isDuplicate = true; // 중복 감지
                        break; // 중복 발견 시 루프 종료
   }
                  }
               //중복여부판단후처리
               //중복이라면 중복이라 알리고 스트림을 닫음.
               //그리고 새로 스트림을 만들게함

               if (isDuplicate) {
                 sendMsg("DuplicateClientID");
                 closeStreams();
                 break;
                 
              } else {//중복이 아니라면
                 sendMsg("GoodClientID");
                  //새롭게 들어온 클라이언트를 서버 로그창에 띄움
                 textArea.append("new Client: " + clientID + "\n");

                 for (ClientInfo c:clientVC) {
                  //c는 기존 접속client, 걔에게 현재 연결된 클라이언트id를 건넨다->기존 클라이언트는 
                  //NEW Client라는 porotol을 받으면 자신의 유저 접속 목록에 새로 추가한다
                  
                    sendMsg("OldClient/" + c.clientID);
                 }
                 //모든 사용자에게 알림
                 broadCast("NewClient/" + clientID);
                  ////현재 방의 목록을 지금 만든 클라이언트에게 현재 방목록을 알림
                 for (RoomInfo r : roomVC) {
                    sendMsg("OldRoom/" + r.roomID);
                 }
                  //방목록 다업데이트 되었다 알림
                 sendMsg("RoomJlistUpdate/Update");
                  //자기소개 끝나면 이제 기존 목록에 추가해줌
                 clientVC.add(this);
                 broadCast("ClientJlistUpdate/Update");
                 break;
              }
            } catch (IOException e) {
               textArea.append("통신 중 오류 발생: " + e.getMessage() + "\n");
               break;
            }
         }
      }

      public void sendMsg(String msg) {
         try {
            dos.writeUTF(msg);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            textArea.append("메시지 전송 오류: " + e.getMessage() + "\n");
            e.printStackTrace();
         }
      }

      public void parseMsg(String str) {
         textArea.append(clientID + " 사용자로부터 수신한 메시지: " + str + "\n");
         System.out.println(clientID + " 사용자로부터 수신한 메시지: " + str);
         st = new StringTokenizer(str, "/");
         String protocol = st.nextToken();//아이디 초반에 확인
         //이제 프로토콜을 받음
         String message ="";//그다음 추가적인 토큰이 있다면 그것은 메세지이다. 메세지가 없으면 읽을 필요 없음
         if (st.hasMoreTokens()) {
            message = st.nextToken();
        }
        switch (protocol) {
         case "Note":
             handleNoteProtocol(st, message);
             break;
         case "CreateRoom":
             handleCreateRoomProtocol(message);
             break;
         case "JoinRoom":
             handleJoinRoomProtocol(st, message);
             break;
         case "SendMsg":
             handleSendMsgProtocol(st, message);
             break;
         case "ClientExit":
             handleClientExitProtocol();
             break;
         case "ExitRoom":
             handleExitRoomProtocol(message);
             break;

         case "GameStart":
             
             break;    
         default:
             log("알 수 없는 프로토콜: " + protocol);
             break;
      }
      //프로토콜처리
   }
   
   private void handleNoteProtocol(StringTokenizer st, String recipientID) {
      String note = st.nextToken();

      for (ClientInfo c : clientVC) {
          if (c.clientID.equals(recipientID)) {
              c.sendMsg("Note/" + clientID + "/" + note);
              break;
          }
      }
  }
  private void handleCreateRoomProtocol(String roomID) {

   boolean roomExists = false;
   for (RoomInfo r : roomVC) {
       if (r.roomID.equals(roomID)) {
           roomExists = true;
           break;
       }
   }
   if (roomExists) {
       sendMsg("CreateRoomFail/OK");
   } else {
       RoomInfo r = new RoomInfo(roomID, this);
       roomVC.add(r);
       this.roomID = roomID;
       sendMsg("CreateRoom/" + roomID);
       broadCast("NewRoom/" + roomID);
       broadCast("RoomJlistUpdate/Update");
   }
}

   private void handleJoinRoomProtocol(StringTokenizer st, String roomID) {
      for (RoomInfo r : roomVC) {
          if (r.roomID.equals(roomID)) {
              r.broadcastRoomMsg("JoinRoomMsg/가입/***" + clientID + "님이 입장하셨습니다.********");
              r.RoomClientVC.add(this);
              this.roomID = roomID; 
              sendMsg("JoinRoom/" + roomID);
                 break;
         }
      }
   }
   private void handleSendMsgProtocol(StringTokenizer st, String roomID) {
      String sendMsg = st.nextToken();
      for (RoomInfo r : roomVC) {
          if (r.roomID.equals(roomID)) {
              r.broadcastRoomMsg("SendMsg/" + clientID + "/" + sendMsg);
          }
      }
  }
  private void handleClientExitProtocol() {
   try {
       closeStreams();
       clientVC.remove(this);
       if (clientSocket != null && !clientSocket.isClosed()) {
           clientSocket.close();
           textArea.append(clientID + " Client Socket 종료.\n");
       }

       broadCast("ClientExit/" + clientID);
       broadCast("ClientJlistUpdate/Update");

   } catch (IOException e) {
      logError("사용자 로그아웃 중 오류 발생", e);
    }
}
private void handleExitRoomProtocol(String roomID) {
   //roomID = roomID;
  this.roomID = "";
   log(clientID + " 사용자가 " + roomID + " 방에서 나감");

   for (RoomInfo r : roomVC) {
       if (r.roomID.equals(roomID)) {
           r.broadcastRoomMsg("ExitRoomMsg/탈퇴/***" + clientID + "님이 채팅방에서 나갔습니다.********");
           r.RoomClientVC.remove(this);
           if (r.RoomClientVC.isEmpty()) {
               roomVC.remove(r);
               broadCast("RoomOut/" + roomID);
               broadCast("RoomJlistUpdate/Update"); 
           }
           break;
       }
   }
}
private void broadCast(String str) {
   for (ClientInfo c : clientVC) {
       c.sendMsg(str);
   }
}
private void log(String message) {
   System.out.println(clientID + ": " + message);
}
private void logError(String message, Exception e) {
   System.err.println(clientID + ": " + message);
   e.printStackTrace();
}
public void closeStreams() throws IOException {
   if (dos != null) {
       dos.close();
   }
   if (dis != null) {
       dis.close();
   }
   if (clientSocket != null && !clientSocket.isClosed()) {
      clientSocket.close();
       textArea.append(clientID + " Client Socket 종료.\n");
   }
}
     /*  public void recvMsg(String msg) {
         textArea.append(clientID + " 사용자로부터 수신한 메시지: " + msg + "\n");
         System.out.println(clientID + " 사용자로부터 수신한 메시지: " + msg);
      } 이제 서버에서 수신은 항상 지속되야 한다 그것도 프로그램 흐름과 관계없이 따라서 클라이언트의 동작을 정의하는 클라이언트인포를 정의 하는 시점에서
       클라이언트 인포에 수신을 하는 스레드를 만들어야 된다=> 그래서 run에 수신을 정의*/
//수신 스레드
      public void run() {
         String msg="";
         while (true) {//언제 받을 지모르니까 항상 열어 두어야 한다.
            try {
               
               msg = dis.readUTF();
               parseMsg(msg);
            } catch (IOException e) {
               handleClientExitProtocol(); //???
            }
         }

      }

   }
   class RoomInfo {

      private String roomID;
      String creatorID; // 출제자 ID
      private Vector<ClientInfo> RoomClientVC;   //

      public RoomInfo(String roomID, ClientInfo creator) {

          this.roomID=roomID;
          this.creatorID = creator.clientID;

          this.RoomClientVC=new Vector<ClientInfo>();
          this.RoomClientVC.add(creator);

      }   

   public void broadcastRoomMsg(String message){   

      for(ClientInfo c: RoomClientVC){   

          c.sendMsg(message);

      }   

  }
}   
   public static void main(String[] args) {
      new Server();
   }

}
