package org.shadowrs.osbot.fxtest;

import lombok.Getter;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.shadowrs.osbot.fxutil.UI;

import java.awt.*;

@ScriptManifest(author = "Shadowrs", info = "Java FX Load test", name = "JavaFX Load test",
        version = 1.00, logo = "")
public class FxLoadTest extends Script {

    public final UI         ui = new UI("https://www.dropbox.com/s/z7pash8a31l9hyf/shadowrs.reagro.testmain.fxml?dl=1",
            "/shadowrs.reagro.testmain.fxml",
            MainFxTestController.class, o -> FxLoadTest.INSTANCE.println(o));
    @Getter
    static       FxLoadTest INSTANCE;

    @Override
    public int onLoop() {
        try {
            process();
        } catch (Exception e) {
            e.printStackTrace();
            log(e);
        }
        return 1_000;
    }

    private void process() throws Exception {
        if (!ui.open() && !ui.confirmed) {
            println("starting UI");
            ui.runFX(INSTANCE);
            return;
        }
        if (!ui.confirmed) {
            println("awaiting UI closure");
            return;
        }
        log("loop");
    }

    private void println(Object s) {
        System.out.println(s);
        log(s);
    }

    @Override
    public void onPaint(Graphics2D iIIiIiiiiiIi) {
        super.onPaint(iIIiIiiiiiIi);
    }

    @Override
    public void onMessage(Message iIIiIiiiiiIi) throws InterruptedException {
        super.onMessage(iIIiIiiiiiIi);
    }

    @Override
    public void onExit() throws InterruptedException {
        super.onExit();
    }

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        INSTANCE = this;
    }
}
