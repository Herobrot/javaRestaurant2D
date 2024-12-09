package utils;

public class GameConfig {
    // Configuración del restaurante
    public static final int WINDOW_WIDTH = 794;
    public static final int WINDOW_HEIGHT = 456;
    public static final int TOTAL_TABLES = 10;
    public static final int TOTAL_WAITERS = 1;
    public static final int TOTAL_COOKS = 2;

    // Posiciones fijas
    public static final double ENTRANCE_X = 550;
    public static final double ENTRANCE_Y = 70;
    public static final double KITCHEN_X = 110;
    public static final double KITCHEN_Y = 140;

    public static final double WAITER_X = 110;
    public static final double WAITER_Y = 290;

    public static final double RECEPTIONIST_X = 640;
    public static final double RECEPTIONIST_Y = 90;

    // Velocidades de movimiento (pixels por segundo)
    public static final double CUSTOMER_SPEED = 100.0;
    public static final double WAITER_SPEED = 150.0;

    // Tiempos (en milisegundos)
    public static final int MIN_EATING_TIME = 5000;
    public static final int MAX_EATING_TIME = 10000;

    // Dimensiones de los sprites
    public static final int SPRITE_SIZE = 32;


    // Nuevas constantes para el posicionamiento de mesas
    public static final int TABLES_PER_ROW = 5;
    public static final double TABLES_START_X = 180; // Posición X inicial de las mesas
    public static final double TABLES_START_Y = 50; // Posición Y inicial de las mesas
    public static final double TABLE_SPACING_X = 50; // Espacio entre mesas horizontalmente
    public static final double TABLE_SPACING_Y = 80; // Espacio entre mesas verticalmente

    // Offset para la posición de los clientes relativa a la mesa
    public static final double CUSTOMER_OFFSET_X = SPRITE_SIZE * 0.5;
    public static final double CUSTOMER_OFFSET_Y = SPRITE_SIZE * 0.5;

    private GameConfig() {
        // Constructor privado para evitar instanciación
    }
}