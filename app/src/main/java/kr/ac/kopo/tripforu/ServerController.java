package kr.ac.kopo.tripforu;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ServerController {
    String adress = "192.168.34.35";
    short port = 6060;
    byte connectionTry = 0;//5회까지 5초 간격으로 재 접속 시도
    
    /**
     * -> 작성자 : 이제경
     * -> 함수 : 서버에 연결하여
     * - 클릭시 해당 여행 일정의 상세 내용을 표시
     */
    public void Connect(){
        new Thread(() -> {
            try {
                connectionTry++;
                Socket sk = new Socket(adress , port) ;
                OutputStream ous = sk.getOutputStream();
                DataOutputStream dous = new DataOutputStream(ous);
            
                String cmd = "<LOGINCHECK/><ID>admin</ID><PW>1234</PW>";
                byte[] buff = cmd.getBytes();
                dous.write(buff);
                
                Thread connectionThread = new Thread(() -> ServerConnectionThread(sk));
                connectionThread.start();
            }
            catch (IOException e) {
                e.printStackTrace();
                if(connectionTry < 6)
                    Connect();
            }
        }).start();
    }
    
    
    private void ServerConnectionThread(Socket sk){
        try {
            while(sk.getKeepAlive()){
                InputStream ins = sk.getInputStream();
                DataInputStream dins = new DataInputStream(ins);
            
                byte[] buf = new byte[1024];
                dins.read(buf);
                String response = new String(buf).trim();
            
                Log.d("ServerConnection", "heartbeat: " + response);
            }//서버가 종료되었을 경우, 인터넷 접속이 끊긴 경우
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
