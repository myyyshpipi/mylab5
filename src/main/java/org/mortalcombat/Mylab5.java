package org.mortalcombat;

import org.mortalcombat.view.JFrames;
import org.mortalcombat.controller.Controller;

public class Mylab5 {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrames(new Controller()).setVisible(true);
            }
        });
    }
}
