package com.vanaddr;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TimeElapsed implements ActionListener {
    private static final Timer timer = new Timer(1_000, new TimeElapsed());

    static void startTimer(JLabel label) {
        TimeElapsed.label = label;
        start = System.nanoTime();
        timer.start();
    }

    static void stopTimer() {
        timer.stop();
    }

    private static JLabel label;
    private static long start;

    private TimeElapsed() {
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        long elapsed = System.nanoTime() - start;
        label.setText(formatTime(elapsed / 1_000_000_000L));
    }

    private static String formatTime(long time) {
        long seconds = time % 60L;
        long minutes = (time / 60L) % 60L;
        long hours = (time / (60L * 60L)) % 24L;
        long days = time / (60L * 60L * 24L);
        String text = "";
        if (days != 0L) text += days + "d ";
        if (hours != 0L) text += hours + "h ";
        if (minutes != 0L) text += minutes + "m ";
        return text + seconds + 's';
    }
}
