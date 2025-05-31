package mortalcombatbversion;

import mortalcombatbversion.view.JFrames;
import mortalcombatbversion.controller.Controller;

public class Mylab5 {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrames(new Controller()).setVisible(true);
            }
        });
    }
}
