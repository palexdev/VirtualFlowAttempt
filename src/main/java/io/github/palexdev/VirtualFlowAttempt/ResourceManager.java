package io.github.palexdev.VirtualFlowAttempt;

@SuppressWarnings("All")
public class ResourceManager {

    private ResourceManager() {}

    public static String loadResource(String name) {
        return ResourceManager.class.getResource(name).toExternalForm();
    }
}
