package io.github.palexdev.VirtualFlowAttempt;

import io.github.palexdev.VirtualFlowAttempt.virtualflow.SimpleCell;
import io.github.palexdev.VirtualFlowAttempt.virtualflow.VirtualFlow;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.util.stream.IntStream;

public class VirtualFlowAttempt extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane stackPane = new StackPane();

        ObservableList<String> cells = FXCollections.observableArrayList();
        IntStream.range(1, 16).mapToObj(i -> "String " + i).forEach(cells::add);

        VirtualFlow<String, SimpleCell<String>> virtualFlow = new VirtualFlow<>(cells, SimpleCell::new);
        virtualFlow.setPrefSize(300, 150);
        virtualFlow.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        virtualFlow.setEffect(MFXDepthManager.shadowOf(DepthLevel.LEVEL1));
        virtualFlow.setSpeed(10, 50);

        stackPane.getChildren().add(virtualFlow);
        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setTitle("VirtualFlow Attempt");
        primaryStage.setScene(scene);
        primaryStage.show();

        ScenicView.show(scene);
    }
}
