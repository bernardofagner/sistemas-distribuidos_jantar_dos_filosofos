package pacote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;


public class Cliente extends Thread{

	private Socket cliente;                 // Vai conectar este cliente a um servidor	

	private BufferedReader receber;         // Representa o recebimento d msgs do cliente
	private PrintStream enviar;             // Representa o envio de msg para o cliente

	private String ip = "";          		// Representa o IP do servidor da maquina que se quer conectar

	private String msgRecebida = "";          //Guarda as msgs recebidas do servidor

	private int porta;                      // Guarda a porta do servidor em que o cliente vai se conectar
	private boolean conectado;

	//Configura o cliente para estabelecer conexao com o servidor
	Cliente(String ip, int porta){

		this.cliente = null;

		this.receber = null;
		this.enviar = null;

		this.ip = ip;
		this.porta = porta;
		
		this.msgRecebida = "";

		this.conectado = false;
		
		//estabelecerConexaoExterna();
		//inicia a thread do cliente
		start();
	}
	
	//##################################################### Rum implementado #####################################################
    public void run() {
    	//Cria conexao deste cliente com o servidor de outro filosofo
		//estabelecerConexaoExterna();
    } 
	
	//Estabelce uma conexao do cliente com o servidor da outra maquina
	public void estabelecerConexaoExterna() throws InterruptedException {
        try {
            //tenta conectar ao servidor externo
 
            Scanner ler = new Scanner(System.in);
            System.out.println("Digite um caracter para iniciar a conexao com o servidor externo...");
            ler.nextLine();
            System.out.println("Conectando...");
            
            this.cliente = new Socket(this.ip, this.porta);
            setConectado(true);
            System.out.println("Conectado!");            
            
            //Configura o envio e recebimento de mensagens entre cliente e servidor
            configReceberMsg();
            configEnviarMsg();
            
            sleep(1000);
 
            ler.close();
 
        }catch(IOException e) {
            System.out.println(e);
        }
    }
	
	//Configura recebimento de msg vindas do servidor local
    public void configReceberMsg() throws IOException{
        BufferedReader receber = null;
 
        try {
 
            receber = new BufferedReader(new InputStreamReader(this.cliente.getInputStream()));
            setReceber(receber); 
            System.out.println("Configurado recebimento de msg do servidor!");
            
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
  //Configura envio de msg para o servidor local
    public void configEnviarMsg() throws IOException{
        PrintStream enviar = null;
 
        try { 
            enviar = new PrintStream(getCliente().getOutputStream(),true);
            setEnviar(enviar);
 
            System.out.println("Envio de msgs para o servidor configurado!");
 
        }catch( IOException e) {
 
            System.out.println(e);
        }
    }
    
  //Recebe as mensagens do servidor
    public String receberMsg() {
    	
    	String msg = "";
    	
		try {
			msg = getReceber().readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg;
    }
    
    //Envia  as mensagens para o servidor local
    public void enviarMsg(String msg) throws IOException  {
 
        getEnviar().println(msg);
        getEnviar().flush();
    }   
    
    //################################################### Setters and Getters ###################################################
    public String getMsgRecebida() {
		return msgRecebida;
	}

	public void setMsgRecebida(String msgRecebida) {
		this.msgRecebida = msgRecebida;
	}
	
	public Socket getCliente() {
		return cliente;
	}

	public void setCliente(Socket cliente) {
		this.cliente = cliente;
	}

	public BufferedReader getReceber() {
		return receber;
	}

	public void setReceber(BufferedReader receber) {
		this.receber = receber;
	}

	public PrintStream getEnviar() {
		return enviar;
	}

	public void setEnviar(PrintStream enviar) {
		this.enviar = enviar;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public boolean isConectado() {
		return conectado;
	}

	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}
	
}
