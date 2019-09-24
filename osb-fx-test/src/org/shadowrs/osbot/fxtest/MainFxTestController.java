package org.shadowrs.osbot.fxtest;

import javafx.event.ActionEvent;
import org.shadowrs.osbot.fxutil.OsbotController;

public class MainFxTestController implements OsbotController {
    @Override
    public void onClose() {
        System.out.println("hi");
    }

    @Override
    public void onAction(ActionEvent e) {

    }
}
