package io.github.palexdev.VirtualFlowAttempt.simulation;

import io.github.palexdev.materialfx.utils.AnimationUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AnimatedMockVirtualFlowApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        AnimatedMockVirtualFlow virtualFlow = new AnimatedMockVirtualFlow();
        stackPane.getChildren().add(virtualFlow);

        Scene scene = new Scene(stackPane, 1440, 900);
        primaryStage.setScene(scene);
        primaryStage.setOnShown(event ->
                AnimationUtils.PauseBuilder.build()
                .setDuration(1000)
                .setOnFinished(end -> virtualFlow.animateSmoothly())
                .getAnimation()
                .play());
        primaryStage.show();
    }
}
