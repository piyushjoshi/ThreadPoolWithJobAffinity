package apigee.assignment.threadpool;

public class TaskAbandonmentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2678925926289288647L;

	public TaskAbandonmentException() {
		super();
	}

	public TaskAbandonmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TaskAbandonmentException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskAbandonmentException(String message) {
		super(message);
	}

	public TaskAbandonmentException(Throwable cause) {
		super(cause);
	}

}
