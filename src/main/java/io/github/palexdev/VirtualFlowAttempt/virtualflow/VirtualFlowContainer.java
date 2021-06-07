package io.github.palexdev.VirtualFlowAttempt.virtualflow;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.stream.Collectors;

public class VirtualFlowContainer<T extends Cell> extends Region {
    private final ListProperty<T> cells = new SimpleListProperty<>(FXCollections.observableArrayList());

    public VirtualFlowContainer(List<T> cells) {
        initialize();
        this.cells.setAll(cells);
    }

    private void initialize() {
        setStyle("-fx-border-color: gold");

        cells.addListener((InvalidationListener) invalidated -> {
            List<Node> nodes = cells.stream().map(Cell::getNode).collect(Collectors.toList());
            getChildren().setAll(nodes);
        });
    }

    public ListProperty<T> getCells() {
        return cells;
    }

    public void setCells(List<T> cells) {
        this.cells.setAll(cells);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double increase = 0;
        double x = 0;
        double y = 0;
        for (Cell cell : getCells()) {
            Node node = cell.getNode();
            double height = node.prefHeight(-1) + increase;
            node.resizeRelocate(x, y, getWidth(), height);
            y += height;
            //increase += 5; TODO variable height testing
        }
    }
}
