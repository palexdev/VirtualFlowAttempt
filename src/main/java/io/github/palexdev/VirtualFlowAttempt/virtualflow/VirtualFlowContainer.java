package io.github.palexdev.VirtualFlowAttempt.virtualflow;

import io.github.palexdev.materialfx.utils.NumberUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.stream.Collectors;

public class VirtualFlowContainer<T, C extends Cell> extends Region {
    private final ListProperty<C> cells = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final VirtualFlow<T, C> virtualFlow;
    private final LayoutManager manager;

    public VirtualFlowContainer(VirtualFlow<T, C> virtualFlow) {
        this.virtualFlow = virtualFlow;
        manager = new LayoutManager();
        initialize();
    }

    private void initialize() {
        setStyle("-fx-border-color: gold");
        buildClip();

        addListeners();
    }

    private void addListeners() {
        virtualFlow.heightProperty().addListener((observable, oldValue, newValue) -> manager.init());
        layoutYProperty().addListener((observable, oldValue, newValue) -> manager.update());
    }

    private void buildClip() {
        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(virtualFlow.widthProperty());
        rectangle.heightProperty().bind(virtualFlow.heightProperty());
        rectangle.layoutYProperty().bind(layoutYProperty().multiply(-1));
        setClip(rectangle);
    }

    private class LayoutManager {
        private final ObjectProperty<IndexRange> cellsRange = new SimpleObjectProperty<>(new IndexRange(0, 0));
        private final ObjectProperty<Bounds> viewportBounds = new SimpleObjectProperty<>();
        private double lastPosition = 0;

        public LayoutManager() {
            viewportBounds.bind(Bindings.createObjectBinding(() -> {
                        Bounds viewportBounds = virtualFlow.getLayoutBounds();
                        Bounds viewportBoundsInScene = virtualFlow.localToScene(viewportBounds);
                        return sceneToLocal(viewportBoundsInScene);
                    }, layoutYProperty(), layoutXProperty())
            );
        }

        protected void init() {
            // Build all cells and compute max height
            cells.setAll(virtualFlow.getItems().stream()
                    .map(item -> virtualFlow.getCellFactory().apply(item))
                    .collect(Collectors.toList())
            );
            setPrefHeight(cells.stream().mapToDouble(C::getFixedHeight).sum());

            // Position first cells
            int start = 0;
            int end = findMaxIndex();
            int overscanEnd = NumberUtils.clamp(end + virtualFlow.getOverscan(), 0, cells.size());
            setCellsRange(new IndexRange(start, overscanEnd));
            System.out.println("Max is: " + end);
            addAndPosition(cells.subList(start, overscanEnd + 1));
        }

        protected void update() {
            C lastOverscan = cells.get(getCellsRange().getEnd());
            System.out.println("Last is: " + ((Label) lastOverscan.getNode()).getText());

            if (!(cells.indexOf(lastOverscan) == cells.size() - 1) && isCellPartiallyVisible(lastOverscan)) {
                System.out.println("Should add " + virtualFlow.getOverscan() + " cells");
                int start = NumberUtils.clamp(getCellsRange().getEnd() + 1, 0, cells.size());
                int end = NumberUtils.clamp(getCellsRange().getEnd() + virtualFlow.getOverscan(), 0, cells.size() - 1);
                setCellsRange(new IndexRange(getCellsRange().getStart(), end));
                addAndPosition(cells.subList(start, end + 1));
            }
        }

        private boolean isCellVisible(C cell) {
            return getViewportBounds().contains(cell.getBounds());
        }

        private boolean isCellPartiallyVisible(C cell) {
            return getViewportBounds().intersects(cell.getBounds());
        }

        private void addAndPosition(List<C> cells) {
            double y = lastPosition;
            for (C cell : cells) {
                Node node = cell.getNode();
                getChildren().add(node);
                node.resizeRelocate(0, y, node.prefWidth(-1), cell.getFixedHeight());
                y += cell.getFixedHeight();
                lastPosition = y;
            }
        }

        private int findMaxIndex() {
            double height = 0;
            int max = 0;

            while (true) {
                C cell = cells.get(max);
                height += cell.getFixedHeight();
                if (height <= virtualFlow.getHeight()) {
                    max++;
                } else {
                    break;
                }
            }
            return max;
        }

        public IndexRange getCellsRange() {
            return cellsRange.get();
        }

        public void setCellsRange(IndexRange cellsRange) {
            this.cellsRange.set(cellsRange);
        }

        public Bounds getViewportBounds() {
            return viewportBounds.get();
        }
    }
}