package cc.tallerdinamo.musicalsuit.bodysequencer;

import processing.core.PApplet;

public class TimeManager {
	PApplet p5;
	private int bpm;
	private long tempoBase;
	private long estabilizadorTempo;
	private int timeCount;
	private int bitsForLoop;
	private long unidadeTempo;
	private boolean novoBit;
	
	public TimeManager(PApplet _p5, int _bitsForLoop) {
		p5 = _p5;
		bitsForLoop = _bitsForLoop;
		setBpm(120);
		tempoBase = p5.millis();
		novoBit = false;
	}
	
	public void setBpm (int newBpm) {
		bpm = newBpm;
		unidadeTempo = 60000 / bpm;
	}
	
	public void atualizaTempoCont () {
		if (p5.millis() > tempoBase + unidadeTempo ) {
			estabilizadorTempo = p5.millis() - (tempoBase + unidadeTempo);
			tempoBase = p5.millis() - estabilizadorTempo;
			
			timeCount++;
			novoBit = true;
		} else {
			novoBit = false;
		}
		timeCount = timeCount%bitsForLoop;
	}
	public int getTimeCount(){
		return timeCount;
	}
	public boolean getNovoBit (){
		return novoBit;
	}
}
