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


   // 네트워크 전역 변수
   ServerSocket ss;
   //클라이언트 관리
   //클라이언트단에는 클라이언트들을 보여 줘야한다-
   //->id를 등록한다->id는 키니까 구분가능

   private Vector<ClientInfo> clientVC=new Vector<ClientInfo>();

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
         startServer();
      } else if (e.getSource() == stopBtn) {
         System.out.println("stop Button clicked");
      }

   }

   //  socket 설정
   public void startServer() {
      int port = Integer.parseInt(port_tf.getText().trim());
      try {
         ss = new ServerSocket(port);

         textArea.append("서버가 포트 " + port + "에서 시작되었습니다.\n");

         waitForClientConnection(); // 5

      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   // 5:
   public void waitForClientConnection() {

      // Thread 생성
      Thread th = new Thread(new Runnable() {
         Socket clientSocket;

         @Override
         public void run() {
            while (true) {            // 6: 무한 loop
               try {
                  textArea.append("클라이언트 Socket 접속 대기중\n");
                  clientSocket = ss.accept(); // 사용자 접속 대기, 무한 대기
                  textArea.append("클라이언트 Socket 접속 완료\n");

                  // 6: ClientInfo 객체 생성 
                  ClientInfo client = new ClientInfo(clientSocket);//이때 소통이라는 동작이가능한 클라이언트 객체를 만드는 것.
                  client.start(); // 객체의 스레드 실행

               } catch (IOException e) {
                  e.printStackTrace();
               }
            }// while 끝
         } 
      });

      th.start(); // 쓰레드 실행

   }
//위의 th.start()가 실행되면 클라이언트 인포가 생성된다-> 생성되면서 클라이언트의 정보/지금 생성된 클라이언트id-프로토콜로 수신/연결된 소켓 스트림정보//서버와 클라이언트가 할수있는 동작정의

   public class ClientInfo extends Thread {
      private Socket clientSocket;
      DataInputStream dis;
      DataOutputStream dos;
      private String clientID = "";

      public ClientInfo(Socket socket) {//클라이어언트1의 소텟 클라이언트2의 소켓...
         clientSocket = socket;
         initStream();
      }

      public void initStream() {
         try {
            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());

            // client가 전송한 ID를 수신
            String msg = dis.readUTF();
            textArea.append(msg + " 클라이언트 접속\n");
            clientID=msg;

         
            
            //기존 클라이언트에게 가입자 정보 전송
            for(ClientInfo c:clientVC){
               c.sendMsg("NewClient/"+clientID);//c는 기존 접속client, 걔에게 현재 연결된 클라이언트id를 건넨다->기존 클라이언트는 NEW Client라는 porotol을 받으면 자신의 유저 접속 목록에 새로 추가한다
               sendMsg("OldClient/"+c.clientID);//지금 과정을 이해해야됨-> 현재 새 클라이언트의 객체(동작)를 만드는 중 즉 this=새클라이언트
               //현재 만들어진 클라이언트에게는 기존 접속자를 알린다
               //a,b,c기존 d추가라고 가정
               //a<-d,d<-a
               //b<-d,d<-b
               //c<-d,d<-c
            }//=>자기소개 하는 것
            clientVC.add(this);//서버속 클라이언트 목록에 마지막으로 새로 추가한 클라이언트를 추가.
            
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }

      }

      public void sendMsg(String msg) {
         try {
            dos.writeUTF(msg);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      public void parseMsg(String msg) {
         st = new StringTokenizer(msg, "/");
         String protocol = st.nextToken();
         String message = st.nextToken();
         System.out.println("수신 메시지 프로토콜 = " + protocol + " 메시지 = " + message);

         if (protocol.equals("Note")) {
            String dstClientID = message; // 신규 clientID
            String noteMsg = st.nextToken();
            handleNoteProtocol(noteMsg, dstClientID);//클라이언트가 서버에게 쪽지를 보내달라고 요청했다면
            //서버는 원하는 목적지에 보내줘야 한다 

         }
      }
      public void handleNoteProtocol(String noteMsg, String dstClientID) {
         // 쪽지 목적지로 전송 준비
         for (ClientInfo c : clientVC) {//접속목록자에서 쪽지를 보내기 원하는 유저가 적은 목적지 클라이언트를 찾아
            if (c.clientID.equals(dstClientID)) {
               c.sendMsg("Note/" + clientID + "/" + noteMsg);//그 클라이언트를 c라하고 해당 클라이언트의 스트림에 메세지를 보낸다.
               break;
            }
         }
      }

     /*  public void recvMsg(String msg) {
         textArea.append(clientID + " 사용자로부터 수신한 메시지: " + msg + "\n");
         System.out.println(clientID + " 사용자로부터 수신한 메시지: " + msg);
      } 이제 서버에서 수신은 항상 지속되야 한다 그것도 프로그램 흐름과 관계없이 따라서 클라이언트의 동작을 정의하는 클라이언트인포를 정의 하는 시점에서
       클라이언트 인포에 수신을 하는 스레드를 만들어야 된다=> 그래서 run에 수신을 정의*/
//수신 스레드
      public void run() {
         while (true) {//언제 받을 지모르니까 항상 열어 두어야 한다.
            try {
               String msg = dis.readUTF();
               textArea.append(clientID + " 사용자로부터 수신한 메시지: " + msg + "\n");
               parseMsg(msg);
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }

      }

   }

   public static void main(String[] args) {
      new Server();
   }

}
