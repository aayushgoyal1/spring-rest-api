public interface LoggingService {

	public void info (Class<?> sourceClass, String message);
	
	public void info (Class<?> sourceClass, String message, Object... args);
	
    public void debug (Class<?> sourceClass, String message);

    public void debug (Class<?> sourceClass, String message, Object... args);

    public void error (Class<?> sourceClass, String message);

    public void error (Class<?> sourceClass, String message, Object... args);

    public void trace (Class<?> sourceClass, String message);

    public void trace (Class<?> sourceClass, String message, Object... args);
}
