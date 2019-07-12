package pacote;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Filosofo extends Thread{

	private int qtdComidas;				// Para manter o equilibro entre os filosofos
	private String nome;				// Recebe o nome do filosofo para sabermos de qual maquina ele vem	

	private int ID;						// Recebe o id da maquina do filosofo vai de 0 a 4
	private String ip;					// Guarda o IP do outro filosofo (maquina)
	private int porta;					// Guarda a porta em que este filosofo (maquina) estara aguardando conexoes
	private int portaRemota;			// Guarda a porta da outra maquina a qual este cliente (filosofo) ira se conectar
	private int maximo;					// Representa a quantidade maxima que se pode comer
	private int diferenca;				// Representa o quanto um filosofo pode ter comido a mais que o outro

	private String justica[];			// Representa um Token que contem as estatisticas para todos os filosofos

	private boolean ativoEnviar;		// Define se a thread de enviar msgs esta ativa
	private boolean ativoReceber;		// Define se a thread de receber msgs esta ativa

	private Cliente pedinte;			// Instancia de cliente que guardara um cliente instanciado
	private Servidor ouvinte;			// Instancia de servidor que gardara um servidor instanciado
	private Garfo garfo;				// Instancia que representara o garfo odo filosofo
	
	public static void main(String[] args) throws InterruptedException {
		
		//Inicializa um filosofo (Nome, Id, diferenca entre os filosofos, maximo de recurso a comer)
		new Filosofo("Eneas 56", 0, 5, 40);
	}
	
	//Construtor da classe filosofo
	public Filosofo(String nome, int id, int diferenca, int maximo) throws InterruptedException {

		this.nome = nome;
		this.ID = id;
		this.ativoEnviar = true;
		this.ativoReceber = true;
		this.maximo = maximo;
		this.diferenca = diferenca;
		
		this.porta = 0;
		this.portaRemota = 0;
		this.ip = "";
		this.qtdComidas = 0;
		
		this.justica = new String [5];
		
		//Inicializa a estrutura de dados
		this.justica[0] = "0" + ";" + "0";
		this.justica[1] = "0" + ";" + "0";
		this.justica[2] = "0" + ";" + "0";
		this.justica[3] = "0" + ";" + "0";
		this.justica[4] = "0" + ";" + "0";
		this.justica[getIdFilosofo()] = getIdFilosofo() + ";" + "0";	
		
		//Le o arquivo .txt e define os parametros de conexao para os sockets
		configuraConexao();
		
		//Inicializa um objeto garfo
		this.garfo = new Garfo(1, false);

		//Inicia a thread deste filosofo
		start();
		
		//inicia o servidor deste filosofo e aguarda para conecta um filosofo a este filosofo
		this.ouvinte = new Servidor(getPorta());
		getOuvinte().estabelecerConexao();
	}
	
	//Toda a logica de gerenciar o jantar aqui.
	public void run(){
		
		//Conecta este filosofo a outro filosofo (outra maquina) no IP desse outro e na porta do server deste outro
		this.pedinte = new Cliente(getIp(), getPortaRemota());		
		try {
			getPedinte().estabelecerConexaoExterna();
		}
		catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		
		//Thread p RECEBER mensagens vindas da outra maquina
		new Thread (){
			
			public void run(){
				
				//variavel que recebe as respostas vindas do outro filosofo
				String msg = "";
				
				try {
					//Simplesmente espera um pouco
					sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				//Enquanto o filosofo estiver ativo...
				while(isAtivoReceber()) {

					try {
						sleep(200);
						
						msg = getOuvinte().receberMsg();

						if(!msg.equals("")) {

							// System.out.println("Recebeu a msg: " + msg);

							if(msg.equals("FIM")) {
									
								//Exibe os dados de todos os filosofos
								for(int i = 0 ; i < 5 ; i++) {
									System.out.println(getJustica(i));
								}
								
								//Informa que o jantar acabou
								getPedinte().enviarMsg("FIM");
								
								// Para de enviar msgs para o filosofo da direita
								setAtivoEnviar(false);
								
								// Para de receber msgs do filosofo da esquerda 
								setAtivoReceber(false);								
							}
							
							//Verifica se alguem pediu o garfo desse filosofo
							if(msg.equals("quero seu garfo")) {
								
								//Verifica se o garfo desse filosofo esta livre
								if(!getGarfo().getIsUsed()){
									System.out.println("Bloqueando o garfo p vc usar");
									
									//Bloqueia o garfo simbolizando que quem pediu pode usar
									getGarfo().setUsed(true);
									
									//Libera o garfo para o outro filosofo e responde ele
									getOuvinte().enviarMsg("pode usar");											
								}
								else {
									getOuvinte().enviarMsg("nao pode usar");
								}														
							}
							
							//Filosofo da outra maquina informa que terminou de usar o garfo deste filosofo
							if(msg.equals("terminei de comer")) {
								
								//Libera o garfo do filosofo dessa maquina
								getGarfo().setUsed(false);
							}
							
							//Filosofo da outra maquina informa que o garfo dele nao esta mais disponivel e desiste de comer
							if(msg.equals("nao da para comer agora")) {
								
								//Libera o garfo do filosofo dessa maquina
								getGarfo().setUsed(false);
							}
							
							//Atualiza as informacoes dos filosofos que chegaram, comparando os valores inteiros dos caracteres numericos da msg
							if(((int)msg.charAt(0)) >= 48 && ((int)msg.charAt(0)) <= 57){
								
								//atualiza o vetor de justica com os dados que chegaram
								int idFilosofo = 0;
								String qtdeComida = "";
								
								String temp [] = new String[2];
								temp = msg.split(";");
								
								idFilosofo = Integer.parseInt(temp[0]);
								qtdeComida = temp[1];
								
								//Carrega os dados atuais para o filosofo em questao
								temp = getJustica(idFilosofo).split(";");
								
								// Verifica se os dados que chegaram sao atuais
								if(((int)Integer.parseInt(qtdeComida)) > ((int)Integer.parseInt(temp[1]))) {
									
									qtdeComida = idFilosofo + ";" + qtdeComida;
									
									//Atualiza os dados para o filosofo dono dos dados que chegaram
									setJustica(idFilosofo, qtdeComida);
									System.out.println("Dados de comida atualizados no filosofo: " + idFilosofo);
									
									//Envia a informacao recebida p a maquina seguinte
									getPedinte().enviarMsg(getJustica(idFilosofo));
									
								}
							}
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					
					msg = "";
				}
				
				
				//Apos sair do jantar, aguarda um pouco
				try {
					sleep(3000);
					
					//Fecha o socket do servidor
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		//Thread p ENVIAR mensagens para a outra maquina
		new Thread (){
			
			public void run(){
				
				String msg = "";

				//Inicia com o filosofo pensando
				try {
					sleep(1000);
					pensando();
					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				//Enquanto o filosofo estiver ativo...
				while(isAtivoEnviar()) {
					
					//System.out.println("while");

					try {
							
						//Verifica se o filosofo pode pedir
						if(justicaFilosofo()){
							
							//Verifica se o garfo deste filosofo esta livre)
							if(!(getGarfo().getIsUsed())) {
								
								System.out.println(getNome() + " esta com fome");

								//Pede o garfo do outro filosofo
								pedindo();
								
								//Pede garfo ao filosofo do lado
								getPedinte().enviarMsg("quero seu garfo");
								
								//Recebe a resposta do pedido
								msg = getPedinte().receberMsg();

								if(msg.equals("pode usar")) {
									
									if(!getGarfo().getIsUsed()) {
										
										//Bloqueia o proprio garfo
										getGarfo().setUsed(true);
										
										//Come
										comendo();
										
										//Informa que terminou de usar o garfo do outro filosofo
										getPedinte().enviarMsg("terminei de comer");
										
										//Cria variaveis temporarias
										int novaQtde;
										String qtdeComida = "";
										String temp [] = new String[2];
										
										//carrega os dados do filosofo desta maquina
										temp = getJustica(getIdFilosofo()).split(";");
										
										novaQtde = Integer.parseInt(temp[1]);
										novaQtde += 1;
										
										qtdeComida = temp[0] + ";" + novaQtde;										

										//Atualiza os dados deste filosofo
										setJustica(Integer.parseInt(temp[0]), qtdeComida);
										
										//Envia as informacoes de todos os filosofos p frente
										System.out.println("Enviando estatisticas de " + getNome());
										
										for(int i = 0 ; i < 5 ; i++) {
											sleep(50);
											System.out.println(getJustica(i));
											getPedinte().enviarMsg(getJustica(i));
										}
										
										//Libera o proprio garfo
										getGarfo().setUsed(false);
										
										//Verifica se chegou ao limite de comidas
										if(novaQtde >= getMaximo()) {
											
											//Exibe os dados de todos os filosofos guardados nesta maquina
											System.out.println("##################### Estatisticas finais #####################");
											for(int i = 0 ; i < 5 ; i++) {												
												System.out.println(getJustica(i));
											}
											System.out.println("################################################################");
											
											setAtivoEnviar(false);
											
											//Informa que o jantar acabou
											getPedinte().enviarMsg("FIM");	
											
										}
										
										// Verifica se o jantar acabou
										if(isAtivoEnviar()) {
											//Inicia a festa pensando
											pensando();
										}										
									}
									else {
										// Desiste de comer
										getPedinte().enviarMsg("nao da para comer agora");
									}
								}
								else {
									
									if(msg.equals("nao pode usar")) {
										
										//Desbloqueia o proprio garfo
										getGarfo().setUsed(false);
									}									
									
									//Inicia a festa pensando ou pensa duante a festa
									pensando();
								}
							}
						}
						else {
							
							//Se nao pode pedir garfo, continua a pensar
							pensando();
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					
					msg = "";
				}
				
				//Apos sair do jantar, aguarda um pouco
				try {
					sleep(3000);
					
					//Fecha o socket do cliente
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	// Configura a conexao cliente servidor
	public void configuraConexao(){
		
		//le um arquivo de configuracoes
		String path = "config.txt";
		String line = "";
		String porta[];
		String ip[];
		BufferedReader buffRead;
		
		try {
			buffRead = new BufferedReader(new FileReader(path));
			
			//Le a primeira linha do arquivo
			line = buffRead.readLine();
			porta = line.split(":");
			System.out.println(porta[0] + ": " + porta[1]);
			setPorta(Integer.parseInt(porta[1]));			
	
			//Le a segunda linha do arquivo
			line = "";
			line = buffRead.readLine();
			porta = null;
			porta = line.split(":");
			System.out.println(porta[0] + ": " + porta[1]);
			setPortaRemota(Integer.parseInt(porta[1]));			
			
			//Le a terceira linha do arquivo
			line = "";
			line = buffRead.readLine();
			ip = line.split(":");
			System.out.println(ip[0] + ": " + ip[1]);			
			setIp(ip[1]);
			System.out.println("");
			
			buffRead.close();
	
		} catch (FileNotFoundException e) {
	
			e.printStackTrace();
		} catch (IOException e) {
	
			e.printStackTrace();
		}
	}
	
	// Pensa por um tempo
	public void pensando() throws InterruptedException {
		
		System.out.println(getNome() + " esta pensando...");
				
		//Pensa por um tempo
		int time;
		
		try {			
			Random r = new Random();
			time = r.nextInt(4700) + 300;
			
			//fica comendo por x segundos		
			sleep(time);
		}catch(InterruptedException e){		
			System.out.println(e);
		}
	}
	
	// Pede um garfo
	public void pedindo() throws InterruptedException {
		
		sleep(100);
		
		System.out.println(getNome() + " esta pedindo um garfo...");
	}

	//Funcao que representa o filosofo comendo
	public void comendo() throws IOException, InterruptedException {
		
		try {
			//fica comendo por um tempo fixo
			System.out.println(getNome() + " esta comendo...");
			sleep(500);
		}catch(InterruptedException e){		
			System.out.println(e);
		}
	}
	
	//Implementa a justica entre os filosofos
	public boolean justicaFilosofo() {
		
		//'verificacaoThis' tem os dados deste filosofo que esta sendo verificado na justica
		String verificacaoLeft = "", verificacaoRight = "", verificacaoThis = "";
		boolean okLeft = false, okRight = false;
		
		// Verifica se este filosofo eh o ultimo da mesa
		if(getIdFilosofo() == 4) {
			//Carrega as informacoes dos filosofos dos dois lados
			verificacaoRight = getJustica(0);
			verificacaoLeft = getJustica((getIdFilosofo()-1));
		}
		
		//Verifica se este filosofo eh o primeiro da mesa
		if(getIdFilosofo() == 0) {
			// Carrega as informacoes dos filosofos dos dois lados
			verificacaoRight = getJustica((getIdFilosofo()+1));
			verificacaoLeft = getJustica(4);	
		}
		
		if (getIdFilosofo() != 0 && getIdFilosofo() != 4) {
			// Carrega as informacoes dos filosofos dos dois lados
			verificacaoRight = getJustica((getIdFilosofo()+1));
			verificacaoLeft = getJustica((getIdFilosofo()-1));			
		}
		
		//Carrega os dados do filosofo que esta sendo avaliado
		verificacaoThis = getJustica((getIdFilosofo()));
		
		String tempLeft[] = new String[2];
		String tempRight[] = new String[2];
		String tempThis[] = new String[2];
		
		//Divide a informacao p avaliar separadamente
		tempLeft = verificacaoLeft.split(";");
		tempRight = verificacaoRight.split(";");
		tempThis = verificacaoThis.split(";");
		
		
		//Obtem valores para avaliacao
		okLeft = (Integer.parseInt(tempThis[1]) - Integer.parseInt(tempLeft[1])) < getDiferenca();
		okRight = (Integer.parseInt(tempThis[1]) - Integer.parseInt(tempRight[1])) < getDiferenca();
		
		//Verifica se pode pedir garfo ou nao e retorna o resultado
		if(okLeft && okRight) {
			return true;
		}
		else {
			return false;
		}
	}
		
	
	/* ########################################## Setters and Getters ########################################## */
	
	public int getPortaRemota() {
		return portaRemota;
	}

	public void setPortaRemota(int portaRemota) {
		this.portaRemota = portaRemota;
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
	
	public Cliente getPedinte() {
		return pedinte;
	}

	public void setPedinte(Cliente pedinte) {
		this.pedinte = pedinte;
	}

	public Servidor getOuvinte() {
		return ouvinte;
	}
	
	public void setOuvinte(Servidor ouvinte) {
		this.ouvinte = ouvinte;
	}
	
	public int getIdFilosofo() {
		return ID;
	}

	public void setIdFilosofo(int iD) {
		this.ID = iD;
	}

	public void setJustica(int idFilosofo, String justica){
		this.justica[idFilosofo] = justica;
	}

	public String getJustica(int idFilosofo){
		return this.justica[idFilosofo];
	}
	
	public int getQtdComidas() {
		return qtdComidas;
	}

	public void setQtdComidas(int qtdComidas) {
		this.qtdComidas = qtdComidas;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Garfo getGarfo() {
		return garfo;
	}

	public void setGarfo(Garfo garfo) {
		this.garfo = garfo;
	}
	
	public int getMaximo() {
		return maximo;
	}

	public void setMaximo(int maximo) {
		this.maximo = maximo;
	}
	
	public boolean isAtivoEnviar() {
		return ativoEnviar;
	}

	public void setAtivoEnviar(boolean ativoEnviar) {
		this.ativoEnviar = ativoEnviar;
	}
	
	public boolean isAtivoReceber() {
		return ativoReceber;
	}

	public void setAtivoReceber(boolean ativoReceber) {
		this.ativoReceber = ativoReceber;
	}
	
	public int getDiferenca() {
		return diferenca;
	}

	public void setDiferenca(int diferenca) {
		this.diferenca = diferenca;
	}

}