package cc.tallerdinamo.musicalsuit.bodysequencer.sequencerviewer;

import java.util.ArrayList;

import android.util.Log;

import processing.core.PApplet;
import processing.core.PVector;

/* IMPORTANTE para ter em conta na hora de programar
 * 
 * 1- Quando cria uma nova clase o mínimo necessario é criar uma variavel de tipo PApplet
 * essa variavel vai permitir usar dentro da clase os metodos de processing.
 * 
 * 2- é preciso declara se as variaveis e os metodos sāo public ou private.
 * tem em conta que em Processing todos os metodos e variaveis sāo public por padrāo
 */

public class SequencerViewer {
/* p5 vai ser inicializada com a variavel entregada pela clase que vai instanciar ela 
*  ou seja a clase PAppletInicial
*/
	PApplet p5;
	private ArrayList <TrackSeq> listOfTracks;
	private int numberOfTracks;
	private int numberOfBits;
	private float radioInt, radioExt;
	private PVector posCenter;
	private int actualBit;
	private boolean isSequencerRunning;
	
	public SequencerViewer( PApplet _p5, int _numberTracks, int _numberBits) {
		p5 = _p5;
		listOfTracks = new ArrayList<TrackSeq> ();
		numberOfTracks = _numberTracks;
		numberOfBits = _numberBits;
		
		createSequencer(); //Create each of the tracks
		actualBit = 0;
		isSequencerRunning = true;
	}
	
	private void createSequencer(){
		//calcule the shorter side.
		float shorterSide = PApplet.min(p5.height, p5.width);
		radioInt = shorterSide*.075f;
		radioExt = shorterSide*.45f;
		
		//the position of the sequencer
		posCenter =  new PVector ( radioExt , p5.height*.5f);
		
		float angleTem = PApplet.TWO_PI / numberOfTracks;
		float diameterBitsBoxs = (shorterSide / 2) / (numberOfBits * 1.5f) ; //p5.width*.01f;
		p5.pushMatrix();
		p5.translate(posCenter.x, posCenter.y);
		for (int t = 0 ; t < numberOfTracks ; t ++) {
			float angT = angleTem * t;
			listOfTracks.add(new TrackSeq (p5, numberOfBits, radioInt, radioExt, diameterBitsBoxs,  angT) );
		}
		p5.popMatrix();
		
		
	}
/* Por ejemplo um metodo para desenhar uma elipse no centro da tela
 * É importante notar o p5. antes do chamado dos metodos de Processing
 */
	public void draw(){
		p5.pushMatrix();
		p5.translate(posCenter.x, posCenter.y);
		
		if (isSequencerRunning) {
			p5.noFill();
			p5.stroke(255);
			p5.strokeWeight(1);
			float r = listOfTracks.get(0).getDiamByNumberBit(actualBit);
			p5.ellipse(0,0, r*2, r*2);
		}
		
		for (TrackSeq ts : listOfTracks) {
			ts.draw();
		}
		
		
		
		p5.popMatrix();
		
	}
	
	public void setActualBit (int _actualBit) {
		actualBit = _actualBit;
	}
	
}
