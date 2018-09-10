/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package severfx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Deageon
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea outputServer;

    @FXML
    private TextField inputServer;

    @FXML
    private Label label;

    ServerSocket serverSocket;
    Socket socket;

    DataInputStream in;
    DataOutputStream out;

//    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        System.out.println("You clicked me!");
        label.setText("접속대기");

        try {
            serverSocket = new ServerSocket(7777);

            socket = serverSocket.accept();//이 블록상태를 어떻게 극복할까?

            InMessage inmsgthread = new InMessage();
            inmsgthread.setSocket(socket);

//            SendMessage outmsgthread = new SendMessage();
//            outmsgthread.setSocket(socket);

            out = new DataOutputStream(socket.getOutputStream());
            inmsgthread.start();
//            outmsgthread.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onTypeText(ActionEvent e) {

        try {
            String msg = "서버님" + inputServer.getText() + "\n";
            outputServer.appendText(msg);
            System.out.println(msg);
            out.writeUTF(msg);
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        inputServer.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

//    private class SendMessage extends Thread {
//
//        private Socket socket;
//
//        @Override
//        public void run() {
//            try {
//                String sendString = inputServer.getText() + "\n";
//                out = new DataOutputStream(socket.getOutputStream());
//
//                while (true) {
//                    out.writeUTF(sendString);
//                    out.flush();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    socket.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//
//        public void setSocket(Socket _socket) {
//            this.socket = _socket;
//        }
//
//    }

    private class InMessage extends Thread {

        private Socket socket;

        public void setSocket(Socket isocket) {
            this.socket = isocket;
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                String receiveString;

                while (true) {
                    receiveString = in.readUTF();

                    if (receiveString == null) {
                        System.out.println("연결 종료");
                        break;
                    } else {
                        outputServer.appendText(receiveString);
                        System.out.println("클라이언트" + receiveString);
                    }
                }
                in.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
