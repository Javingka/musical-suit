package cc.tallerdinamo.musicalsuit.bodysequencer.sequencerviewer;

import processing.core.PApplet;
import processing.core.PVector;

public class BasicButton {
	public PApplet p5;
	public PVector pos;
	public float diam;
	protected int colorOff;
	protected int colorOn;
	protected int colorFondoApp;
	protected int colorLinha;
	protected int colorText;
	private float tamanhoTexto;
	private float tamanhoTextoNombre;
	public int cargaColor;
	public boolean ligado;
	public boolean mudandoOn;
	public boolean mudandoOff;
	public boolean click; //só para saber quando foi clicado
	private String etiqueta;
	public String textoBotao;
	private String posVer, posHor;
	private float tx, ty; //posicao do texto do botao
	private boolean botaoToggle;
	private int colorEtiqueta;
	public float chaf;
	private boolean colorOffPorDefecto;
	
	public BasicButton (PApplet _p5, PVector _pos, float _diam, int _color) {
		p5 = _p5;
		diam = _diam;
		pos = _pos;
		colorOn = _color;
		chaf = diam*.2f;
		int hc = (int) p5.hue(colorOn);
		int st = (int) p5.saturation(colorOn);
		int br = (int) p5.brightness(colorOn);
		colorOff = p5.color(hc, st,br, 255); //no tiene cambios en el color entre encendido o apagado
		colorOffPorDefecto = true;
		cargaColor = colorOff;
		colorLinha = p5.color(120);
		colorText = p5.color(0);
		tamanhoTexto = p5.height*.05f;
		ligado = false;
		click = false;
		p5.textAlign(PApplet.CENTER, PApplet.CENTER);
		p5.textSize(tamanhoTexto);
		tamanhoTextoNombre = tamanhoTexto;
		setPosicaoTexto("abaixo", "centro");
		textoBotao = " ";//"Pincel X";
		botaoToggle = false;
		turnBotaoOff();
		setColorEtiqueta(p5.color (0) );
		
	}
/**
 * 
 * @param ver String: centro, esquerda ou direitarectangulo
 * @param hor String: centro, acima ou abaixo
 */
	public void setPosicaoTexto(String ver, String hor){
		posVer = ver;
		posHor = hor;
		atualizaDataTexto();
	}
	public void setBotaoComoToggle(){
		botaoToggle = true;
	}
	public void atualizaDataTexto() {
		int pTx, pTy;
		if (posHor == "esquerda") {
			pTx = PApplet.RIGHT;
			tx = -diam;
		} else if (posHor == "direita") {
			pTx = PApplet.LEFT;
			tx = diam;
		} else {
			pTx = PApplet.CENTER;
			tx = 0;
		}
		if (posVer == "acima") {
			pTy = PApplet.TOP;
			ty = -diam;
		} else if (posVer == "abaixo") {
			pTy = PApplet.BOTTOM;
			ty = diam*1.25f;
		} else {
			pTy = PApplet.CENTER;
			ty = 0;		
		}
		p5.textAlign(pTx, pTy);
	}
	public void desenhaTexto() {
		
		p5.fill(colorFondoApp);
		p5.textSize(tamanhoTextoNombre*1.1f);
		p5.text(textoBotao, tx, ty);
		
		p5.fill(colorText);
		p5.textSize(tamanhoTextoNombre);
		p5.text(textoBotao, tx, ty);
	}
	public void desenhaCubreTexto() {
		p5.pushMatrix();
		p5.pushStyle();
		p5.translate(pos.x, pos.y );
		p5.fill(colorFondoApp);
		p5.rectMode(PApplet.CENTER);
		p5.noStroke();
		p5.rect(tx, ty - tamanhoTextoNombre /2, p5.textWidth(textoBotao)*1.3f, tamanhoTextoNombre*1.2f);
	//		p5.textSize(tamanhoTextoNombre);
	//		p5.text(textoBotao, tx, ty);
		p5.popMatrix();
		p5.popStyle();
	}
	public void desenhaCubreTexto(String tex, float tx, float ty) {
		p5.pushMatrix();
		p5.pushStyle();
		p5.translate(tx, ty );
		p5.fill(colorFondoApp);
		p5.rectMode(PApplet.CENTER);
		p5.noStroke();
		p5.rect(0,0, p5.textWidth(tex)*1.1f, tamanhoTextoNombre*2.2f);
	//		p5.textSize(tamanhoTextoNombre);
	//		p5.text(textoBotao, tx, ty);
		p5.popMatrix();
		p5.popStyle();
	}
	public void setColor(int novaCor) {
		colorOn = novaCor;
		if ( colorOffPorDefecto )
			colorOff = p5.color(novaCor, 255);
		atualizaCor();
	}
	public void setNomeBotao(String novoNome){
		textoBotao = novoNome;
	}
	public void setEtiqueta(String newLabel) {
		etiqueta = newLabel;
	}
	public void setNovaPos( PVector nPos) {
		pos = nPos;
	}
	public void desenharBotaoCircular() {
		p5.pushMatrix();
		p5.pushStyle();
		p5.translate(pos.x, pos.y);
		seleccionEstilo();
		p5.ellipse(0,0, diam, diam);
		desenhaTextos();
		p5.popStyle();
		p5.popMatrix();
	}
	
	public void desenharBotaoRec() {
		p5.pushMatrix();
		p5.pushStyle();
		p5.rectMode(PApplet.CENTER);
		p5.translate(pos.x, pos.y);
		seleccionEstilo();
		if (ligado)
			p5.rect(0,0, diam*.9f, diam*.9f, chaf, chaf, chaf, chaf);
		else
			p5.rect(0,0, diam, diam, chaf, chaf, chaf, chaf);
		desenhaTextos();
		
		p5.popStyle();
		p5.popMatrix();
	}
	public void desenharBotaoCirComCor(int colorIn) {
		p5.pushMatrix();
		p5.pushStyle();
		p5.rectMode(PApplet.CENTER);
		p5.translate(pos.x, pos.y);
		
		p5.fill(colorIn);
		p5.strokeWeight(5);
		p5.stroke(colorIn);
		
		p5.ellipse(0, 0, diam, diam);
		
		p5.popStyle();
		p5.popMatrix();
	}
	public void desenharBotaoRecComCor(int colorIn) {
		p5.pushMatrix();
		p5.pushStyle();
		p5.rectMode(PApplet.CENTER);
		p5.translate(pos.x, pos.y);
		
		p5.fill(colorIn);
		p5.strokeWeight(7);
		p5.stroke(colorIn);
		
		p5.rect(0,0, diam, diam, chaf, chaf, chaf, chaf);
		
		p5.popStyle();
		p5.popMatrix();
	}
	public void seleccionEstilo(){
		p5.fill(cargaColor);
		if (ligado) {
			p5.strokeWeight(5);
			p5.stroke(255);//colorLinha);
		}else {
			p5.strokeWeight(2);
			p5.stroke(colorLinha);
			p5.noStroke();
		}
		
	}
	public void desenhaTextos() {
		if (textoBotao != null) {
			atualizaDataTexto();
			desenhaTexto();
		}
		if (etiqueta != null) {
			p5.pushStyle();
			p5.textAlign(PApplet.CENTER, PApplet.CENTER);
			p5.textSize(tamanhoTexto);
			p5.fill(colorText);
			p5.text(etiqueta, 0,0);
			p5.popStyle();
		}	
	}
	
	public boolean botaoToogleOnClick (PVector evaluar) {
		boolean ligadoPrev = ligado;
		boolean resp = false;
		click = false;
		float dist = PVector.dist(pos, evaluar);
		
		if (botaoToggle && !ligado) {
			if (dist < diam ) {
				resp = true;
				click = true;
				ligado = !ligado;
				atualizaCor();
			}
			
			testMudanca(ligadoPrev);
		}
		return resp;
	}
	public boolean botaoOnClick (PVector evaluar) {
		boolean ligadoPrev = ligado;
		boolean resp = false;
		click = false;
		float dist = PVector.dist(pos, evaluar);
		
		if (dist < diam/2 ) {
			resp = true;
			click = true;
			ligado = !ligado;
			atualizaCor();
		}
		
		testMudanca(ligadoPrev);
				
		return resp;
	}
	public void turnBotaoOn() {
		boolean ligadoPrev = ligado;
		ligado = true;
		testMudanca(ligadoPrev);
		atualizaCor();
	}
	public void turnBotaoOff() {
		boolean ligadoPrev = ligado;
		ligado = false;
		testMudanca(ligadoPrev);
		atualizaCor();
	}

	public boolean getEstadoBotao(){
		return ligado;
	}
	public void setEstadoBotao(boolean novoEstado){
		boolean ligadoPrev = ligado;
		
		ligado = novoEstado;
		testMudanca(ligadoPrev);
		atualizaCor();
	}
	
	public void atualizaCor(){
		if (ligado) {
			cargaColor = colorOn;
		} else {
			cargaColor = colorOff;
		}
	}

	public int getColorOn() {
		return colorOn;
	}
	public void setColorOff(int colorN){
		colorOff = colorN;
		colorOffPorDefecto = false;
		if (!ligado) //se o boton esta em estado de desligado, pega o colorOff como ativo
			cargaColor = colorOff;
	}
	public void setColorLinha(int colorN) {
		colorLinha = colorN;
	}
	public void testMudanca(boolean ligadoPrev) {
		mudandoOn = false;
		mudandoOff = false;
		
		if (ligado && !ligadoPrev) {
			mudandoOn = true;
		}
			
		
		if (!ligado && ligadoPrev) {
			mudandoOff = true;
		}
	}
	public void setColorText(int colorN) {
		colorText = colorN;
	}
	public void setTamanhoTexto(float val) {
		tamanhoTextoNombre = val;
	}
	public void setColorFondo(int col) {
		col = colorFondoApp;
	}
	public void setColorEtiqueta(int nCor) {
		colorEtiqueta = nCor;
	}
	public PVector getPosicion(){
		return pos;
	}
}
