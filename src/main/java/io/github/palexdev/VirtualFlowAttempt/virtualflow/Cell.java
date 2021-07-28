package io.github.palexdev.VirtualFlowAttempt.virtualflow;

import javafx.geometry.Bounds;
import javafx.scene.Node;

public interface Cell {
    Node getNode();
    double getFixedHeight();

    default Bounds getBounds() {
        return getNode().getBoundsInParent();
    }
}
