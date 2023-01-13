package com.indexss;

import javax.swing.*;
import java.net.URL;

public class Data {
    public static URL heartURL = Data.class.getResource("statics/heart.png");
    public static URL correctURL = Data.class.getResource("statics/correct.png");
    public static URL wrongURL = Data.class.getResource("statics/wrong.png");
    public static URL timeoutURL = Data.class.getResource("statics/timeout.png");
    public static URL deadURL = Data.class.getResource("statics/dead.png");

    public static ImageIcon heart;
    public static ImageIcon correct;
    public static ImageIcon wrong;
    public static ImageIcon timeout;
    public static ImageIcon dead;

    static {
        heart = new ImageIcon(heartURL);
        correct = new ImageIcon(correctURL);
        wrong = new ImageIcon(wrongURL);
        timeout = new ImageIcon(timeoutURL);
        dead = new ImageIcon(deadURL);
    }
}
