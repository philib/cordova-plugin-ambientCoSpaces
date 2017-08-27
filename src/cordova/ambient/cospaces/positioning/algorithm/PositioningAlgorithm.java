package ambient.cospaces.positioning.algorithm;

public class PositioningAlgorithm {

    private Position position;
    public void calculatePos(int beaconId){

        position = Position.getInstance();

        if (beaconId == 1) {
            position.x = 22;
            position.y = 28;
        } else if (beaconId == 2) {
            position.x = 18;
            position.y = 28;
        } else if (beaconId == 3) {
            position.x = 22;
            position.y = 23;
        } else if (beaconId == 4) {
            position.x = 18;
            position.y = 23;
        } else if (beaconId == 5) {
            position.x = 22;
            position.y = 18;
        } else if (beaconId == 6) {
            position.x = 18;
            position.y = 18;
        } else if (beaconId == 7) {
            position.x = 22;
            position.y = 14;
        } else if (beaconId == 8) {
            position.x = 18;
            position.y = 13;
        } else if (beaconId == 9) {
            position.x = 22;
            position.y = 10;
        } else if (beaconId == 10) {
            position.x = 1;
            position.y = 18;
        } else if (beaconId == 11) {
            position.x = 7;
            position.y = 18;
        } else if (beaconId == 12) {
            position.x = 1;
            position.y = 13;
        } else if (beaconId == 13) {
            position.x = 7;
            position.y = 13;
        } else if (beaconId == 14) {
            position.x = 1;
            position.y = 9;
        } else if (beaconId == 15) {
            position.x = 7;
            position.y = 9;
        } else if (beaconId == 16) {
            position.x = 13;
            position.y = 13;
        } else if (beaconId == 17) {
            position.x = 12;
            position.y = 9;
        } else if (beaconId == 18) {
            position.x = 14;
            position.y = 7;
        } else if (beaconId == 19) {
            position.x = 18;
            position.y = 6;
        } else if (beaconId == 20) {
            position.x = 19;
            position.y = 8;
        } else if (beaconId == 21) {
            position.x = 11;
            position.y = 21;
        } else if (beaconId == 22) {
            position.x = 4;
            position.y = 23;
        } else if (beaconId == 24) {
            position.x = 14;
            position.y = 21;
        } else if (beaconId == 25) {
            position.x = 16;
            position.y = 14;
        } else if (beaconId == 26) {
            position.x = 7;
            position.y = 5;
        } else if (beaconId == 27) {
            position.x = 1;
            position.y = 1;
        } else if (beaconId == 28) {
            position.x = 1;
            position.y = 1;
        } else if (beaconId == 29) {
            position.x = 7;
            position.y = 1;
        } else if (beaconId == 30) {
            position.x = 1;
            position.y = 1;
        } else if (beaconId == 31) {
            position.x = 9;
            position.y = 14;
        } else if (beaconId == 32) {
            position.x = 9;
            position.y = 11;
        } else if (beaconId == 23) {
            position.x = 15;
            position.y = 17;
        } else if (beaconId == 33) {
            position.x = 16;
            position.y = 2;
        } else if (beaconId == 34) {
            position.x = 9;
            position.y = 2;
        }

        int scaleX = 100/24;
        int scaleY = 100/28;

        position.x *= scaleX;
        position.y *= scaleY;

        position.building = "O";
        position.floor = 2;
    }
}
