package ambient.cospaces.positioning.algorithm;

public class Position {
    public int x;
    public int y;
    public String building = "O";
    public int floor = 2;
    public String username = "no_username";
    public String roleColor = "#253bbb";
    public String roleName = "Student";
    public String imei = "1234";

    // Eine (versteckte) Klassenvariable vom Typ der eigenen Klasse
    private static Position instance;
    // Verhindere die Erzeugung des Objektes über andere Methoden
    private Position () {}
    // Eine Zugriffsmethode auf Klassenebene, welches dir '''einmal''' ein konkretes
    // Objekt erzeugt und dieses zurückliefert.
    public static Position getInstance () {
        if (Position.instance == null) {
            Position.instance = new Position ();
        }
        return Position.instance;
    }
}
