package carpool.exception.transaction;

import carpool.common.Constants.transactionState;
import carpool.exception.PseudoException;

public class TransactionAccessViolationException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "TransactionAccessViolation";
	
	public TransactionAccessViolationException(){
        super();
    }
	
	public TransactionAccessViolationException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 6;
    }

}