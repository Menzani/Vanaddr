package com.vanaddr;

import javax.swing.*;
import java.awt.*;

class JTextFieldWithPlaceholder extends JTextField {
    private final String placeholder;

    JTextFieldWithPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (getText().isEmpty() && isEnabled()) {
            Graphics2D pen = (Graphics2D) graphics;
            pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pen.setColor(getDisabledTextColor());
            Insets insets = getInsets();
            pen.drawString(placeholder, insets.left, graphics.getFontMetrics().getMaxAscent() + insets.top);
        }
    }
}
