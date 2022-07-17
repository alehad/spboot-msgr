package msgr.init;

public interface IInitializableComponent {

	// I've used this interface to allow deferred initialisation of various services and components which are injected through @Autowired. 
	// Initialising those components in the Constructor forces initialisation for all of those services, if @Autowired when in reality 
	// user may not wish to have those services available and may wish to control their availability via application.properties
	public boolean isInitialized();
	
	public void initialize();
}
