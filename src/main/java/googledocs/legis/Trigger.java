package googledocs.legis;

public class Trigger {
	
	
	
	public abstract static class Event {
		public static final Type eventType = null;
	}
	
	public static class PurchaseEvent extends Trigger.Event {
		public static final Type eventType = Type.PURCHASE;
		public PurchaseEvent() {
			super();
		}
	}
	
	public static class TradeEvent extends Trigger.Event {
		public static final Type eventType = Type.TRADE;
		public TradeEvent() {
			super();
		}
	}
	
	public static class ClaimEvent extends Trigger.Event {
		public static final Type eventType = Type.CLAIM;
		public ClaimEvent() {
			super();
		}
	}
	
	public enum Type {
		
		PURCHASE, TRADE, CLAIM
		
	}
	
}
