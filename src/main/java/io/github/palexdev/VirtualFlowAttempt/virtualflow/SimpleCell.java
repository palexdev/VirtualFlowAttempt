package io.github.palexdev.VirtualFlowAttempt.virtualflow;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class SimpleCell<T> implements Cell {
    private final Label label;

    public SimpleCell(T data) {
        label = new Label(data.toString());
        label.setPadding(new Insets(5));
        label.setMinWidth(100);
        //label.setStyle("-fx-border-color: " + ColorUtils.rgb(ColorUtils.getRandomColor()));
    }

    @Override
    public Node getNode() {
        return label;
    }
}
