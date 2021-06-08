package io.github.palexdev.VirtualFlowAttempt.virtualflow;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VirtualFlowContainer<T, C extends Cell> extends Region {
    private final VirtualFlow<T, C> virtualFlow;
    private final ListProperty<T> items = new SimpleListProperty<>();
    private final ListProperty<C> cells = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Function<T, C>> globalCellFactory = new SimpleObjectProperty<>();

    public VirtualFlowContainer(VirtualFlow<T, C> virtualFlow, ObservableList<T> items, Function<T, C> globalCellFactory) {
        this.virtualFlow = virtualFlow;
        setGlobalCellFactory(globalCellFactory);
        setItems(items);

        initialize();
    }

    private void initialize() {
        setStyle("-fx-border-color: gold");
        buildClip();

        items.addListener((InvalidationListener) invalidated -> handleCells());

        globalCellFactory.addListener(invalidated -> {
            getChildren().clear();
            handleCells();
        });

        handleCells();
    }

    private void buildClip() {
        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(virtualFlow.widthProperty());
        rectangle.heightProperty().bind(virtualFlow.heightProperty());
        rectangle.layoutYProperty().bind(layoutYProperty().multiply(-1));
        setClip(rectangle);
    }

    protected void handleCells() {
        buildCells();
        List<Node> nodes = cells.stream()
                .map(Cell::getNode)
                .collect(Collectors.toList());
        getChildren().setAll(nodes);
    }

    protected void buildCells() {
        List<C> tmp = items.stream()
                .map(item -> getGlobalCellFactory().apply(item))
                .collect(Collectors.toList());
        cells.setAll(tmp);
    }

    public ObservableList<T> getItems() {
        return items.get();
    }

    public ListProperty<T> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    public Function<T, C> getGlobalCellFactory() {
        return globalCellFactory.get();
    }

    public ObjectProperty<Function<T, C>> globalCellFactoryProperty() {
        return globalCellFactory;
    }

    public void setGlobalCellFactory(Function<T, C> globalCellFactory) {
        this.globalCellFactory.set(globalCellFactory);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double increase = 0;
        double x = 0;
        double y = 0;
        for (C cell : cells) {
            Node node = cell.getNode();
            double height = node.prefHeight(-1) + increase;
            node.resizeRelocate(x, y, getWidth(), height);
            y += height;
            //increase += 5; TODO variable height testing
        }
    }
}
