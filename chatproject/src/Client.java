// 1: setupActionListeners() 구현
// 3: Socket 설정
// 4: 데이터 송신 및 수신
// 6.1 수신 Thread화
//이건 수업용파일 내가 이해를 client
//새로운 클라이언트는 기존의 인원을 알아내야 한다. 기존인원은 새인원을 알아야한다.
//
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class Client extends mainGUI implements ActionListener { // 1: ActionListener

   // LoginGUI 변수
   private JFrame loginGUI = new JFrame("Login");
   private JPanel loginJpanel;
   private JTextField serverIP_tf;
   private JTextField serverPort_tf;
   private JTextField clientID_tf;
   private JButton loginBtn;

   

   // 3: 네트워크 변수
   Socket socket;
   String serverIP;
   int serverPort;

   // 4: 스트림 변수
   DataInputStream dis;
   DataOutputStream dos;

   // 6: 클라이언트들 변수
   String clientID;
   private Vector<String> clientVC=new Vector<String>();
   StringTokenizer st;


   public Client() {
      initLoginGUI();
      initMainGUI();
      setupActionListeners(); // 1
   }

   // 1: setupActionListeners() 구현
   public void setupActionListeners() {
      loginBtn.addActionListener(this);
      noteBtn.addActionListener(this);
      joinRoomBtn.addActionListener(this);
      createRoomBtn.addActionListener(this);
      sendBtn.addActionListener(this);
   }
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == loginBtn) {
         System.out.println("login Button Clicked");
         connectToServer(); // 3
      } else if (e.getSource() == noteBtn) {
         System.out.println("note Button Clicked");
         handleNoteSendButtonClick();        //7: 쪽지 보내기
      } else if (e.getSource() == joinRoomBtn) {
         System.out.println("Join Room Button Clicked");
      } else if (e.getSource() == createRoomBtn) {
         System.out.println("Create Room Button Clicked");
      } else if (e.getSource() == sendBtn) {
         System.out.println("Send Button Clicked");
      }
}
//서버 접속 창은 클라이언트 창에서 띄워야 되니까
   public void initLoginGUI() {
      loginGUI.setLayout(null);
      loginGUI.setBounds(100, 100, 310, 341);
      loginJpanel = new JPanel();
      loginJpanel.setBorder(new EmptyBorder(5, 5, 5, 5));

      loginGUI.setContentPane(loginJpanel); // 1
      loginJpanel.setLayout(null);

      JLabel lblNewLabel = new JLabel("서버 IP");
      lblNewLabel.setBounds(12, 31, 50, 15);
      loginJpanel.add(lblNewLabel);

      serverIP_tf = new JTextField();
      serverIP_tf.setBounds(109, 25, 164, 21);
      loginJpanel.add(serverIP_tf);
      serverIP_tf.setColumns(10);

      JLabel lblPort = new JLabel("서버 port");
      lblPort.setBounds(12, 82, 55, 15);
      loginJpanel.add(lblPort);

      serverPort_tf = new JTextField();
      serverPort_tf.setColumns(10);
      serverPort_tf.setBounds(109, 76, 164, 21);
      loginJpanel.add(serverPort_tf);

      clientID_tf = new JTextField();
      clientID_tf.setColumns(10);
      clientID_tf.setBounds(109, 126, 164, 21);
      loginGUI.add(clientID_tf);

      JLabel lblId = new JLabel("클라이언트 ID");
      lblId.setBounds(12, 132, 85, 15);
      loginJpanel.add(lblId);

      loginBtn = new JButton("로그인");
      loginBtn.setBounds(12, 174, 261, 23);
      loginJpanel.add(loginBtn);

      loginGUI.setVisible(true);

   }



   
   public void handleNoteSendButtonClick() {
      String dstClient = (String) clientJlist.getSelectedValue();
      String noteMsg = JOptionPane.showInputDialog(this,dstClient+"로 전송할 내용" ,"쪽지 내용 입력" ,JOptionPane.INFORMATION_MESSAGE );
      sendMsg("Note/" + dstClient + "/" + noteMsg); //서버로 쪽지 전송
   }

   // 3: Socket 설정
   public void connectToServer() {
      serverIP = serverIP_tf.getText().trim(); // IP 주소 가지고 옴
      serverPort = Integer.parseInt(serverPort_tf.getText().trim());

      try {
         socket = new Socket(serverIP, serverPort);
         System.out.println("Socket 연결 성공!!!!");
         initStream(); // 4
      } catch (UnknownHostException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   // 4: Stream 설정
   public void initStream() {

      try {
         dis = new DataInputStream(socket.getInputStream());
         dos = new DataOutputStream(socket.getOutputStream());
         clientID = clientID_tf.getText().trim();
         dos.writeUTF(clientID); // 서버에 자신의 ID를 전송
         //서버는 이후 클라이언트의 아이디를 받아 ClientInfo 객체를 만듬 
         //clientinfo는 서버 클래스에서 '클라이언트'역할을 하는 얘 메세지 수신,전송 쪽지보내기 등의 동작을 클라이언트 인포를 통해 시행한다
         //또한 서버는 초기 연결, 소켓스트림에 연결된 직후 기존 유저에게 새 유저를 알리고 
         //새유저에게는 기존 인원을 알린다. 그리고 유저목록을 업데이트한다

         clientVC.add(clientID);
 
         clientJlist.setListData(clientVC);//현재 자신이 가지고 있는 접속자 목록을 ui에 표시 
         //
         setTitle("클라이언트"+clientID);
               
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      //수신을 Thread 화
      Thread th = new Thread(new Runnable() {//서버의 메시지를 계속 받는 얘
         @Override
         public void run() {
              while(true) {
                  try {
                      String msg = dis.readUTF();
                      System.out.println("서버로부터 수신한 메시지 : " + msg);
                      parseMsg(msg);
                  } catch (IOException e) {               }
              }
            
         }
      });
      th.start();

   }
   ///send,recv는 이 클라이언트 2,3,4..객체의것을 불러와냐한다 즉 특정해야된다 스트림ㅇ ㄴ클라이언트당 한개씩 있기 때문이다 
   public void sendMsg(String msg) {
      try {
         dos.writeUTF(msg);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

  //프로토컬 분석처리
   public void parseMsg (String msg){
      st= new StringTokenizer(msg,"/");
      String protocol=st.nextToken();
      String message=st.nextToken();
      System.out.println("수신 메시지 프로토콜 =" +protocol+"메세지= "+message);

      if(protocol.equals("NewClient")){
         String clientID=message;//신규
         clientVC.add(clientID);
         clientJlist.setListData(clientVC);
      }else if(protocol.equals("OldClient")){//????왜 동작이같은데 분리했지?
         String clientID=message;//기존
         clientVC.add(clientID);
         clientJlist.setListData(clientVC);
      }else if(protocol.equals("Note")){
         String clientID=message;
         String noteMsg=st.nextToken();
         JOptionPane.showMessageDialog(this,noteMsg,clientID+"가 전송한 쪽지",JOptionPane.INFORMATION_MESSAGE);
      }
      

   } 


   public static void main(String[] args) {
      new Client();
   }

}
