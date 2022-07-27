package session;

import java.util.TimerTask;

public class SessionTransferTimer extends TimerTask {
	
	private SessionTransferManager sessionTransferManager;
	private String sessionCode;
	
	public SessionTransferTimer(SessionTransferManager sessionTransferManager, String sessionCode) {
		this.sessionTransferManager = sessionTransferManager;
		this.sessionCode = sessionCode;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000*60);
			sessionTransferManager.removeSessionTransfer(sessionCode);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
