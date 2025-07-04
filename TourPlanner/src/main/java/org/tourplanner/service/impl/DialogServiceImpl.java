package org.tourplanner.service.impl;

import jakarta.annotation.PostConstruct;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.springframework.stereotype.Service;
import org.tourplanner.TourPlannerApplication;
import org.tourplanner.config.ReportProperties;
import org.tourplanner.service.DialogService;
import javafx.scene.control.Alert;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class DialogServiceImpl implements DialogService {

    private final ReportProperties props;
    private Path lastDirectory;
    private String lastFileName;

    public DialogServiceImpl(ReportProperties props) {
        this.props = props;
    }

    @PostConstruct
    void initDefaults() {
        lastDirectory = Paths.get(props.getDefaultDir());
        lastFileName = props.getDefaultFile();
    }

    @Override
    public Path showFileSaveDialog(String title, String extDesc, String ext, String suggestedName, Window owner) {
        FileChooser chooser = buildChooser(title, extDesc, ext, suggestedName);
        File file = chooser.showSaveDialog(owner);
        return handleResult(file);
    }

    @Override
    public Path showFileOpenDialog(String title, String extDesc, String ext, Window owner) {
        FileChooser chooser = buildChooser(title, extDesc, ext, null);
        File file = chooser.showOpenDialog(owner);
        return handleResult(file);
    }

    @Override
    public void showFile(Path path) throws IOException {

        if(path == null || !Files.exists(path)) return;

        if(TourPlannerApplication.HOST_SERVICES != null) {
            TourPlannerApplication.HOST_SERVICES.showDocument(path.toUri().toString());
            return;
        }

        // fallback: java.awt.Desktop
        if(Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(path.toFile());
        }
    }

    @Override
    public void showMessageBox(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
    }

    private FileChooser buildChooser(String title, String extDesc, String ext, String suggestedName) {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle(title);
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extDesc, ext));
        if(Files.isDirectory(lastDirectory)) {
            filechooser.setInitialDirectory(lastDirectory.toFile());
        }
        filechooser.setInitialFileName(suggestedName != null ? suggestedName : lastFileName);
        return filechooser;
    }

    private Path handleResult(File file) {
        if(file == null) return null;

        lastDirectory = file.getParentFile().toPath();
        lastFileName = file.getName();
        return file.toPath();
    }
}