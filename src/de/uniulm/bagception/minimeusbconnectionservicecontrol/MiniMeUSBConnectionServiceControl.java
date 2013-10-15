package de.uniulm.bagception.minimeusbconnectionservicecontrol;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.philipphock.android.lib.services.ServiceUtil;
import de.philipphock.android.lib.services.observation.ServiceObservationActor;
import de.philipphock.android.lib.services.observation.ServiceObservationReactor;
import de.uniulm.bagception.broadcastconstants.BagceptionBroadcastContants;
import de.uniulm.bagception.service.USBConnectionServiceRemote;

public class MiniMeUSBConnectionServiceControl extends Activity implements ServiceObservationReactor, USBConnectionReactor{

	private boolean serviceOnline = false;
	private ServiceObservationActor observationActor;
	private USBConnectionActor usbConnectionActor;
	public static final String SERVICE_NAME = "de.uniulm.bagception.rfidapi.miniusbconnectionservice.service.USBConnectionService";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mini_me_usbconnection_service_control);
		observationActor = new ServiceObservationActor(this,SERVICE_NAME);
		usbConnectionActor = new USBConnectionActor(this);
	}

	
	
	@Override
	protected void onResume() {
		super.onResume();
		onServiceStopped(SERVICE_NAME);
		observationActor.register(this);
		usbConnectionActor.register(this);
		ServiceUtil.requestStatusForServiceObservable(this, SERVICE_NAME);
		
		sendRescanBroadcast();
		
		Intent startServiceIntent = new Intent(USBConnectionServiceRemote.class.getName());
		startService(startServiceIntent);	
		
		
	}
	@Override
	protected void onPause() {
		observationActor.unregister(this);
		usbConnectionActor.unregister(this);
		super.onPause();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		return true;
	}

	private void sendRescanBroadcast(){
		Intent i = new Intent();
		i.setAction(BagceptionBroadcastContants.USB_CONNECTION_BROADCAST_RESCAN);
		sendBroadcast(i);
	}

	@Override
	public void onServiceStarted(String serviceName) {
		TextView status = (TextView) findViewById(R.id.serviceStatus);
		status.setText("online");
		status.setTextColor(Color.GREEN);
		
		Button startStopBtn = (Button) findViewById(R.id.startStopService);
		startStopBtn.setText("stop service");
		startStopBtn.setEnabled(true);
		serviceOnline=true;
		sendRescanBroadcast();

	}

	@Override
	public void onServiceStopped(String serviceName) {
		
		TextView status = (TextView) findViewById(R.id.serviceStatus);
		status.setText("offline");
		status.setTextColor(Color.RED);
		
		Button startStopBtn = (Button) findViewById(R.id.startStopService);
		startStopBtn.setText("start service");
		startStopBtn.setEnabled(true);
		serviceOnline=false;
		
		TextView v = (TextView) findViewById(R.id.usbStatus);
		v.setText("unknown");
		v.setTextColor(Color.BLUE);
	}
	
	public void onStartStopService(View v){
		Button startStopBtn = (Button) findViewById(R.id.startStopService);
		startStopBtn.setEnabled(false);
		
		if (!serviceOnline){
			Intent startServiceIntent = new Intent(USBConnectionServiceRemote.class.getName());
			startService(startServiceIntent);	
		}else{
			Intent startServiceIntent = new Intent(USBConnectionServiceRemote.class.getName());
			stopService(startServiceIntent);
		}
	}



	@Override
	public void onUSBConnected() {
		TextView v = (TextView) findViewById(R.id.usbStatus);
		v.setText("connected");
		v.setTextColor(Color.GREEN);
	}



	@Override
	public void onUSBDisconnected() {
		TextView v = (TextView) findViewById(R.id.usbStatus);

		v.setText("disconnected");
		v.setTextColor(Color.RED);
	}
	
}
