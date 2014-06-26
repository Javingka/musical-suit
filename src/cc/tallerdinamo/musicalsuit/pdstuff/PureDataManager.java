package cc.tallerdinamo.musicalsuit.pdstuff;

/* IMPORTANTE
 * 1. Para o uso de libPd, é precisso ter aberto em eclipse o projeto PdCore e ter linkado ele com
 * o atual projeto.
 * Click dereito no nome do projeto / Properties / Android / Libraries / Add / PdCore
 * + info: https://github.com/libpd/pd-for-android
 * 
 * 2. Tem que criar uma pasta dentro de res, com o nome "raw" e botar aí o patch de PD zipado
 * neste caso o codigo esta escreto para procurar um arquivo de nome "Patch.zip"
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;
import org.puredata.core.utils.PdDispatcher;

import android.util.Log;
import processing.core.PApplet;

public class PureDataManager  {
	//esta variavel entrega um TAG para imprimir na consola mensagens que possam ser filtrados pelo TAG
	private static final String TAG = "PureDataManager";
	PdDispatcher dispatcher = new PdUiDispatcher();
	private static final int SAMPLE_RATE = 44100;
	PApplet p5;
	
	ClassForPdCallBack classPdCB; //classe que vai enviar info para os metodos CallBacks  
	ArrayList <String> keyFloats;
	int chanelsIn; //canales de entrada
	int ticksPerBuffer;
	
	public PureDataManager (PApplet _p5) {
		p5 = _p5;
		
		classPdCB = new ClassForPdCallBack((PdListenerCallBack) p5); //se inicializa com a Interface da clase que implementa à atual
		keyFloats = new ArrayList<String>();
		chanelsIn = 0; //canales de entrada por padrāo
		ticksPerBuffer = 1; // ticks per buffer por padrāo
	}
	/**
	 * @param int / quantidade de canais de entrada
	 */
	public void setChanelIn (int newChanelIn) {
		chanelsIn = newChanelIn; //canales de entrada
	}
	
/**
 * When specifying the number of ticks per buffer, you are effectively choosing the duration of the audio buffer
	  through which Pd will exchange audio samples with the operating system. For example, if you request four ticks
	  per buffer at a sample rate of 44100Hz, then the duration will be 4 * 64 / 44100Hz = 5.8ms
 * @param integer newTicksPerBuffer
 */
	public void setTicksPerBuffer (int newTicksPerBuffer){
		ticksPerBuffer = newTicksPerBuffer;
	}
	
/**
 * Funçāo que agrega os string chaves para receber Floats de PD
 * @param string da palabra chave dada em PD
 */
	public void addSendMessagesFromPD(String key) {
		keyFloats.add(key);
	}
/** Abre o Patch de PD, segundo o nome do patch e o caminho para obter o id do recurso raw no arquivo .zip
 * 
 * @param nome do patch '<nome>.pd'
 * @param PathToIdResourse
 * @return
 */
	public int openPatch(final String patch, int PathToIdResourse) {
		final File dir = p5.getFilesDir();
		final File patchFile = new File(dir, patch);
		int out=-1;
		try {
			IoUtils.extractZipResource(p5.getResources().openRawResource(PathToIdResourse), dir, true);
			out = PdBase.openPatch(patchFile.getAbsolutePath());
			Log.d(TAG, "out" + out);
		} catch (final IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString() + "; exiting now");
			finish();
		}
		return out;
	}	
	/** Este método pega um string de elementos para enviar a Pd separados por um espaço " "
	 *  e converte numa lista que vao ser enviada para o Pd
	 * @param dest
	 * @param s -> A lista tem que ser enviada como string
	 */
	public void sendList(String dest, String s) {  
		  String[] pieces = s.split(" ");  
		  //println ("Pieces[0]: "+pieces[0] +" pieces[1]: " +pieces[1] + " pieces[2]: " +pieces[2]);
		  Object[] list = new Object[pieces.length];

		  for (int i=0; i < pieces.length; i++) {  
		    try {  
		      list[i] = Float.parseFloat(pieces[i]);  
		    } catch (NumberFormatException e) {  
		      list[i] = pieces[i];  
		    }  
		  }
		//  println ("list[0]: "+list[0] +" list[1]: " +list[1] + " list[2]: " +list[2]);
		  PdBase.sendList(dest, list);  
	}
	public float[] getArrayFromPd(String arrayName) {
		int sizeArray = PdBase.arraySize(arrayName);
		float[] putHereTheArray = new float[sizeArray];
		PdBase.readArray(putHereTheArray, 0, arrayName, 0, sizeArray);
		
		return putHereTheArray;
	}
	public void sendArrayToPD(float [] arrayToSend, String nameArrayInPd) {
		int sizeArray = arrayToSend.length;
		PdBase.writeArray(nameArrayInPd, 0,arrayToSend, 0, sizeArray);
	}
	
	public void onResume() {
		final int nIn = chanelsIn;
		Log.d(TAG, "Starting LibPD");
		if (AudioParameters.suggestSampleRate() < SAMPLE_RATE) {
			Log.d(TAG,"required sample rate not available; exiting");
			finish();
			return;
		}
		final int nOut = Math.min(AudioParameters.suggestOutputChannels(), 2);
		if (nOut == 0) {
			Log.d(TAG,"audio output not available; exiting");
			finish();
			return;
		}
		try {
			PdAudio.initAudio(SAMPLE_RATE, nIn, nOut, ticksPerBuffer, true);
			PdAudio.startAudio(p5);
			PdBase.setReceiver(dispatcher);
			
			for (int i = 0 ; i < keyFloats.size() ; i ++) {
				recividorMensagensPd(keyFloats.get(i));
			}
			
		} catch (final IOException e) {
			Log.e(TAG, e.toString());
		}
	}
	
	private void recividorMensagensPd(String etiquetaSend){
		//funçāo para pegar dados enviados desde o pd 
		dispatcher.addListener(etiquetaSend, new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, float x) {
				classPdCB.sendToCallBackMethod(source, x);
			}
			public void receiveBang(String source){
				classPdCB.sendToCallBackMethod(source);	
			}
			public void receiveSymbol(String source, String symbol){
				classPdCB.sendToCallBackMethod(source, symbol);		
			}
			public void receiveList(String source, Object... args){
				classPdCB.sendToCallBackMethod(source, args);		
			}
			public void receiveMessage(String source, String symbol, Object... args){
				classPdCB.sendToCallBackMethod(source, symbol, args);
			}
		});
	}
	public void finish() {
		Log.d(TAG, "Finishing for some reason");
		cleanup();
	}
	
	public void cleanup() {
		// make sure to release all resources
		PdAudio.stopAudio();
		PdBase.release();
	}
	public void onPause() {
		PdAudio.stopAudio();
	}
	public void onDestroy() {
		cleanup();
	}
}
