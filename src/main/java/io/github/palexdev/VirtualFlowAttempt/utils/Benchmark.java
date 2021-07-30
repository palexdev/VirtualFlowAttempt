package io.github.palexdev.VirtualFlowAttempt.utils;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Comparator;

public class Benchmark {
    private String fileName = "Benchmark.log";
    private PrintMode printMode = PrintMode.FILE;
    private boolean printAfterBenchmark = true;
    private boolean debugOnMaxCapacity = true;
    private String extraDebugString = "";
    private long maxResults = 100;
    private Mode mode = Mode.DEFAULT;
    private ObservableList<Result> results = FXCollections.observableArrayList();
    private ListChangeListener<Result> changeListener = changed -> {
        if (results.size() == maxResults) {
            if (debugOnMaxCapacity) {
                printWorstBestResults();
            }
            results = FXCollections.observableArrayList();
            initList();
        }
    };

    private Benchmark() {
        initList();
    }

    public static Benchmark instance() {
        return new Benchmark();
    }

    private void initList() {
        results.addListener(changeListener);
    }

    public Benchmark setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Benchmark setPrintMode(PrintMode printMode) {
        this.printMode = printMode;
        return this;
    }

    public Benchmark setPrintAfterBenchmark(boolean printAfterBenchmark) {
        this.printAfterBenchmark = printAfterBenchmark;
        return this;
    }

    public Benchmark setDebugOnMaxCapacity(boolean debugOnMaxCapacity) {
        this.debugOnMaxCapacity = debugOnMaxCapacity;
        return this;
    }

    public Benchmark setExtraDebugString(String extraDebugString) {
        this.extraDebugString = extraDebugString;
        return this;
    }

    public Benchmark setMaxResults(long maxResults) {
        results.clear();
        results.removeListener(changeListener);
        this.maxResults = maxResults;

        changeListener = changed -> {
            if (results.size() == maxResults) {
                if (debugOnMaxCapacity) {
                    printWorstBestResults();
                }
                results = FXCollections.observableArrayList();
                initList();
            }
        };
        results.addListener(changeListener);
        return this;
    }

    public Benchmark setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public void benchmarkMillis(Runnable runnable) {
        benchmarkMillis(runnable, extraDebugString);
    }

    public void benchmarkMillis(Runnable runnable, String extraString) {
        benchmarkMillis(runnable, extraString, Format.MILLIS);
    }

    public void benchmarkMillis(Runnable runnable, String extraString, Format format) {
        setExtraDebugString(extraString);

        long beforeMillis = System.currentTimeMillis();
        runnable.run();
        long afterMillis = System.currentTimeMillis();
        long elapsed = afterMillis - beforeMillis;
        Result result = new Result(elapsed, true).format(format);
        if (printAfterBenchmark) {
            System.out.println(extraDebugString + " Took: " + result.getDisplayString() + " to run");
        }
    }

    public void benchmarkNano(Runnable runnable) {
        benchmarkNano(runnable, extraDebugString);
    }

    public void benchmarkNano(Runnable runnable, String extraString) {
        benchmarkNano(runnable, extraString, Format.NANO);
    }

    public void benchmarkNano(Runnable runnable, String extraString, Format format) {
        long beforeNano = System.nanoTime();
        runnable.run();
        long afterNano = System.nanoTime();
        long elapsed = afterNano - beforeNano;
        Result result = new Result(elapsed, false).format(format);
        if (printAfterBenchmark) {
            System.out.println(extraDebugString + " Took: " + result.getDisplayString() + " to run");
        }
    }

    private void printWorstBestResults() {
        String best = results.stream().filter(r -> r.result > 0.000009).min(Comparator.comparingDouble(o -> o.result)).map(Result::getDisplayString).orElse("Not Found!");
        String worst = results.stream().max(Comparator.comparingDouble(o -> o.result)).map(Result::getDisplayString).orElse("Not Found!");
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------------------------------------------------------------\n");
        if (!extraDebugString.trim().isEmpty()) {
            sb.append(extraDebugString).append("\n");
        }
        sb.append("Worst Result: \t").append(worst).append("\n");
        sb.append("Best Result: \t").append(best).append("\n");
        sb.append("----------------------------------------------------------------------------------------------------\n");
        print(sb.toString());
    }

    private void print(String s) {
        if (printMode == PrintMode.CONSOLE) {
            System.out.println(s);
        } else {
            LogSystem.debug(fileName, s, StandardOpenOption.APPEND);
        }
    }

    private class Result {
        private double result;
        private final boolean isMillis;
        private String unit = "n";

        private Result(long result, boolean isMillis) {
            this.result = result;
            this.isMillis = isMillis;
        }

        public Result format(Format format) {
            switch (format) {
                case MILLIS -> formatToMillis();
                case SECONDS -> formatToSeconds();
            }

            if (mode == Mode.STORE && results.size() < maxResults) {
                results.add(this);
            }

            return this;
        }

        private void formatToMillis() {
            unit = "ms";
            if (!isMillis) {
                result *= Math.pow(10, -6);
            }
        }

        private void formatToSeconds() {
            unit = "s";
            if (!isMillis) {
                formatToMillis();
            }
            result *= Math.pow(10, -3);
        }

        public String getDisplayString() {
            return new DecimalFormat("0.00000").format(result) + unit;
        }
    }

    public enum Format {
        NANO, MILLIS, SECONDS
    }

    public enum Mode {
        DEFAULT, STORE
    }

    public enum PrintMode {
        CONSOLE, FILE
    }
}
