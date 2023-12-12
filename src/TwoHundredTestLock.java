/**
 * 
 */

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class TwoHundredTestLock {

	/**
	 * Make 200 Threads
	 * @param args
	 * @throws IOException, InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		int portNumber = 8080;
		/***/
		TestSynchronizedServer s = new TestSynchronizedServer(portNumber);
		s.start();
		
		ExecutorService service = Executors.newFixedThreadPool(200);
		
		for (int i = 0; i < 200; i++) {
			service.execute(new TestSynchronizedClient(portNumber, "Client"));
		}
		
		TimeUnit.SECONDS.sleep(1);
		System.out.println("End");
				
	}

}
