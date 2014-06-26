package cc.tallerdinamo.musicalsuit.bodysequencer;

/* IMPORTANTE a ter presente:
 * 
 * 1- Para poder importar PApplet, temos primeiro que botar na pasta "libs" o arquivo:
 * processing-android-core.jar que pode ser encontrado nas pasta Processing/Core ou
 * http://www.java2s.com/Code/Jar/p/Downloadprocessingandroidcorejar.htm
 * 
 * 2- Cada sketch de processing que é criado pelo meio de um 'intent' tem que ser declarado
 * no AndroidManifest.xml
 * 
 * 4- No final da classe tem os metodos proprios do ciclo de vida de uma Activity em Android
 * E importante por que no onCreate pegamos dados que podem ser enviados desde o Main Activity
 * 
 * 3- Ao utilizar float tem que agregar ao numero uma 'f' no final. Por exemplo 3.14f
 */


import org.puredata.core.PdBase;

import cc.tallerdinamo.musicalsuit.arduinoreceiver.ArduinoReceiver;
import cc.tallerdinamo.musicalsuit.bodysequencer.sequencerviewer.SequencerViewer;
import cc.tallerdinamo.musicalsuit.multitouch.MTListenerCallBack;
import cc.tallerdinamo.musicalsuit.multitouch.MultiTouchP;
import cc.tallerdinamo.musicalsuit.pdstuff.PdListenerCallBack;
import cc.tallerdinamo.musicalsuit.pdstuff.PureDataManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;
import processing.core.PApplet;

public class PAppletInicialBodySeq extends PApplet  implements PdListenerCallBack,MTListenerCallBack {
	//esta variavel entrega um TAG para imprimir na consola mensagens que possam ser filtrados pelo TAG
	private static final String TAG = "PAppletInicialBodySeq";
		
	String stringVal;
	float numeroFloat;

//Codigo Bluetooth 
	private static final String DEVICE_ADDRESS =  "00:14:03:11:35:83";	
	private ArduinoReceiverIn arduinoReceiver = new ArduinoReceiverIn();
	
// Criamos uma clase especialmente para botar ali todo o codigo de libPD.
	PureDataManager pdManager;
	MultiTouchP multiTouch;
	
//Classes 
	SequencerViewer sequencerViewer;
	TimeManager timeManager;
	
	public void setup(){
		Log.i("PAppletInicialBodySeq", "entered setup()");
		multiTouch = new MultiTouchP(this);
		sequencerViewer = new SequencerViewer(this, 6, 16);
		timeManager = new TimeManager(this, 16);
		textSize(50);
	}
	
	public void draw() {
		background(200);
		
		timeManager.atualizaTempoCont();
		sequencerViewer.draw();
		
		if (timeManager.getNovoBit() ) {
			sequencerViewer.setActualBit(timeManager.getTimeCount());
		}
		text(timeManager.getTimeCount(), width*.8f, height*.5f );
//		multiTouch.drawInfo();
		
	
//		Log.i(TAG,  "Data received: " + d);
	}
	
	
	
/* METODOS PROPRIOS DE UM ACTIVITY, ligados direitamente ao ciclo de vida	
 * http://www.devmedia.com.br/entendendo-o-ciclo-de-vida-de-uma-aplicacao-android/22922
 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		//pegamos os valores "extras" que enviamos desde o MainActivity
		Bundle extras=getIntent().getExtras(); //criamos um objeto que pega todos os extras existentes no intent
		stringVal = extras.getString("Um String"); // pegamos o valor segundo a etiqueta dada no MainActivity
		numeroFloat = extras.getFloat("Um float"); // pegamos o valor segundo a etiqueta dada no MainActivity
		
	//Inicializaçāo do objeto que vai gerenciar o Pd
		pdManager = new PureDataManager(this);
	//coloca o nome do Patch, nāo confundir com o nome do arquivo .zip, e logo coloca 'path' desde onde pega o zip
		pdManager.openPatch("entonador.pd", cc.tallerdinamo.musicalsuit.R.raw.patch); 
		pdManager.setTicksPerBuffer(1);
		pdManager.setChanelIn(0);
	//Agrega todos os Strings chaves que vāo receber dados
		pdManager.addSendMessagesFromPD("Sfloat_0");
		pdManager.addSendMessagesFromPD("Sfloat_1");
		
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		// in order to receive broadcasted intents we need to register our receiver
		registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
		
		// this is how you tell Amarino to connect to a specific BT device from within your own code
		Amarino.connect(this, DEVICE_ADDRESS);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		// if you 	connect in onStart() you must not forget to disconnect when your app is closed
		Amarino.disconnect(this, DEVICE_ADDRESS);
		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoReceiver);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		pdManager.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
		pdManager.onPause();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		pdManager.onDestroy();
	}
	@Override
	public void finish() {
		super.finish();
		pdManager.finish();
	}

/* MESSAGES TO SEND DATA TO PD. Para enviar mensagens pra PD usar os seguentes metodos
 * 	PdBase.sendBang(String receiver);
  	PdBase.sendFloat(String receiver, float value);
    PdBase.sendSymbol(String receiver, String symbol);
	PdBase.sendList(String receiver, Object... list);
	PdBase.sendMessage(String receiver, String message, Oject... list);
	
	TO GET AN ARRAY FROM PD	
	float [] newArray = pdManager.getArrayFromPd("nome do array em PD"); este metodo retorna o array.
	TO SEND AN ARRAY TO PD	
	pdManager.sendArrayToPD( float [] arrayToSend , "nome do array em PD"); este metodo envia um array de float para um outro array em PD
	
 */
	
//CALLBACKS FROM PD As seguentes funçōes sāo chamadas quando tiver alguma mensagem nova desde PD
	@Override
	public void callWhenReceiveFloat(String key, float val) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "recebendo do PD = " + key + ": " + val );
	}
	@Override
	public void callWhenReceiveBang(String key) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void callWhenReceiveSymbol(String key, String symbol) {
		// TODO Auto-generated method stub
	}
	@Override
	public void callWhenReceiveList(String key, Object... args) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void callWhenReceiveMessage(String key, String symbol,
			Object... args) {
		// TODO Auto-generated method stub
	}
	
//Método callback, que é chamado cada vez que tem um novo evento na tela
	@Override
	public void screenTouched(int id, float x, float y) {
		// TODO Auto-generated method stub
		Log.i(TAG, "mutiTouch, toque em x:" + x + " y: " + y + " e id: " + id );
	}
	@Override
	public void screenTouchedReleased(int id) {
		// TODO Auto-generated method stub
		Log.i(TAG, "mutiTouch, released no id: " + id );
	}

	@Override
	public void screenTouchedDragged(int id, float x, float y, float dist, float ang) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "Touch dragged, toque em x:" + x + " y: " + y + " e id: " + id + " dist: "+dist+" angulo: "+ang);
	}
	@Override
	public void screenTouchedPinch(float pinchMag) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Um Belisco de :" +pinchMag);
	}
	
	public boolean surfaceTouchEvent(MotionEvent me) {
		multiTouch.surfaceTouchEvent(me);
	    return super.surfaceTouchEvent(me);
	}

	public class ArduinoReceiverIn extends BroadcastReceiver {
		private String msg = "nada";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String data = null;
			
			// the device address from which the data was sent, we don't need it here but to demonstrate how you retrieve it
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
			
			
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support differnt data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA){
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA); 
				msg = data;
				Log.i("TAG",  "Data received: " + data);
				if (data != null){
//					mValueTV.setText(data);
					try {
						// since we know that our string value is an int number we can parse it to an integer
						final int sensorReading = Integer.parseInt(data);
//						mGraph.addDataPoint(sensorReading);
					} 
					catch (NumberFormatException e) { /* oh data was not an integer */ }
				}
			}
		}
	}
}
