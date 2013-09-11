package carpool.exception.notification;

import carpool.exception.PseudoException;

public class NotificationOwnerNotMatchException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "NotificationOwnerNotMatch";
	
	public NotificationOwnerNotMatchException(){
        super();
    }
	
	public NotificationOwnerNotMatchException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 9;
    }

}