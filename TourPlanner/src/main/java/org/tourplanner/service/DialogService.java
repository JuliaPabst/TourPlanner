package org.tourplanner.service;

import java.io.IOException;
import java.nio.file.Path;
import javafx.stage.Window;

public interface DialogService {

    Path showFileSaveDialog(String title,
                            String extDesc,
                            String extPattern,
                            String suggestedFileName,
                            Window owner);

    Path showFileOpenDialog(String title,
                            String extDesc,
                            String extPattern,
                            Window owner);

    void showFile(Path file) throws IOException;
}