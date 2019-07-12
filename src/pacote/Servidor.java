package pacote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor extends Thread{

	private Socket cliente;         			// Vai aceitar a conexao do cliente e colocar neste socket
	private ServerSocket servidor;				// Socket do servidor

	private BufferedReader receber;				// Representa o recebimento d msgs do cliente conectado
	private PrintStream enviar;					// Representa o envio d msgs para o cliente conectado

	private int porta;							// Guarda a porta em que o servidor vai ser iniciado

	private boolean conectado;              	// varivel que vai informar se o servidor ta conectado
	
	

	//Configura construtor para iniciar um servidor
	Servidor(int porta){
		this.cliente = null;
		this.servidor = null;

		this.receber = null;
		this.enviar = null;

		this.porta = porta;
		this.conectado = true;

		try {
			//Sobe o servidor na porta local passada
			this.servidor = new ServerSocket(this.porta);
			System.out.println("Servidor no ar na porta: " + this.porta);
		}
		catch (IOException e) {
			System.out.println("Porta ocupada!");
			e.printStackTrace();
		}
		
		//Inicia a thread do servidor
		//estabelecerConexao();
		start();
	}
	
	//################################################### Rum implementado #####################################################
	public void run() {		
		//Aguarda um cliente se conectar
		//estabelecerConexao();
    }
	
	//Aceita uma solicitacao de conexo de um cliente com o servidor
    public void estabelecerConexao(){
        Socket cliente = null;
       
            try {
            	System.out.println("Aguardando pedido de conexao...");
                cliente = this.servidor.accept();
                setCliente(cliente);               
                System.out.println("Conexao estabelecida com sucesso em: " + getCliente().getInetAddress().getHostAddress());
                
                configReceberMsg();
                configEnviarMsg();
            }
            catch (IOException e) {
                e.printStackTrace();
            }           
    }
	
    //Configura recebimento de msg vindas do cliente local
    public void configReceberMsg() throws IOException{
        BufferedReader receber = null;
        
        try {
        	receber = new BufferedReader(new InputStreamReader(getCliente().getInputStream()));
        	setReceber(receber);
        	System.out.println("Recebimento de msg do cliente configurado!");
        	
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }
    
  //Configura envio de msg para o cliente local
    public void configEnviarMsg() throws IOException{
        PrintStream enviar = null;
       
        enviar = new PrintStream(getCliente().getOutputStream(),true);
        setEnviar(enviar);
        System.out.println("Envio de msgs para o cliente configurado!");
    }
	
  //Recebe as mensagens do cliente
    public String receberMsg() {
   
    	String msg = "";
    	try {
    		msg = getReceber().readLine();
		}
    	catch (IOException e) {
			e.printStackTrace();
		}
		
		return msg;
    }
	
    //Envia as mensagens para o cliente local
    public void enviarMsg(String msg) throws IOException  {       
        getEnviar().println(msg);
        getEnviar().flush();
    }
	
	
	//################################################### Setters and Getters ###################################################
	public Socket getCliente() {
		return cliente;
	}

	public void setCliente(Socket cliente) {
		this.cliente = cliente;
	}

	public ServerSocket getServidor() {
		return servidor;
	}

	public void setServidor(ServerSocket servidor) {
		this.servidor = servidor;
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