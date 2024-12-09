package utils;

public class GameConfig {
    // Restaurant Configuration
    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 384;
    public static final int TOTAL_TABLES = 10;
    public static final int TOTAL_WAITERS = 1;
    public static final int TOTAL_COOKS = 2;

    // Spawnpoints
    public static final double ENTRANCE_X = 300;
    public static final double ENTRANCE_Y = 340;
    public static final double KITCHEN_X = 220;
    public static final double KITCHEN_Y = 100;

    public static final double WAITER_X = 390;
    public static final double WAITER_Y = 330;

    public static final double RECEPTIONIST_X = 290;
    public static final double RECEPTIONIST_Y = 340;

    // Velocidades de movimiento (pixels por segundo)
    public static final double CUSTOMER_SPEED = 100.0;
    public static final double WAITER_SPEED = 150.0;

    // Tiempos (en milisegundos)
    public static final int MIN_EATING_TIME = 5000;
    public static final int MAX_EATING_TIME = 10000;

    // Dimensiones de los sprites
    public static final int SPRITE_SIZE = 24;


    // Nuevas constantes para el posicionamiento de mesas
    public static final int TABLES_PER_ROW = 5;
    public static final double TABLES_START_X = 80; // Posición X inicial de las mesas
    public static final double TABLES_START_Y = 200; // Posición Y inicial de las mesas
    public static final double TABLE_SPACING_X = 50; // Espacio entre mesas horizontalmente
    public static final double TABLE_SPACING_Y = 80; // Espacio entre mesas verticalmente

    // Offset para la posición de los clientes relativa a la mesa
    public static final double CUSTOMER_OFFSET_X = -1;
    public static final double CUSTOMER_OFFSET_Y = 10;

    private GameConfig() {
        // Constructor privado para evitar instanciación
    }
}