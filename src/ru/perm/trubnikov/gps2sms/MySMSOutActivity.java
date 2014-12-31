package ru.perm.trubnikov.gps2sms;

public class MySMSOutActivity extends MySMSActivity {

	@Override
	protected String getSMSSource() {
		return "sent";
	}
	
}
