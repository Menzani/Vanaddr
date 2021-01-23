package com.vanaddr;

import javax.swing.*;

class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(MainWindow::new);
        } else {
            String initialCharacters = null;
            String finalCharacters = null;
            for (String arg : args) {
                if (arg.startsWith("initial=")) {
                    initialCharacters = arg.substring(8);
                } else if (arg.startsWith("final=")) {
                    finalCharacters = arg.substring(6);
                }
            }
            if (initialCharacters == null && finalCharacters == null) {
                System.out.println("You have specified neither the initial characters nor the final characters.");
                return;
            }
            if (initialCharacters != null) {
                System.out.println("Match initial characters: '" + initialCharacters + '\'');
            }
            if (finalCharacters != null) {
                System.out.println("Match final characters: '" + finalCharacters + '\'');
            }
            Cruncher.crunch(initialCharacters != null, finalCharacters != null, initialCharacters, finalCharacters, null);
        }
    }
}
