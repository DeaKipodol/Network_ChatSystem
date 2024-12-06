import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;



public class Client2024 extends JFrame implements ActionListener, KeyListener {
    private static final long serialVersionUID = 2L;

    // Login GUI 변수
    private JFrame loginGUI = new JFrame("Login"); // 11-19
    private JPanel loginJpanel;
    private JTextField serverIP_tf;
    private JTextField serverPort_tf;
    private JTextField clientID_tf; // 클라이언트 ID
    private JLabel img_Label;
    private JButton loginBtn; // 11-13
    private String serverIP; // 11-14
    private int serverPort; // 11-14
    private String clientID; // 11-20 //클라이언트 ID

    // Main GUI 변수
    private JPanel contentPane;
    private JList<String> clientJlist = new JList(); // 전체 접속자 명단, 첫번째는 자기 자신 //11-20
    private JList<String> roomJlist = new JList(); // 11-21
    private JButton noteBtn = new JButton("쪽지 보내기");
    private JButton joinRoomBtn = new JButton("채팅방 참여");
    private JButton createRoomBtn = new JButton("방 만들기");
    private JButton clientExitBtn = new JButton("채팅종료");
    private JButton startGameBtn = new JButton("게임 시작"); // ** 게임 시작 버튼 추가
    private JTextField msgTf = new JTextField(); // ** 채팅 메시지 입력 필드
    private JButton sendBtn = new JButton("전송"); 
    // 클라이언트 관리
    private Vector<String> clientVC = new Vector<>(); // 11-20
    private Vector<String> roomClientVC = new Vector<>(); // 11-21
    private String myRoomID = ""; // 내가 참여한 채팅방 11-28

    // network 변수
    private Socket socket; // 11-14
    private DataInputStream dis;
    private DataOutputStream dos;

    // 기타
    StringTokenizer st;
    // private boolean stopped = false;
    private boolean socketEstablished = false;

    public Client2024() {
        initializeLoginGUI();
        initializeMainGUI();
        addActionListeners(); // 11-13
    }

    void initializeLoginGUI() {
        loginGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 1
        loginGUI.setBounds(100, 100, 385, 541); // 1
        loginJpanel = new JPanel();
        loginJpanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        loginGUI.setContentPane(loginJpanel); // 1
        loginJpanel.setLayout(null);

        JLabel lblNewLabel = new JLabel("Server IP");
        lblNewLabel.setFont(new Font("굴림", Font.BOLD, 20));
        lblNewLabel.setBounds(12, 244, 113, 31);
        loginJpanel.add(lblNewLabel);

        serverIP_tf = new JTextField();
        serverIP_tf.setBounds(135, 245, 221, 33);
        loginJpanel.add(serverIP_tf);
        serverIP_tf.setColumns(10);

        JLabel lblServerPort = new JLabel("Server Port");
        lblServerPort.setFont(new Font("굴림", Font.BOLD, 20));
        lblServerPort.setBounds(12, 314, 113, 31);
        loginJpanel.add(lblServerPort);

        serverPort_tf = new JTextField();
        serverPort_tf.setColumns(10);
        serverPort_tf.setBounds(135, 312, 221, 33);
        loginJpanel.add(serverPort_tf);

        JLabel lblId = new JLabel("ID");
        lblId.setFont(new Font("굴림", Font.BOLD, 20));
        lblId.setBounds(12, 376, 113, 31);
        loginJpanel.add(lblId);

        clientID_tf = new JTextField();
        clientID_tf.setColumns(10);
        clientID_tf.setBounds(135, 377, 221, 33);
        loginJpanel.add(clientID_tf);

        loginBtn = new JButton("Login"); // 11-13
        loginBtn.setFont(new Font("굴림", Font.BOLD, 20));
        loginBtn.setBounds(12, 450, 344, 44);
        loginJpanel.add(loginBtn);

        try {
            ImageIcon im = new ImageIcon("images/다람쥐.jpg");
            img_Label = new JLabel(im);
            img_Label.setBounds(12, 23, 344, 154);
            loginJpanel.add(img_Label);
        } catch (Exception e) {
            // 이미지 로딩에 실패한 경우 예외 처리
            JOptionPane.showMessageDialog(this, "image.", "Error", JOptionPane.ERROR_MESSAGE);
            // 이 부분에 적절한 오류 처리를 추가하세요.
        }

        loginGUI.setVisible(true); // 1
    }

    void initializeMainGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(600, 100, 510, 460);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel 접속자 = new JLabel("전체 접속자");
        접속자.setBounds(33, 46, 73, 15);
        contentPane.add(접속자);
        
      clientJlist.setBounds(12,71 ,108 ,174); 
      contentPane.add(clientJlist); 

      clientExitBtn.setBounds(12 ,275 ,108 ,23); 
      contentPane.add(clientExitBtn); 
      
      noteBtn.setBounds(12 ,320 ,108 ,23); 
      contentPane.add(noteBtn); 

      JLabel 채팅방 = new JLabel("채팅방목록"); 
      채팅방.setBounds(278 ,20 ,97 ,15); 
      contentPane.add(채팅방); 
      
      roomJlist.setBounds(174 ,45 ,271 ,298); 
      contentPane.add(roomJlist); 

      joinRoomBtn.setBounds(326 ,353 ,108 ,23); 
      contentPane.add(joinRoomBtn); 
      joinRoomBtn.setEnabled(false);
      
      createRoomBtn.setBounds(184 ,353 ,108 ,23); 
      contentPane.add(createRoomBtn);
      this.setVisible(false);

      ////////////////////////////////////
      startGameBtn.setBounds(184, 385, 250, 23); // ** 게임 시작 버튼 위치 설정
      contentPane.add(startGameBtn);
      startGameBtn.setEnabled(false); // ** 기본적으로 비활성화 상태

      sendBtn.setBounds(184, 420, 100, 23); // ** 전송 버튼 추가
      sendBtn.setEnabled(false);
      contentPane.add(sendBtn);

      msgTf.setBounds(12, 385, 150, 23); // ** 메시지 입력 필드 추가
      msgTf.setEditable(false);
      contentPane.add(msgTf);

      this.setVisible(false);
    }

   void addActionListeners() { // ** Simplified action listener registration for clarity
    loginBtn.addActionListener(this);
    noteBtn.addActionListener(this);
    joinRoomBtn.addActionListener(this);
    createRoomBtn.addActionListener(this);
    clientExitBtn.addActionListener(this);
    roomJlist.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getClickCount() == 2) { // 더블 클릭 시 동작
                String selectedRoom = roomJlist.getSelectedValue();
                if (selectedRoom != null && !selectedRoom.isEmpty()) {
                    GameMain2 gameRoom = new GameMain2(selectedRoom);
                    gameRoom.setVisible(true);
                }
            }
        }
    });
    /////////게임 로직
    startGameBtn.addActionListener(this); // ** 게임 시작 버튼 리스너 추가
    sendBtn.addActionListener(this); // ** 채팅 메시지 전송 버튼 리스너 추가
    msgTf.addKeyListener(this); // ** 채팅 메시지 입력 필드의 키 리스너 추가
   }

   public void connectToServer() {
       if (!socketEstablished) {
           try {
               serverIP = serverIP_tf.getText().trim();
               serverPort = Integer.parseInt(serverPort_tf.getText().trim());
               socket = new Socket(serverIP, serverPort);

               dis = new DataInputStream(socket.getInputStream()); 
               dos = new DataOutputStream(socket.getOutputStream());
               socketEstablished = true;

               sendMyClientID(); // 클라이언트 ID 전송.
           } catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(this,"잘못된 포트 번호입니다.","오류", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
           } catch (IOException e) {
               JOptionPane.showMessageDialog(this,"서버에 연결할 수 없습니다.","연결 오류", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
           }
       }
   }

   void sendMyClientID() {
       clientID = clientID_tf.getText().trim(); 
       sendMsg(clientID);

       try {
           String msg = dis.readUTF();
           if ("DuplicateClientID".equals(msg)) {
               JOptionPane.showMessageDialog(this,"이미 사용중인 ID입니다.","중복 ID", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
               clientID_tf.setText("");
               clientID_tf.requestFocus();
               socketEstablished = false;
               socket.close();
               System.exit(0);// 애플리케이션 종료.  
              

           } else {
               InitializeAndRecvMsg();
           }
       } catch (IOException e) {
           JOptionPane.showMessageDialog(this,"서버로부터 응답을 받는 중 오류가 발생했습니다.","통신 오류", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
       }
   }

   void InitializeAndRecvMsg() {
       this.setVisible(true);
       this.loginGUI.setVisible(false);

       clientVC.add(clientID);
//      clientJlist.setListData(clientVC);// JLIST로 화면에 출력.
       setTitle(clientID);// ** Set title to the client's ID.

       new Thread(() -> {  
           try {
               String msg;  
               while (true) {  
                   msg = dis.readUTF();  
                   System.out.println("서버로부터 받은 메시지: " + msg);
                   parseMsg(msg);
               }
           } catch (IOException e) {  
               handleServerShutdown();  
           }  
       }).start();
   }

   void sendMsg(String msg) {  
       try {  
           dos.writeUTF(msg);  
       } catch (IOException e) {  
           JOptionPane.showMessageDialog(this,"메시지 전송 중 오류가 발생했습니다.","오류", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
       }  
   }

   void parseMsg(String msg) { // ** Process received messages based on protocol.
       st = new StringTokenizer(msg,"/");
       String protocol = st.nextToken();
        String message = st.hasMoreTokens() ? st.nextToken() : "";

       switch (protocol) {
           case "NewClient":
           case "OldClient":
               addClientToList(message);// 서버가 등록할 정보만 전송한다.
               break;
            case "GameStart": // ** 게임 시작 메시지 처리
               activateGameButtons(); // ** 게임 시작 시 버튼 활성화
               break;

           case "Note":// 쪽지 처리.
               String note=st.nextToken();
               showMessageBox(note,message + "님으로부터 쪽지");
               break;

           case "CreateRoom":
               handleCreateRoom(message);
               break;

           case "NewRoom":
           case "OldRoom":// 방 목록 업데이트.
               handleAddRoomJlist(message);
               break;

           case "CreateRoomFail":// 방 생성 실패 처리.
               showErrorMessage("Create Room Fail","알림");// ** Improved error message for clarity.
               break;


           case "JoinRoom":
               handleJoinRoom(message);
               break;


           case "ClientJlistUpdate":// 클라이언트 목록 업데이트 처리.
                xxxupdateClientJlist();// ** Update client list display.

            case "RoomJlistUpdate":// 방 목록 업데이트 처리.
                System.out.println("Updating Room List");
                xxxupdateRoomJlist();// ** Update room list display.

            case "ClientExit":
                removeClientFromJlist(message);// 클라이언트 퇴장 처리.
                break;

            case "ServerShutdown":
                handleServerShutdown();// 서버 종료 처리.
                break;

            case "RoomOut":
                handleRoomOut(message);// 방 퇴장 처리.
                break;


            default:
                break;// 처리되지 않은 프로토콜에 대한 기본 처리.
       }
   }
   private void activateGameButtons() {
    sendBtn.setEnabled(true); // ** 전송 버튼 활성화
    msgTf.setEditable(true); // ** 메시지 입력 필드 활성화
}

   private void showMessageBox(String msg,String title) {  
       JOptionPane.showMessageDialog(null,msg,title,JOptionPane.CLOSED_OPTION);// 메시지 박스 표시.  
   }

   private void addClientToList(String clientID) {  
       clientVC.add(clientID);// 클라이언트 목록에 추가.  
//      clientJlist.setListData(clientVC);// JLIST로 화면에 출력.  
   }

   private void xxxupdateClientJlist() {  
       clientJlist.setListData(clientVC);// 클라이언트 목록 업데이트.  
   }

   private void handleCreateRoom(String roomName) {  
       myRoomID=roomName;// 현재 방 이름 설정.   
       joinRoomBtn.setEnabled(false);// 참여 버튼 비활성화.   
       createRoomBtn.setEnabled(false);// 방 생성 버튼 비활성화.         
       setTitle("사용자: "+clientID+" 채팅방: "+myRoomID);// 타이틀 업데이트.      
   }

   private void handleAddRoomJlist(String roomName) {  
       if(myRoomID.equals("")){   
           joinRoomBtn.setEnabled(true);// 방이 없을 때 참여 버튼 활성화.   
     }   
       roomClientVC.add(roomName);// 방 이름 추가.   
       roomJlist.setListData(roomClientVC);// 방 목록 업데이트.   
   }

   private void xxxupdateRoomJlist() {  
       roomJlist.setListData(roomClientVC);// 방 목록 업데이트.   
   }

   private void handleJoinRoom(String roomName) {  
       myRoomID=roomName;// 현재 방 이름 설정.   
       joinRoomBtn.setEnabled(false);// 참여 버튼 비활성화.   
       createRoomBtn.setEnabled(false);// 방 생성 버튼 비활성화.         
       setTitle("사용자: "+clientID+" 채팅방: "+myRoomID);// 타이틀 업데이트.   
          
       showInfoMessage("Join Room success","알림");// 성공 메시지 표시.   
   }

   private void removeClientFromJlist(String clientID) {  
        clientVC.remove(clientID);// 클라이언트 목록에서 제거.  
   }

   private void handleServerShutdown() {  
        try {  
            socket.close();// 소켓 닫기.  
            clientVC.removeAllElements();// 클라이언트 목록 초기화.  
            if(!myRoomID.isEmpty()) {  
                roomClientVC.removeAllElements();// 방 목록 초기화.  
          }  
      } catch(IOException e) {  
          e.printStackTrace();  
      }  
        System.exit(0);// 애플리케이션 종료.  
   }

   private void handleRoomOut(String roomName) {  
        roomClientVC.remove(roomName);// 지정된 방 제거.  
        if(roomClientVC.isEmpty()) {   
            joinRoomBtn.setEnabled(false);// 방이 없을 때 참여 버튼 비활성화.   
      }      
   }

   private void showErrorMessage(String message,String title) {   
        JOptionPane.showMessageDialog(null,message,title,JOptionPane.ERROR_MESSAGE);// 오류 메시지 표시.   
   }

   private void showInfoMessage(String message,String title) {    
        JOptionPane.showMessageDialog(null,message,title,JOptionPane.INFORMATION_MESSAGE);// 정보 메시지 표시.   
   }

   @Override    
   public void actionPerformed(ActionEvent e) {    
        if(e.getSource()==loginBtn){    
            System.out.println("login button clicked");    
            connectToServer();    
      } else if(e.getSource()==noteBtn){    
            System.out.println("note button clicked");    
            handleNoteSendButtonClick();    
      } else if(e.getSource()==createRoomBtn){    
            handleCreateRoomButtonClick();    
      } else if(e.getSource()==joinRoomBtn){    
            handleJoinRoomButtonClick(); 
            ////////게임로직              
      } else if (e.getSource() == startGameBtn) { // ** 게임 시작 버튼 클릭 처리
            handleGameStartButtonClick();
} else if (e.getSource() == sendBtn) { // ** 채팅 메시지 전송 버튼 클릭 처리
            handleSendButtonClick();
}
   }
   private void handleGameStartButtonClick() {
    System.out.println("게임 시작 버튼 클릭됨");

    String answer = JOptionPane.showInputDialog("정답을 입력하세요:"); // ** 출제자로부터 정답 입력 받기
    if (answer != null && !answer.trim().isEmpty()) {
        sendMsg("GameStart/" + myRoomID + "/" + answer.trim()); // ** 서버로 게임 시작 및 정답 전송
    } else {
        System.out.println("정답이 입력되지 않았습니다.");
    }
}

   private void handleSendButtonClick() { // ** 메시지 전송 버튼 처리
     if (!myRoomID.isEmpty()) {
            String msg = msgTf.getText().trim();
            if (!msg.isEmpty()) {
                sendMsg("SendMsg/" + myRoomID + "/" + msg);
                msgTf.setText("");
            }
       }
}
   
    public void handleNoteSendButtonClick() {   
        System.out.println("note button clicked");   
        String dstClient=(String)clientJlist.getSelectedValue();   

        String note=JOptionPane.showInputDialog("보낼 메시지");   
        if(note!=null){   
            sendMsg("Note/"+dstClient+"/"+note);   
            System.out.println("receiver: "+dstClient+" | 전송 노트: "+note);   
      }   
   }

   private void handleCreateRoomButtonClick() {
       System.out.println("createRoomBtn clicked");

       String roomName = JOptionPane.showInputDialog("Enter Room Name:"); // ** Prompt user for room name.
       if (roomName == null || roomName.trim().isEmpty()) {
           System.out.println("Room creation cancelled or no name entered"); // ** Log cancellation.
           return; // ** Exit if no valid name is provided.
       }
       sendMsg("CreateRoom/" + roomName.trim()); // ** Send room creation request to server.
   }

   private void handleJoinRoomButtonClick() { // ** Method to handle joining a room.
       System.out.println("joinRoomBtn clicked");
       String roomName = (String) roomJlist.getSelectedValue(); // ** Get selected room name.
       if (roomName != null) {
           sendMsg("JoinRoom/" + roomName); // ** Send join request to server.
       }
   }

   private void handleClientExitButtonClick() { // ** Handle client exit action.
       if (!myRoomID.isEmpty()) { // ** Check if user is in a room before exiting.
           sendMsg("ExitRoom/" + myRoomID); // ** Notify server of exit from room.
       }

       sendMsg("ClientExit/Bye"); // ** Notify server of client exit.

       clientVC.removeAllElements(); // ** Clear client list.

       if (!myRoomID.isEmpty()) { 
           roomClientVC.removeAllElements(); // ** Clear room list if in a room.
           myRoomID = ""; // ** Reset current room info.
       }

       closeSocket(); // ** Close socket connection properly.
       System.exit(0); // ** Exit application.
   }

   private void closeSocket() { // ** Method to close socket and streams safely.
       try {
           if (dos != null) {
               dos.close(); // ** Close output stream.
           }
           if (dis != null) {
               dis.close(); // ** Close input stream.
           }
           if (socket != null) {
               socket.close(); // ** Close socket connection.
           }
       } catch (IOException e) {
           e.printStackTrace(); // ** Log any exceptions during closing process.
       }
   }

   private void handleExitRoomButtonClick() { 
       System.out.println("exitRoomBtn clicked"); 

       sendMsg("ExitRoom/" + myRoomID); // ** Notify server of exit from current room.

       myRoomID = ""; // ** Reset current room info.
 
       joinRoomBtn.setEnabled(roomClientVC.size() > 0); 
       createRoomBtn.setEnabled(true); 
       setTitle("사용자: " + clientID); // ** Update title to reflect client ID only.
   }

   public void keyPressed(KeyEvent e) { 
   }

   
          

   public void keyTyped(KeyEvent e) { 
   }

   public static void main(String[] args) { 
       new Client2024(); // ** Start the client application. 
   }

@Override
public void keyReleased(KeyEvent e) {
   // TODO Auto-generated method stub
   
}
}