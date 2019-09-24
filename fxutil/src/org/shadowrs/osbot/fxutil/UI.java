package org.shadowrs.osbot.fxutil;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import lombok.*;
import org.osbot.rs07.script.Script;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.String.format;

@RequiredArgsConstructor
public class UI {

    @Getter
    @NonNull
    final String downloadFromUrl, outputFile;
    @Getter
    @NonNull
    final Class <? extends OsbotController> controllerClass;

    @Getter
    @NonNull
    final Feedback feedback;

    public void runFX(Script s) {
        confirmed = false; // reset if directly invoked
        SwingUtilities.invokeLater(() -> {
            println("swing thread: "+Thread.currentThread().toString());
            createJFrame();
            Platform.setImplicitExit(false);
            Platform.runLater(() -> {
                println("plat thread: "+Thread.currentThread().toString());
                addJavaFX(jfxp, s);
            });
        });
    }

    public void println(String s) {
        if (feedback != null)
            feedback.println(s);
    }
    public void println(Object s) {
        if (feedback != null)
            feedback.println(s);
    }

    public JFrame   frame;
    public JFXPanel jfxp;

    private void createJFrame() {
        println("Creating JFrame for JavaFXPanel");
        if (frame != null) {
            println("frame exists, disposing of "+frame);
            frame.dispose(); // remove last
        }
        frame = new JFrame("Swing Frame for JavaFX");
        jfxp  = new JFXPanel();
        println("created frame "+frame+" with fxp "+jfxp);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(jfxp);
        frame.setSize(230, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                println("jframe closed 1 "+e);
                confirmed = true; // stops repeats
                if (controller != null) {
                    controller.onClose();
                }
                super.windowClosed(e); // required
            }
        });
        println("frame created: "+frame);
    }

    public OsbotController controller;

    private void addJavaFX(JFXPanel jfxp, Script s) {
        if (controllerClass == null) {
            println("missing controller class");
            return;
        }
        println("panel init");
        try {
            downloadFile(new URL(downloadFromUrl),
                    new File(s.getDirectoryData() + outputFile),
                    new ProgressListener(feedback));
        } catch (Exception e) {
            println(e);
            e.printStackTrace();
        }
        try {
            // TODO might have to load another way - see gateway io load
            URL  rsc         = new File(s.getDirectoryData() + outputFile).toURI().toURL();
            println("ui from "+rsc);
            //noinspection ConstantConditions
            if (rsc == null) {
                frame.dispose();
                println("disposing frame, java fX init fail");
                try {
                    println("hmm.. " + getClass().getProtectionDomain().getCodeSource().getLocation());
                } catch (Exception e) {
                    println(e != null ? e.toString() : "??");
                }
                return;
            }

            FXMLLoader loader = new FXMLLoader(rsc);
            controller = controllerClass.newInstance();
            loader.setController(controller);
            AnchorPane page = loader.load();

            Scene scene = new Scene(page);
            jfxp.setScene(scene);

            println("UI showing");
            frame.pack();
        } catch (Exception e) {
            println("Error loading ui.fxml!");
            println(e);
            e.printStackTrace();
        }
    }

    public boolean open() {
        return frame != null && frame.isVisible();
    }

    public boolean confirmed;

    public static void downloadFile(URL url, File destination, @NonNull ProgressListener listener) {
        try {
            URLConnection connection;
            final URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Wind0ws NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1");
            connection = con;

            int                 size            = connection.getContentLength();
            SizedInputStream    sizeInputStream = new SizedInputStream(connection.getInputStream(), size, listener);
            BufferedInputStream in              = new BufferedInputStream(sizeInputStream);
            FileOutputStream    fileOut         = new FileOutputStream(destination);
            double              before          = listener == null ? 0.0D : listener.getCurrentProgress();
            long                startTime       = System.currentTimeMillis();
            int                 totalRead       = 0;

            try {
                byte[] data = new byte[1024];

                int count;
                while((count = in.read(data, 0, 1024)) != -1) {
                    fileOut.write(data, 0, count);
                    totalRead += count;
                    if (listener != null) {
                        double progress = (double)totalRead / (double)size * 100.0D;
                        String rate = (long)totalRead / Math.max(1L, (System.currentTimeMillis() - startTime) / 1000L) + "/s";
                        listener.updateMessageAndProgress(format("[%s] Downloading... %02f%% (%s of %s bytes) %s from %s to %s", UI.class.getName(), progress, totalRead, size, rate, url.getPath(), destination.getAbsolutePath()), progress);
                    }
                }
            } finally {
                in.close();
                fileOut.close();
            }

            if (listener != null) {
                listener.onProgressUpdate(before);
            }

            listener.feedback.println("[WebUtil] Downloaded " + totalRead + " bytes from " + url + " -> " + destination.getAbsolutePath());
        } catch (Throwable var24) {
            var24.printStackTrace();
            listener.feedback.println(var24);
        }

    }
}
