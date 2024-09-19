public interface ActionHandler {
    void execute(P2PRelationshipVisualizationRequest request);
}

public class OwnershipRelationshipHandler implements ActionHandler {
    @Override
    public void execute(P2PRelationshipVisualizationRequest request) {
        // Implement ownership relationship logic here
    }
}

public class NonOwnershipRelationshipHandler implements ActionHandler {
    @Override
    public void execute(P2PRelationshipVisualizationRequest request) {
        // Implement non-ownership relationship logic here
    }
}

public class ActionRegistry {
    private final Map<String, ActionHandler> actionHandlers = new HashMap<>();

    public ActionRegistry() {
        actionHandlers.put("RETURN_OWNERSHIP_RELATIONSHIP", new OwnershipRelationshipHandler());
        actionHandlers.put("RETURN_NON_OWNERSHIP_RELATIONSHIP", new NonOwnershipRelationshipHandler());
        // Add more actions as needed
    }

    public void executeAction(String actionName, P2PRelationshipVisualizationRequest request) {
        ActionHandler handler = actionHandlers.get(actionName);
        if (handler != null) {
            handler.execute(request);
        } else {
            throw new IllegalArgumentException("Unknown action: " + actionName);
        }
    }
}
