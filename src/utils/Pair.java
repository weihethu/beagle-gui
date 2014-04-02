package utils;

/**
 * Utility class Pair
 * @author Wei He
 *
 * @param <T> type of 1st element
 * @param <U> type of 2nd element
 */
public class Pair<T, U> {
	/**
	 * first element
	 */
	private T first;
	/**
	 * second element
	 */
	private U second;

	/**
	 * constructor
	 */
	public Pair() {
		first = null;
		second = null;
	}
	
	/**
	 * constructor
	 * @param first 1st element
	 * @param second 2nd element
	 */
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * get 1st element
	 * @return 1st element
	 */
	public T getFirst() {
		return first;
	}
	
	/**
	 * get 2nd element
	 * @return 2nd element
	 */
	public U getSecond() {
		return second;
	}
	
	/**
	 * set 1st element
	 * @param value 1st element value
	 */
	public void setFirst(T value) {
		first = value;
	}

	/**
	 * set 2nd element
	 * @param value 2nd element value
	 */
	public void setSecond(U value) {
		second = value;
	}
}
