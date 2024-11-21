

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JTextArea;


public class ClientInfo extends Thread{
    protected  DataInputStream dis;
    protected DataOutputStream dos;

    protected Socket clientSocket;

    protected String clientID="";
    protected String roomID="";
    protected JTextArea textArea;
   

    public ClientInfo(Socket soc) {
        this.clientSocket=soc;
        initNewClient();
    }
    private  void initNewClient(){
        ///initStream 이 필요없어지나?
        try {
            dis=new DataInputStream(clientSocket.getInputStream());
            dos=new DataOutputStream(clientSocket.getOutputStream());
            clientID=dis.readUTF();
            //이제 클라이언트 아이디가 맨앞에오게 함. 즉 보내기전 인포에서 묶어서 보냄.
            textArea.append("new Client: "+clientID+"\n");
            dos.writeUTF("환영합니다!");
         } catch (IOException e) {
            System.out.println("서버 오류: " + e.getMessage());
         }
        
    }
    
    public void run(){
        while(true){
            try {
                String msg =dis.readUTF();
                textArea.append(clientID+": "+msg+"\n");
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
