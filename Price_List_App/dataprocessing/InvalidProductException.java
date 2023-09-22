package dataprocessing;

public class InvalidProductException extends Exception {
	/**
	 * Exception thrown when an invalid product attempts to be created
	 */
	private static final long serialVersionUID = -2546270434495007608L;

	public InvalidProductException() {
		this("Invalid data for product.");
	}

	public InvalidProductException(String message) {
		super(message);
	}
}
