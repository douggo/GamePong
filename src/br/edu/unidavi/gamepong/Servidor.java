package br.edu.unidavi.gamepong;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Douglas
 */
public class Servidor extends Thread {
    private static ArrayList<BufferedWriter> clientes;
    private static ServerSocket servidor;
    private static Socket cliente;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private InputStream inputStream;
    private String nome;
    
    /**
    * Construtor da classe Servidor.
    * Inicialização das classes de InputStream para realizar a comunicação
    *     entre Servidor e Cliente.
    */
    public Servidor(Socket cliente) {
        this.cliente = cliente;
        try {
            inputStream = this.cliente.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    /**
    * Método responsável para rodar a thread depois de executar o método main.
    * Inicialização das classes de OutputStream para realizar a comunicação
    *     entre o Servidor e Cliente.
    */
    @Override
    public void run(){
        try {
            String mensagem;
            OutputStream outputStream = this.cliente.getOutputStream();
            Writer outputStreamWriter = new OutputStreamWriter (outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            
            this.clientes.add(bufferedWriter);
            nome = mensagem = bufferedReader.readLine();
            
            while(!"Sair".equalsIgnoreCase(mensagem) && mensagem != null) {
                mensagem = bufferedReader.readLine();
                System.out.println(mensagem);
                enviaParaTodos(bufferedWriter, mensagem);
            }
        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    /**
    * Método responsável pelo envio das mensagens para o resto dos clientes.
    * Para cada cliente registrado no ArrayList é enviado a mensagem.
    */
    public void enviaParaTodos(BufferedWriter bufferedWriterSaida, String mensagem) throws IOException {
        BufferedWriter bufferedWriterLocal;
        
        for (BufferedWriter bw : clientes) {
            bufferedWriterLocal = (BufferedWriter)bw;
            if (!(bufferedWriterSaida == bufferedWriterLocal)) {
                bw.write(mensagem + "\r\n");
                bw.flush();
            }
        }
    }
    
    /**
    * Método main.
    * Inicializa a janela de configuração da porta e as configurações iniciais
    *     para clientes acessarem o servidor. Após isso ele executa a Thread
    *     para escutar a mensagem de um cliente e enviar para os demais
    */
    public static void main(String[] args) {
        try {
            JLabel lblMessage = new JLabel("Porta do Servidor:");
            JTextField txtPorta = new JTextField("6789");
            Object[] texts = {lblMessage, txtPorta};  
            JOptionPane.showMessageDialog(null, texts);
            
            servidor = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clientes = new ArrayList<BufferedWriter>();
            
            JOptionPane.showMessageDialog(null,"Servidor está ativo na porta: " + txtPorta.getText());
            
            while (true) {
                System.out.println("Aguardando a conexão...");
                cliente = servidor.accept();
                if (cliente.isConnected()) {
                    System.out.println("Cliente conectado com sucesso...");
                    Thread threadServidor = new Servidor(cliente);
                    threadServidor.start();
                } else {
                    System.out.println("Não foi possível realizar a conexão com o cliente...");
                }
            }
        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }
}