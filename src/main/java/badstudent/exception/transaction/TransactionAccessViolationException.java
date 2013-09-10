package badstudent.exception.transaction;

import badstudent.common.Constants.transactionState;

public class TransactionAccessViolationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TransactionAccessViolationException(){
        super();
    }

    public int getCode() {
        return -1;
    }


}
