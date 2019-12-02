import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Program {

    Timer fpsTimer;
    MainWindow window = new MainWindow();

    //Sets how strongly to apply speed/acceleration, set to whatever the FPS is for 1:1 ratio
    double cSpeed = 60;
    //Sets the framerate
    int frames = 17;

    int xRotation = 0;
    int zRotation = 0;
    //X, Y, and Z values of the rocket relative to the center of the moon (m)
    int rocketPerciseX = 0;
    int rocketPerciseY = 4000000;
    int rocketPerciseZ = 0;
    //Rocket's speed tangent to it's orbit (m/s)
    //double rocketSpeed = 1565.89;
    //Rocket's xyz acceleration vectors
    double ax = 0;
    double ay = 0;
    double az = 0;
    //Rocket's xyz velocity vectors
    double vx = 1000;
    double vy = 0;
    double vz = 0;
    double dv = 0;
    //Angle of the triangle made from the center of the moon to the rocket in the x and y plane
    double orbitAngleXY = Math.toRadians(90);
    double orbitAngleXZ = 0;


    //Constants I'm gonna use
    final static int MOON_RADIUS = 1737100;
    final static double MOON_MASS = 7.34767309*Math.pow(10,22);
    //Ratio of real meters to pixels
    final static int CONVERSION_FACTOR = 12867;
    static final double G = 6.674*Math.pow(10,-11);

    final static int FRONT = 0;
    final static int RIGHT = 1;
    final static int LEFT = 2;
    final static int BACK = 3;
    final static int TOP = 4;
    final static int BOTTOM = 5;

    int scenePos = FRONT;


    SpriteObj rocket = new SpriteObj("Rocket", "Rocket.png", "RocketFlames.png" , 300, 300);
    SpriteObj moon = new SpriteObj("Moon", "MoonFront.png", 365, 265);

    Program(){
        window.addLower(moon);
        window.addLower(rocket);
        String[] moonSpriteArr = {"MoonFront.png", "MoonRight.png", "MoonLeft.png", "MoonBack.png", "MoonTop.png", "MoonBottom.png"};
        moon.setSpriteArr(moonSpriteArr);

        fpsTimer = new Timer(frames, new TimerListener());
        fpsTimer.start();
    }


    //Math that I'll be using enough to justify having methods for

    //Calculates the distance from the rocket to the moon
    int distanceFormula(){
        int d = (int) (Math.sqrt(Math.pow(rocketPerciseX, 2) + Math.pow(rocketPerciseY, 2)+ Math.pow(rocketPerciseZ, 2)));
        if (d > 1737100){
            return d;
        }
        else{
            //return 1737100;
            return d;
        }
    }

    int xzDistance(){
        return (int) (Math.sqrt(Math.pow(rocketPerciseX, 2) + Math.pow(rocketPerciseZ, 2)));
    }

    //Converts the precise location to the coordinate system used by the display
    int pointConvertX(){
        if (scenePos == FRONT) {
            return ((int) ((Double.valueOf(rocketPerciseX)) / Double.valueOf(CONVERSION_FACTOR))) + 500 - 14;
        }
        else if (scenePos == RIGHT){
            return -((int) ((Double.valueOf(rocketPerciseZ)) / Double.valueOf(CONVERSION_FACTOR))) + 500 - 14;
        }
        else if (scenePos == BACK){
            return -((int) ((Double.valueOf(rocketPerciseX)) / Double.valueOf(CONVERSION_FACTOR))) + 500 - 14;
        }
        else if (scenePos == LEFT){
            return ((int) ((Double.valueOf(rocketPerciseZ)) / Double.valueOf(CONVERSION_FACTOR))) + 500 - 14;
        }
        else if (scenePos == TOP){
            return -((int) ((Double.valueOf(rocketPerciseX)) / Double.valueOf(CONVERSION_FACTOR))) + 500 - 14;
        }
        else if (scenePos == BOTTOM){
            return ((int) ((Double.valueOf(rocketPerciseX)) / Double.valueOf(CONVERSION_FACTOR))) + 500 - 14;
        }
        else {
            return ((int) ((Double.valueOf(rocketPerciseX)) / Double.valueOf(CONVERSION_FACTOR))) + 500 - 14;
        }
    }
    //Different functions needed for x and y since the y coordinates need to be inverted
    int pointConvertY(){
        if (scenePos == TOP || scenePos == BOTTOM){
            return 400 + ((int)((Double.valueOf(rocketPerciseZ))/Double.valueOf(CONVERSION_FACTOR)));
        }
        else {
            return 400 - ((int)((Double.valueOf(rocketPerciseY))/Double.valueOf(CONVERSION_FACTOR)));
        }
    }

    static double gravCalc (int distance){
        double g = ((G*MOON_MASS)/(Math.pow(distance, 2)));
        //System.out.println(g);
        return g;
    }

    double getMoonXYAngle(){
        int xz = xzDistance();
        if (Double.valueOf(xz) != 0) {
            return Math.atan((Double.valueOf(rocketPerciseY))/(Double.valueOf(xz)));
        }
        else {
            return Math.PI/2;
        }
    }
    double getMoonXZAngle(){
        if (rocketPerciseX != 0) {
            return Math.atan((Double.valueOf(rocketPerciseZ))/(Double.valueOf(rocketPerciseX)));
        }
        else {
            return Math.PI/2;
        }
    }

    double getXZAccel(double gravity){
        return gravity * Math.cos(getMoonXYAngle());
    }

    double getXAccel(){
        double d = getXZAccel(gravCalc(distanceFormula()));
        if (rocketPerciseX < 0){
            return Math.abs(Math.cos(getMoonXZAngle()) * (d));
        }
        else {
            return -(Math.abs(Math.cos(getMoonXZAngle()) * (d)));
        }
    }
    double getYAccel(){
        double d = gravCalc(distanceFormula());
        if (rocketPerciseY < 0) {
            return Math.abs(Math.sin(getMoonXYAngle()) * (d));
        }
        else {
            return -(Math.abs(Math.sin(getMoonXYAngle()) * (d)));
        }
    }
    double getZAccel(){
        if (rocketPerciseZ != 0) {
            double d = getXZAccel(gravCalc(distanceFormula()));

            if (rocketPerciseZ < 0) {
                return Math.abs(Math.sin(getMoonXZAngle()) * d);
            } else {
                return -(Math.abs(Math.sin(getMoonXZAngle()) * (d)));
            }
        }
        else{
            return 0;
        }
    }

    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            mainLoop();
        }
    }

    void mainLoop(){

        /*
        orbitAngleX = getMoonXYAngle();
        orbitAngleY = getMoonXZAngle();
         */

        //Determines acceleration based on proximity to moon
        ax = getXAccel();
        ay = getYAccel();
        az = getZAccel();

        vx += ax/cSpeed;
        vy += ay/cSpeed;
        vz += az/cSpeed;

        //The speed is in m/s, but this code runs 60 times a second at normal speed, so it's divided by 60
        rocketPerciseX += (vx/cSpeed);
        rocketPerciseY += (vy/cSpeed);
        rocketPerciseZ += (vz/cSpeed);

        //Used for debugging stuff
        /*
        counter++;
        if (counter >= 1000) {
            System.out.println("Percise: " + rocketPerciseX + ", " + rocketPerciseY + ". Actual:" + pointConvertX(rocketPerciseX) + ", " + pointConvertY(rocketPerciseY));
            System.out.println("ax: " + ax + ", ay:" + ay);
            counter = 0;
        }
        */
        if (window.dPressed){
            xRotation++;
        }
        if (window.aPressed) {
            xRotation--;
        }
        if (window.wPressed){
            zRotation--;
        }
        if (window.sPressed){
            zRotation++;
        }

        if (window.spacePressed) {
            rocket.updateSprite(1);
            dv += (30/Double.valueOf(cSpeed));

            if (scenePos == FRONT){
                vx += (30*(Math.sin(Math.toRadians(xRotation))))/cSpeed;
                vy += (30*(Math.cos(Math.toRadians(xRotation))))/cSpeed;
            }
            else if (scenePos == RIGHT){
                vz -= (30*(Math.sin(Math.toRadians(xRotation))))/cSpeed;
                vy += (30*(Math.cos(Math.toRadians(xRotation))))/cSpeed;
            }
            else if (scenePos == LEFT){
                vz += (30*(Math.sin(Math.toRadians(xRotation))))/cSpeed;
                vy += (30*(Math.cos(Math.toRadians(xRotation))))/cSpeed;
            }
            else if (scenePos == BACK){
                vx -= (30*(Math.sin(Math.toRadians(xRotation))))/cSpeed;
                vy += (30*(Math.cos(Math.toRadians(xRotation))))/cSpeed;
            }
            else if (scenePos == TOP){
                vx += (30*(Math.sin(Math.toRadians(xRotation))))/cSpeed;
                vz -= (30*(Math.cos(Math.toRadians(xRotation))))/cSpeed;
            }
            else if (scenePos == BOTTOM){
                vx += (30*(Math.sin(Math.toRadians(xRotation))))/cSpeed;
                vz -= (30*(Math.cos(Math.toRadians(xRotation))))/cSpeed;
            }
        }
        else {
            rocket.updateSprite(0);
        }

        if (window.downPressed && !window.downUsed){
            window.downUsed = true;
            cSpeed = cSpeed*2;
        }
        if (window.upPressed && !window.upUsed){
            window.upUsed = true;
            cSpeed = cSpeed/2;
        }

        if (window.onePressed && !window.oneUsed){
            window.oneUsed = true;
            scenePos += 1;
            if (scenePos > 5){
                scenePos = 0;
            }
            moon.updateMoonSprite(scenePos);
        }

        if (scenePos == FRONT && rocketPerciseZ < 0){
            rocket.level = -1;
        }
        else if (scenePos == FRONT && rocketPerciseZ > 0){
            rocket.level = 1;
        }
        else if (scenePos == RIGHT && rocketPerciseX < 0){
            rocket.level = -1;
        }
        else if (scenePos == RIGHT && rocketPerciseX > 0){
            rocket.level = 1;
        }
        else if (scenePos == LEFT && rocketPerciseX > 0){
            rocket.level = -1;
        }
        else if (scenePos == LEFT && rocketPerciseX < 0){
            rocket.level = 1;
        }
        else if (scenePos == BACK && rocketPerciseZ > 0){
            rocket.level = -1;
        }
        else if (scenePos == BACK && rocketPerciseZ < 0){
            rocket.level = 1;
        }
        else if (scenePos == TOP && rocketPerciseY < 0){
            rocket.level = -1;
        }
        else if (scenePos == TOP && rocketPerciseY > 0){
            rocket.level = 1;
        }
        else if (scenePos == BOTTOM && rocketPerciseY > 0){
            rocket.level = -1;
        }
        else if (scenePos == BOTTOM && rocketPerciseY < 0){
            rocket.level = 1;
        }

        window.sortDrawList(window.lowerDrawList);

        window.boxVal = scenePos;

        rocket.rotateImg(xRotation);
        rocket.x = pointConvertX();
        rocket.y = pointConvertY();

        //System.out.println("X: " + rocket.x + ", Y: " + rocket.y);

        window.text[0] = "vx: " + vx;
        window.text[1] = "vy: " + vy;
        window.text[2] = "vz: " + vz;
        window.text[3] = "Total speed: " + (Math.sqrt(vx*vx + vy*vy));
        window.text[4] = "dv: " + dv;
        window.text[5] = "altitude: " + (distanceFormula() - MOON_RADIUS);
        window.text[6] = "Time factor: " + 60/cSpeed + "x";
        window.text[7] = "Z Angle: " + orbitAngleXZ;

        window.refresh();

    }
}
