package cc.tallerdinamo.musicalsuit.bodysequencer.sequencerviewer;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class TrackSeq {
	PApplet p5;
	private int numberOfBits;
	private float widthSeq, heightSeq;
	private float radio1, radio2;
	private float anglePos;
	private ArrayList <BitBox> listBits;
	
	private int colorOff_a;
	private int colorOff_b;
	
	private float distVar;
	
	public TrackSeq (PApplet _p5, int bits, float r1, float r2, float _diam, float _angPos) {
		p5 = _p5;
		numberOfBits = bits;
		radio1 = r1;
		radio2 = r2;
		widthSeq = radio2 - radio1;
		heightSeq = _diam;
		anglePos = _angPos;
		
		colorOff_a = p5.color(125,0,100);
		colorOff_b = p5.color(200,0,50 );
		
		listBits = new ArrayList <BitBox>(); //list of bits of the tracker sequence
		createTrack();
	}
	private void createTrack() {
		distVar = widthSeq / numberOfBits ;
		
		int color = colorOff_a;
		for (int b = 0 ; b < numberOfBits ; b++) {
			if (b % 4 == 0) {
				if (color == colorOff_a)
					color = colorOff_b;
				else
					color = colorOff_a;
			} 
			float diamVar = radio1 + distVar * b;
			PVector p = new PVector ( diamVar * PApplet.cos (anglePos), diamVar * PApplet.sin(anglePos) );
			listBits.add(new BitBox (p5, p, heightSeq, color) );
		}
	}
	public void draw(){
		
		for (BitBox bb : listBits){
			bb.desenharBotaoCircular();
		}
	}
	public float getDiamByNumberBit( int index ) {
		float val = radio1 + distVar * index;
		return val;
	}
	
}
