package io.github.palexdev.VirtualFlowAttempt.virtualflow;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;

import java.util.function.Function;

public class VirtualFlow<T, C extends Cell> extends Region {
    private final ScrollBar scrollBar;

    private final VirtualFlowContainer<T, C> container;
    private final ListProperty<T> items = new SimpleListProperty<>();
    private final ObjectProperty<Function<T, C>> cellFactory = new SimpleObjectProperty<>();
    private final IntegerProperty overscan = new SimpleIntegerProperty(2);

    public VirtualFlow(ObservableList<T> items, Function<T, C> cellFactory) {
        scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);

        setItems(items);
        setCellFactory(cellFactory);
        container = new VirtualFlowContainer<>(this);

        getChildren().setAll(container, scrollBar);
        initialize();
    }

    private void initialize() {
        setStyle("-fx-background-color: white;\n" + "-fx-border-color: red");

        container.addEventFilter(ScrollEvent.ANY, event -> Event.fireEvent(this, event));

        scrollBar.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() - scrollBar.getWidth(),
                widthProperty()
        ));
        scrollBar.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> getHeight() - scrollBar.getHeight(),
                heightProperty()
        ));

        scrollBar.prefHeightProperty().bind(heightProperty());
        scrollBar.maxProperty().bind(Bindings.createDoubleBinding(
                () -> container.getHeight() - getHeight(),
                container.heightProperty(), heightProperty()
        ));

        scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> container.setLayoutY(-newValue.doubleValue()));
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

    public Function<T, C> getCellFactory() {
        return cellFactory.get();
    }

    public ObjectProperty<Function<T, C>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setCellFactory(Function<T, C> cellFactory) {
        this.cellFactory.set(cellFactory);
    }

    public int getOverscan() {
        return overscan.get();
    }

    public IntegerProperty overscanProperty() {
        return overscan;
    }

    public void setOverscan(int overscan) {
        this.overscan.set(overscan);
    }

    public void setSpeed(double unit, double block) {
        scrollBar.setUnitIncrement(unit);
        scrollBar.setBlockIncrement(block);
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return super.buildEventDispatchChain(tail)
                .append(scrollBar.getEventDispatcher());
    }
}
