import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameMain2 extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JButton btnSend; // 전송 버튼
    private JButton btnStartGame; // 게임시작 버튼
    private boolean isGameStarted = false; // 게임 시작 상태 확인 변수

    public GameMain2(String roomName) {
        setTitle("채팅방: " + roomName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        textField = new JTextField();
        textField.setBounds(142, 330, 300, 21);
        contentPane.add(textField);
        textField.setColumns(10);

        btnSend = new JButton("전송");
        btnSend.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnSend.setBounds(446, 329, 97, 23);
        btnSend.setEnabled(false); // 초기 상태: 비활성화
        contentPane.add(btnSend);

        JTextPane textPane_1 = new JTextPane();
        textPane_1.setBackground(new Color(255, 255, 255));
        textPane_1.setBounds(142, 10, 300, 310);
        contentPane.add(textPane_1);

        btnStartGame = new JButton("게임시작");
        btnStartGame.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnStartGame.setBounds(465, 60, 97, 23);
        btnStartGame.setEnabled(true); // 초기 상태: 활성화
        contentPane.add(btnStartGame);

        JLabel 접속자 = new JLabel("접속자");
        접속자.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        접속자.setBounds(12, 10, 73, 15);
        contentPane.add(접속자);

        JList<String> clientJlist = new JList<>();
        clientJlist.setBounds(12, 35, 108, 285);
        contentPane.add(clientJlist);

        // "나가기" 버튼 추가
        JButton exitButton = new JButton("나가기");
        exitButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        exitButton.setBounds(12, 330, 100, 23);
        contentPane.add(exitButton);

        // "게임시작" 버튼 동작 정의
        btnStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGameStarted = true; // 게임 시작 상태 변경
                btnSend.setEnabled(true); // 전송 버튼 활성화
                btnStartGame.setEnabled(false); // 게임 시작 버튼 비활성화
                JOptionPane.showMessageDialog(null, "게임이 시작되었습니다!", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // "나가기" 버튼 동작 정의
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 현재 창 닫기
                dispose();

                // Client2024 메인 화면으로 돌아가기
                EventQueue.invokeLater(() -> {
                    Client2024 clientUI = new Client2024();
                    clientUI.setVisible(true);
                });
            }
        });
    }
}
