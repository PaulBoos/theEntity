package timers;

import core.BotInstance;

import java.time.Instant;

public class TimerQueue {
	
	Timer head;
	Thread timerThread;
	
	public TimerQueue() {
		(timerThread = new Thread(this::loop)).start();
	}
	
	public void addTimer(Timer timer) {
		if(head == null) {
			head = timer;
			if(timerThread.getState() == Thread.State.TERMINATED) (timerThread = new Thread(this::loop)).start();
		}
		else if(head.getExecutionTime() <= timer.getExecutionTime()) head.addTail(timer);
		else head = timer.addTail(head);
	}
	
	public Timer getHead() {
		return head;
	}
	
	private void loop() {
		try {
			while(true) {
				if(head == null) return;
				if(head.check(true, BotInstance.botInstance)) {
					head = head.tail;
					System.gc();
				}
				Thread.sleep(10000);
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static class Timer {
		
		long executionTime;
		Lambda code;
		Timer tail;
		
		public Timer(Instant instant, Lambda code) {
			this.code = code;
			executionTime = instant.getEpochSecond();
			System.out.println("New Timer for " + instant.toString().replace("T"," @ ").replace("Z",""));
		}
		
		public Timer(long epochSeconds, Lambda code) {
			this.code = code;
			executionTime = epochSeconds;
			System.out.println("New Timer for " + Instant.ofEpochSecond(epochSeconds).toString().replace("T"," @ ").replace("Z",""));
		}
		
		boolean check(boolean execute, BotInstance botInstance) {
			if(execute) {
				if(Instant.now().getEpochSecond() >= executionTime) {
					code.execute(botInstance);
					return true;
				} else return false;
			} else {
				return Instant.now().getEpochSecond() >= executionTime;
			}
		}
		
		Timer addTail(Timer newTail) {
			if(tail == null) tail = newTail;
			else if(tail.getExecutionTime() <= newTail.getExecutionTime()) tail.addTail(newTail);
			else tail = newTail.addTail(tail);
			return this;
		}
		
		public long getExecutionTime() {
			return executionTime;
		}
		
	}
	
	public interface Lambda {
		
		void execute(BotInstance instance);
		
	}
	
}
