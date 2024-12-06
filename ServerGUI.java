import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ServerGUI extends JFrame {
	protected JButton stopBtn;
	protected JButton startBtn;
	protected JTextField port_tf;
	protected JScrollPane scrollPane;
	protected JTextArea textArea;

    public ServerGUI() {
        initGUI();
    }

    private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 330, 350);
  
		setLayout(null);
  
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 296, 195);
		add(scrollPane);
  
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
  
		JLabel lblNewLabel = new JLabel("포트 번호");
		lblNewLabel.setBounds(12, 232, 64, 15);
		add(lblNewLabel);
  
		port_tf = new JTextField();
		port_tf.setBounds(100, 229, 209, 21);
		add(port_tf);
		port_tf.setColumns(10);
  
		startBtn = new JButton("서버 시작");
		startBtn.setBounds(12, 260, 130, 23);
		add(startBtn);
  
		stopBtn = new JButton("서버 중지");
		stopBtn.setBounds(178, 260, 130, 23);
		add(stopBtn);
		setVisible(true);
    }
}
