import java.time.Instant;

public class TurnTimer {
	
	long executionTime;
	boolean executed;
	Lambda code;
	
	TurnTimer(Instant instant, Lambda code) {
		this.code = code;
		executionTime = instant.getEpochSecond();
		System.out.println("NEW TURN TIMER FOR " + instant.toString().replace("T"," @ ").replace("Z",""));
		new Thread(this::loop).start();
	}
	
	void loop() {
		try {
			while(true) {
				if(this.check(true, BotInstance.botInstance)) return;
				Thread.sleep(10000);
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	boolean check(boolean execute, BotInstance botInstance) {
		if(execute) {
			if(executed) return false;
			else if(Instant.now().getEpochSecond() >= executionTime) {
				code.execute(botInstance);
				executed = true;
				return true;
			}
			else return false;
		} else {
			return Instant.now().getEpochSecond() >= executionTime;
		}
	}
	
	public long getExecutionTime() {
		return executionTime;
	}
	
	public interface Lambda {
		
		public void execute(BotInstance instance);
		
	}
	
}
