package io.github.palexdev.VirtualFlowAttempt.simulation;

import io.github.palexdev.VirtualFlowAttempt.utils.Benchmark;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.NumberUtils;
import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnimatedMockVirtualFlow extends Region {
    private final List<Cell> cells = new ArrayList<>();
    private final int numCells = 20;
    private final double fixedCellHeight = 30;
    private final double totalHeight = numCells * fixedCellHeight;
    private final double viewportHeight = 150;
    private final int overscan = 2;
    private final double scrollUnit = 10;

    private final VBox cellsContainer;
    private final ObjectProperty<NumberRange<Double>> containerBounds = new SimpleObjectProperty<>(NumberRange.of(0.0, totalHeight));
    private final ObjectProperty<NumberRange<Integer>> visibleIndexes = new SimpleObjectProperty<>(NumberRange.of(0, 4));
    private final ObjectProperty<NumberRange<Integer>> upperOverscan = new SimpleObjectProperty<>();
    private final ObjectProperty<NumberRange<Integer>> lowerOverscan = new SimpleObjectProperty<>();
    private NumberRange<Integer> addedRange;

    private final InvalidationListener overscanChanged = invalidated -> {
        updateContainerChildrenV1();
        markOverscan();
    };

    private final StringProperty scrollDirection = new SimpleStringProperty();
    private final IntegerProperty numOfScrolls = new SimpleIntegerProperty(0);
    private final int debugScroll = 8;
    private final boolean debug = false;

    private boolean isReset = false;

    private final Benchmark benchmark = Benchmark.instance().setMaxResults(300);

    public AnimatedMockVirtualFlow() {
        setPrefSize(300, viewportHeight);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        setStyle("-fx-border-color: red");
        IntStream.range(0, numCells).forEach(i -> cells.add(new Cell("Cell " + (i + 1), 30)));

        cellsContainer = new VBox();
        cellsContainer.setStyle("-fx-border-color: gold");
        cellsContainer.setAlignment(Pos.TOP_CENTER);
        cellsContainer.prefWidthProperty().bind(widthProperty());
        cellsContainer.getChildren().setAll(cells.stream().map(Cell::getNode).collect(Collectors.toList()));

        getChildren().add(cellsContainer);

        initFlow();
        addListeners();
    }

    private void initFlow() {
        int max = 0;
        double height = 0;

        while (true) {
            Cell cell = cells.get(max);
            height += cell.height;
            if (height < viewportHeight) {
                max++;
            } else {
                break;
            }
        }

        int start = 0;
        int end = NumberUtils.clamp(max + overscan, 0, cells.size());
        setVisibleIndexes(NumberRange.of(start, end - overscan));
        updateOverscanIndexes(getVisibleIndexes());
        updateContainerChildrenV1();
        markOverscan();
    }

    private void addListeners() {
        sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    KeyCode keyCode = event.getCode();
                    if (keyCode == KeyCode.UP) {
                        scrollDirection.set("DOWN");
                        double min = getContainerBounds().getMin() - scrollUnit;
                        if (min >= (viewportHeight - totalHeight)) {
                            NumberRange<Double> newRange = NumberRange.of(min, getContainerBounds().getMax() - scrollUnit);
                            setContainerBounds(newRange);
                            increaseScroll();
                        }
                    } else if (keyCode == KeyCode.DOWN) {
                        scrollDirection.set("UP");
                        double min = getContainerBounds().getMin() + scrollUnit;
                        if (min <= 0) {
                            NumberRange<Double> newRange = NumberRange.of(min, getContainerBounds().getMax() + scrollUnit);
                            setContainerBounds(newRange);
                            decreaseScroll();
                        }
                    }
                });
            }
        });

        cellsContainer.layoutYProperty().bind(Bindings.createDoubleBinding(
                () -> getContainerBounds().getMin(),
                containerBounds
        ));
        cellsContainer.paddingProperty().bind(Bindings.createObjectBinding(
                () -> new Insets(getUpperOverscan().getMin() * 30, 0, 0, 0),
                upperOverscan
        ));
        containerBounds.addListener((observable, oldValue, newValue) -> updateVisibleIndexes());
        visibleIndexes.addListener((observable, oldValue, newValue) -> updateOverscanIndexes(newValue));
        upperOverscan.addListener(overscanChanged);
        lowerOverscan.addListener(overscanChanged);
        numOfScrolls.addListener((observable, oldValue, newValue) -> {
            if (debug) {
                System.out.println("Num Of Scrolls: " + newValue.intValue());
            }
        });
        scrollDirection.addListener((observable, oldValue, newValue) -> System.out.println(newValue));

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> System.out.println("Range: [" + getContainerBounds().getMin() + ", " + getContainerBounds().getMax() + "]"));
    }

    public void animateSlowly() {
        AnimationUtils.TimelineBuilder.build()
                .add(KeyFrames.of(750, event -> {
                    double min = getContainerBounds().getMin() - scrollUnit;
                    int mul;

                    if (numOfScrolls.get() == 0) {
                        isReset = false;
                    }

                    if (min >= (viewportHeight - totalHeight) && !isReset) {
                        mul = -1;
                        scrollDirection.set("DOWN");
                        increaseScroll();
                    } else {
                        isReset = true;
                        mul = 1;
                        scrollDirection.set("UP");
                        decreaseScroll();
                    }

                    double offset = (mul * scrollUnit);
                    NumberRange<Double> newRange = NumberRange.of(getContainerBounds().getMin() + offset, getContainerBounds().getMax() + offset);
                    setContainerBounds(newRange);
                })).setCycleCount(Animation.INDEFINITE).getAnimation().play();
    }

    public void animateSmoothly() {
        AnimationUtils.TimelineBuilder.build()
                .add(KeyFrames.of(10, event -> {
                    if (getContainerBounds().getMax() == 150) {
                        isReset = true;
                    } else if (getContainerBounds().getMax() == 600) {
                        isReset = false;
                    }

                    int offset;
                    if (isReset) {
                        offset = 1;
                        scrollDirection.set("UP");
                    } else {
                        offset = -1;
                        scrollDirection.set("DOWN");
                    }
                    NumberRange<Double> newRange = NumberRange.of(getContainerBounds().getMin() + offset, getContainerBounds().getMax() + offset);
                    setContainerBounds(newRange);
                })).setCycleCount(Animation.INDEFINITE).getAnimation().play();
    }

    private void updateVisibleIndexes() {
        if (numOfScrolls.get() == debugScroll && debug) {
            System.out.println("Debug Update Visible Indexes");
        }

        int newMin = firstVisible();
        int newMax = lastVisible(newMin);
        setVisibleIndexes(NumberRange.of(newMin, newMax));
    }

    private void updateOverscanIndexes(NumberRange<Integer> visibleRange) {
        if (numOfScrolls.get() == debugScroll && debug) {
            System.out.println("Debug Update Overscan Indexes");
        }

        setUpperOverscan(computeUpperRange(visibleRange));
        setLowerOverscan(computeLowerRange(visibleRange));
    }

    public void updateContainerChildrenV1() {
        benchmark.setFileName("ChildrenUpdateV1.log")
                .setExtraDebugString("UpdateContainerChildrenV1")
                .setMode(Benchmark.Mode.STORE)
                .setPrintAfterBenchmark(false)
                .benchmarkNano(() -> {
                            if (addedRange == null) {
                                int min = getUpperOverscan().getMin();
                                int max = getLowerOverscan().getMax();
                                cellsContainer.getChildren().setAll(cells.subList(min, max + 1).stream().map(Cell::getNode).collect(Collectors.toList()));
                            } else {
                                List<Integer> addedIndexes = expandRange(addedRange);
                                List<Integer> changedIndexes = expandRange(NumberRange.of(getUpperOverscan().getMin(), getLowerOverscan().getMax()));

                                List<Node> toAdd = changedIndexes.stream()
                                        .filter(i -> !addedIndexes.contains(i))
                                        .map(i -> cells.get(i).getNode())
                                        .collect(Collectors.toList());
                                List<Node> toRemove = addedIndexes.stream()
                                        .filter(i -> !changedIndexes.contains(i))
                                        .map(i -> cells.get(i).getNode())
                                        .collect(Collectors.toList());
                                cellsContainer.getChildren().removeAll(toRemove);
                                int addIndex = (scrollDirection.get().equalsIgnoreCase("up")) ? 0 : cellsContainer.getChildren().size();
                                cellsContainer.getChildren().addAll(addIndex, toAdd);
                            }

                            addedRange = NumberRange.of(getUpperOverscan().getMin(), getLowerOverscan().getMax());
                        }, "V1", Benchmark.Format.SECONDS
                );
    }

    public void updateContainerChildrenV2() {
        benchmark.setFileName("ChildrenUpdateV2.log")
                .setExtraDebugString("UpdateContainerChildrenV2")
                .setMode(Benchmark.Mode.STORE)
                .setPrintAfterBenchmark(false)
                .benchmarkNano(() -> {
                            if (numOfScrolls.get() == debugScroll && debug) {
                                System.out.println("Debug Update Container Children V2");
                            }

                            int min = getUpperOverscan().getMin();
                            int max = getLowerOverscan().getMax();

                            if (numOfScrolls.get() == debugScroll && debug) {
                                System.out.println("Debug Update Container Children V2 - Debug SubList");
                                List<Cell> sub = cells.subList(min, max + 1);
                                System.out.println(Arrays.toString(sub.toArray()));
                            }

                            cellsContainer.getChildren().setAll(cells.subList(min, max + 1).stream().map(Cell::getNode).collect(Collectors.toList()));
                            addedRange = NumberRange.of(getUpperOverscan().getMin(), getLowerOverscan().getMax());
                        }, "V2", Benchmark.Format.SECONDS
                );
    }

    private NumberRange<Integer> computeUpperRange(NumberRange<Integer> range) {
        int min = NumberUtils.clamp(range.getMin() - overscan, 0, numCells - 1);
        int max = min;
        for (int i = 1; i < overscan; i++) {
            max += 1;
        }
        return NumberRange.of(min, max);
    }

    private NumberRange<Integer> computeLowerRange(NumberRange<Integer> range) {
        int max = NumberUtils.clamp(range.getMax() + overscan, 0, numCells - 1);
        int min = max;
        for (int i = 1; i < overscan; i++) {
            min -= 1;
        }
        return NumberRange.of(min, max);
    }

    private int firstVisible() {
        int index = 0;
        for (Cell cell : cells) {
            if (checkIntersects(cell)) {
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
        for (int i = index; i < cells.size(); i++) {
            Cell cell = cells.get(i);
            if (!checkIntersects(cell)) {
                break;
            }
            index++;
        }
        return index;
    }

    private boolean checkIntersects(Cell cell) {
        double cellPos = (cells.indexOf(cell) + 1) * cell.height;
        double absMin = Math.abs(getContainerBounds().getMin());
        double max = NumberUtils.clamp(absMin + viewportHeight, 0, totalHeight);
        return cellPos > absMin && cellPos < max;
    }

    private void markOverscan() {
        if (numOfScrolls.get() == debugScroll && debug) {
            System.out.println("Debug Mark Overscan");
        }

        cells.forEach(cell -> NodeUtils.setBackground(cell.getNode(), Color.TRANSPARENT));

        IntStream.rangeClosed(getUpperOverscan().getMin(), getUpperOverscan().getMax())
                .forEach(i -> NodeUtils.setBackground(cells.get(i).getNode(), Color.rgb(105, 239, 173, 0.5)));
        IntStream.rangeClosed(getLowerOverscan().getMin(), getLowerOverscan().getMax())
                .forEach(i -> NodeUtils.setBackground(cells.get(i).getNode(), Color.rgb(105, 239, 173, 0.5)));
    }

    public void scrollBy(int mul) {
        double val = NumberUtils.clamp(mul * scrollUnit, -viewportHeight, viewportHeight);
        setContainerBounds(NumberRange.of(getContainerBounds().getMin() + val, getContainerBounds().getMax() + val));
    }

    private List<Integer> expandRange(NumberRange<Integer> range) {
        return IntStream.rangeClosed(range.getMin(), range.getMax()).boxed().collect(Collectors.toList());
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

    public NumberRange<Integer> getUpperOverscan() {
        return upperOverscan.get();
    }

    public void setUpperOverscan(NumberRange<Integer> upperOverscan) {
        this.upperOverscan.set(upperOverscan);
    }

    public NumberRange<Integer> getLowerOverscan() {
        return lowerOverscan.get();
    }

    public void setLowerOverscan(NumberRange<Integer> lowerOverscan) {
        this.lowerOverscan.set(lowerOverscan);
    }

    private void increaseScroll() {
        numOfScrolls.set(numOfScrolls.get() + 1);
    }

    private void decreaseScroll() {
        numOfScrolls.set(numOfScrolls.get() - 1);
    }

    private static class Cell {
        private final Label textNode;
        private final String name;
        private final double height;

        private Cell(String name, double height) {
            textNode = new Label();
            textNode.setPadding(new Insets(0, 5, 0, 5));
            textNode.setPrefHeight(height);
            textNode.setMinHeight(USE_PREF_SIZE);
            textNode.setMaxHeight(USE_PREF_SIZE);
            textNode.setMaxWidth(Double.MAX_VALUE);
            this.name = name;
            this.height = height;
            textNode.setText(name);
        }

        public Label getNode() {
            return textNode;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
