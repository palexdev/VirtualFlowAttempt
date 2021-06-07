package io.github.palexdev.VirtualFlowAttempt.virtualflow;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;

import java.util.List;

public class VirtualFlow<T extends Cell> extends Region {
    private final ScrollBar scrollBar;
    private final VirtualFlowContainer<T> container;

    public VirtualFlow(List<T> cells) {
        scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);

        container = new VirtualFlowContainer<>(cells);

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

        scrollBar.valueProperty().addListener((ov, old_val, new_val) -> container.setLayoutY(-new_val.doubleValue()));
    }

    public ListProperty<T> getCells() {
        return container.getCells();
    }

    public void setCells(List<T> cells) {
        container.setCells(cells);
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
