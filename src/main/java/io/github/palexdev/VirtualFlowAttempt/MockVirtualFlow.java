package io.github.palexdev.VirtualFlowAttempt;

import io.github.palexdev.VirtualFlowAttempt.log.LogSystem;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXFlowlessListView;
import io.github.palexdev.materialfx.utils.NumberUtils;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockVirtualFlow extends Application {
    private final ObservableList<Weight> weights = FXCollections.observableArrayList();
    private final double viewportHeight = 150;
    private final double totalHeight = 300;
    private final double scrollUnit = 10;
    private int overscan = 2;

    private final ObjectProperty<NumberRange<Double>> containerBounds = new SimpleObjectProperty<>(NumberRange.of(0.0, totalHeight));
    private final ObjectProperty<NumberRange<Integer>> visibleIndexes = new SimpleObjectProperty<>();
    private final OverscanManger overscanManger = new OverscanManger();
    private HBox box;
    private MFXFlowlessListView<Weight> listView;
    private Rectangle rectangle;

    @Override
    public void start(Stage primaryStage) {
        initialize();

        Scene scene = new Scene(box, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void initialize() {
        box = new HBox(80);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(60, 0, 0, 0));

        listView = new MFXFlowlessListView<>();

        rectangle = new Rectangle();
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.GOLD);
        rectangle.widthProperty().bind(listView.widthProperty());
        rectangle.setHeight(totalHeight);

        box.getChildren().addAll(listView, rectangle);

        IntStream.range(1, 11).forEach(i -> weights.add(new Weight("Weight " + i, 30)));
        initFlow();

        box.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();
            if (keyCode == KeyCode.UP) {
                double min = getContainerBounds().getMin() - scrollUnit;
                if (min >= (viewportHeight - totalHeight)) {
                    NumberRange<Double> newRange = NumberRange.of(min, getContainerBounds().getMax() - scrollUnit);
                    setContainerBounds(newRange);
                }
            } else if (keyCode == KeyCode.DOWN) {
                double min = getContainerBounds().getMin() + scrollUnit;
                if (min <= 0) {
                    NumberRange<Double> newRange = NumberRange.of(min, getContainerBounds().getMax() + scrollUnit);
                    setContainerBounds(newRange);
                }
            }
        });

        setFlowListeners();
    }

    private void setFlowListeners() {
        containerBounds.addListener((observable, oldValue, newValue) -> updateIndexes());
        visibleIndexes.addListener(invalidated -> {
            overscanManger.updateOverscanIndexes();
            //markOverscan(newValue.getMin(), newValue.getMax());
            //debugRanges(oldValue, newValue);
        });
        overscanManger.overscanIndexesProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null) {
                return;
            }

            updateFlow(newValue);
            debugRanges(oldValue, newValue);
        });
    }

    private void initFlow() {
        int max = 0;
        double height = 0;

        while (true) {
            Weight weight = weights.get(max);
            height += weight.value;
            if (height < viewportHeight) {
                max++;
            } else {
                break;
            }
        }

        int start = 0;
        int end = NumberUtils.clamp(max + overscan, 0, weights.size());
        setVisibleIndexes(NumberRange.of(start, end - overscan));
        overscanManger.setOverscanIndexes(NumberRange.of(start, end));
        listView.getItems().addAll(weights.subList(start, end + 1));
    }

    private void updateIndexes() {
        rectangle.setTranslateY(getContainerBounds().getMin());

        int newMin = firstVisible();
        int newMax = lastVisible(newMin);
        setVisibleIndexes(NumberRange.of(newMin, newMax));

        debugVisibleCells();
    }

    private void updateFlow(NumberRange<Integer> newValue) {
        listView.setItems(
                IntStream.rangeClosed(newValue.getMin(), newValue.getMax())
                        .mapToObj(weights::get)
                        .collect(Collectors.toList())
        );
    }

    private int firstVisible() {
        int index = 0;
        for (Weight weight : weights) {
            if (intersects(weight)) {
                break;
            }
            index++;
        }
        return index;
    }

    public int lastVisible() {
        return lastVisible(firstVisible());
    }

    private int lastVisible(int startIndex) {
        int index = startIndex;
        for (int i = index; i < weights.size(); i++) {
            Weight weight = weights.get(i);
            if (!intersects(weight)) {
                break;
            }
            index++;
        }
        return index - 1;
    }

    private boolean isOverscanVisible() {
        int minIndex = getVisibleIndexes().getMin() - overscan;
        return minIndex > 0;
    }

    private boolean intersects(Weight weight) {
        double pos = (weights.indexOf(weight) + 1) * weight.getValue();
        double viewportPos = pos + getContainerBounds().getMin();
        return viewportPos > 0 && viewportPos < viewportHeight + weight.getValue();
    }

    public void scrollBy(int mul) {
        double val = NumberUtils.clamp(mul * scrollUnit, -viewportHeight, viewportHeight);
        setContainerBounds(NumberRange.of(getContainerBounds().getMin() + val, getContainerBounds().getMax() + val));
    }

    private void debugVisibleCells() {
        List<String> logs = new ArrayList<>();
        IntStream.range(0, weights.size()).forEach(i -> {
            if (NumberRange.inRangeOf(i, getVisibleIndexes())) {
                logs.add("W" + (i + 1) + " is visible\n");
            } else {
                logs.add("W" + (i + 1) + " is not visible\n");
            }
        });
        LogSystem.debug("MockVisible.log", logs);
    }

    private void debugOverscan() {
        List<String> logs = new ArrayList<>();
        IntStream.range(0, weights.size()).forEach(i -> {
            if (weights.get(i).isOverscan()) {
                logs.add("W" + (i + 1) + " is overscan\n");
            }
        });
        LogSystem.debug("MockOverscan.log", logs);
    }

    private <T extends Number> void debugRanges(NumberRange<T> oldRange, NumberRange<T> newRange) {
        StringBuilder sb = new StringBuilder();
        sb.append("Old: [")
                .append(oldRange.getMin())
                .append(", ")
                .append(oldRange.getMax())
                .append("]  ")
                .append("New: [")
                .append(newRange.getMin())
                .append(", ")
                .append(newRange.getMax())
                .append("]  ");
        System.out.println(sb);
    }

    // TODO for tests only, to remove
    public NumberRange<Integer> getCompleteVisibleIndexes() {
        return overscanManger.getOverscanIndexes();
    }

    private class OverscanManger {
        private final ObjectProperty<NumberRange<Integer>> overscanIndexes = new SimpleObjectProperty<>();
        private boolean upperOverscanPresent = false;
        private boolean lowerOverscanPresent = false;

        public void updateOverscanIndexes() {
            int min = getVisibleIndexes().getMin() - overscan;
            int max = getVisibleIndexes().getMax() + overscan;

            upperOverscanPresent = min >= 0;
            lowerOverscanPresent = max < weights.size();
            
            int firstVisible = firstVisible();
            if (lastVisible(firstVisible) == getOverscanIndexes().getMax() ) {
                setOverscanIndexes(NumberRange.of(min, max));
            }
        }

        public NumberRange<Integer> getOverscanIndexes() {
            return overscanIndexes.get();
        }

        public ObjectProperty<NumberRange<Integer>> overscanIndexesProperty() {
            return overscanIndexes;
        }

        public void setOverscanIndexes(NumberRange<Integer> overscanIndexes) {
            this.overscanIndexes.set(overscanIndexes);
        }
    }

    public class Weight {
        private final String name;
        private final double value;
        private final BooleanProperty overscan = new SimpleBooleanProperty(false);

        public Weight(String name, double value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public double getValue() {
            return value;
        }

        public boolean isOverscan() {
            return overscan.get();
        }

        public BooleanProperty overscanProperty() {
            return overscan;
        }

        public void setOverscan(boolean overscan) {
            this.overscan.set(overscan);
        }

        @Override
        public String toString() {
            return "Weight " + (weights.indexOf(this) + 1);
        }
    }

    public void setOverscan(int overscan) {
        this.overscan = overscan;
    }

    public NumberRange<Double> getContainerBounds() {
        return containerBounds.get();
    }

    public void setContainerBounds(NumberRange<Double> containerBounds) {
        this.containerBounds.set(containerBounds);
    }

    public NumberRange<Integer> getVisibleIndexes() {
        return visibleIndexes.get();
    }

    public void setVisibleIndexes(NumberRange<Integer> visibleIndexes) {
        this.visibleIndexes.set(visibleIndexes);
    }
}
