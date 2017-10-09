package com.bubble.musikero;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Miguel on 05/10/2017.
 * Singleton Pattern
 * refs => https://android.jlelse.eu/how-to-make-the-perfect-singleton-de6b951dfdb0
 */
public class Singleton {

    // Constantes globales

    /*public static final SimpleDateFormat SDF_MIN_SEC =
            new SimpleDateFormat("hh:mm:ss", Locale.US);*/

    // variables de instancia del singleton

    private static volatile Singleton singleton;

    // variables globales o de control de la aplicacion

    // ...

    // ensure a private constructor to avoid multiple instances.
    private Singleton() {
        if (singleton != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    /**
     * Optimized method for acces to the only instance of the Singleton or global class.
     * */
    public static Singleton getInstance() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }

}
