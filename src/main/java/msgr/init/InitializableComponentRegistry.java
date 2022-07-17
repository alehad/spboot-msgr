package msgr.init;

import java.util.List;

public class InitializableComponentRegistry<T extends IInitializableComponent> {

	private List<T> registry;

	private T activeComponent;
	
	public InitializableComponentRegistry(List<T> registry, String active) {
		this.registry = registry;
		setActive(active);
	}
	
	public T getActiveComponent() {
		return activeComponent;
	}
	
	public void setActive(String active) {
		activeComponent = registry.stream().filter(s -> s.getClass().getName().contains(active)).findFirst().get();
		if (activeComponent != null && !activeComponent.isInitialized()) {
			activeComponent.initialize();
		}
	}
	
	public List<T> getRegistry() {
		return registry;
	}
}
