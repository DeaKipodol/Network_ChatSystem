import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class mainGUI extends JFrame {
   protected  JPanel contentPane;
   protected JButton noteBtn;
   protected JTextArea chatArea;
   protected JButton joinRoomBtn;
   protected JButton createRoomBtn;
   protected JList<String> roomJlist;
   protected JList<String> clientJlist;
   protected JTextField msg_tf;
   protected JButton sendBtn;

   public mainGUI() {
      initMainGUI();
  }

	public void initMainGUI() {
      setBounds(400, 100, 500, 445);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);

      JLabel lblNewLabel = new JLabel("현재 접속자");
      lblNewLabel.setBounds(12, 10, 87, 15);
      contentPane.add(lblNewLabel);

      roomJlist = new JList();
      roomJlist.setBounds(12, 222, 118, 112);
      contentPane.add(roomJlist);

      noteBtn = new JButton("쪽지 전송");
      noteBtn.setBounds(12, 156, 118, 23);
      contentPane.add(noteBtn);

      JLabel lblNewLabel_1 = new JLabel("채팅방목록");
      lblNewLabel_1.setBounds(12, 197, 87, 15);
      contentPane.add(lblNewLabel_1);

      clientJlist = new JList();
      clientJlist.setBounds(12, 35, 118, 112);
      contentPane.add(clientJlist);

      createRoomBtn = new JButton("방만들");
      createRoomBtn.setBounds(12, 375, 118, 23);
      contentPane.add(createRoomBtn);

      joinRoomBtn = new JButton("채팅방참여");
      joinRoomBtn.setBounds(12, 344, 118, 23);
      contentPane.add(joinRoomBtn);

      chatArea = new JTextArea();
      chatArea.setBounds(138, 5, 336, 359);
      contentPane.add(chatArea);

      msg_tf = new JTextField();
      msg_tf.setBounds(138, 376, 267, 21);
      contentPane.add(msg_tf);
      msg_tf.setColumns(10);

      sendBtn = new JButton("전송");
      sendBtn.setBounds(410, 375, 64, 23);
      contentPane.add(sendBtn);
      this.setVisible(true);
   }
}
  