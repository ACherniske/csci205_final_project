module csci205_final_project  {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.slf4j;

    exports org.five_nights_at_dana to javafx.graphics;
    opens org.five_nights_at_dana to javafx.fxml;
    opens org.five_nights_at_dana.Controllers to javafx.fxml;
}