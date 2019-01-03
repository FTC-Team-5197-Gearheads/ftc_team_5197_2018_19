/**
 * Meet 3 Code
 * This code looks different in structure from previous meets because it now utilizes multithreading
 * for each system in the robot. This enables the safe use of while loops, henceforth enabling the
 * use of limit switches.
 *
 *
 * Version History
 *========================  ======= ================================================================
 * @Author Lorenzo Pedroza  v 0.1   Created basic proof of concept to test a multithreaded Revtrixbot
 */

package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.DogeCV;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name = "Meet 3 REVTrixbot Depot", group = "Meet 3")
public class Meet_3_FourWheels_Depot extends OpMode {
    REVTrixbot robot = new REVTrixbot();
    volatile boolean isLanded = false; //volatile as modified by different threads https://www.javamex.com/tutorials/synchronization_volatile.shtml
    volatile boolean isUnhooked = false;
    private static final String DRIVETRAIN_STATUS_STRING = "Drivetrain Status";
    private static final String MINERAL_LIFTER_STATUS_STRING = "Mineral Lifter Status";
    private static final String INITIALIZED = "INITIALIZED";
    private static final String GOLD_LOCATOR_STATUS_STRING = "Gold Locator Status";
    private static final String EXECUTION_COMPLETE_STRING = "Execution Complete";

    @Override
    public void init() {  //Define the behaviour of systems and initialize hardware

        //Drivetrain Code
        robot.threadDT = new REVTrixbot.JavaThreadDrivetrain(){
            @Override
            public void run() {
                super.run();
                telemetry.addData(DRIVETRAIN_STATUS_STRING, "Waiting for Landing");
                telemetry.update();
                while(!isLanded);
                telemetry.addData(DRIVETRAIN_STATUS_STRING, "Moving to Gold Mineral Check Location");
                telemetry.update();
                encoderDrive(1, -3, 3);
                isUnhooked = true;
                encoderDrive(1, 3, 3); //TODO. Figure our driving to gold position
                telemetry.addData(DRIVETRAIN_STATUS_STRING,"Moving to Depot and then Crater");
                switch (robot.goldLocator.getGoldPos()){
                    case LEFT:
                        break;

                    case RIGHT:
                        break;

                    case MID:
                        break;

                    default:
                        break;
                }

                telemetry.addData(DRIVETRAIN_STATUS_STRING, EXECUTION_COMPLETE_STRING);
                telemetry.update();

            }
        };
        robot.threadDT.initHardware(hardwareMap);
        telemetry.addData(DRIVETRAIN_STATUS_STRING, INITIALIZED);

        //Mineral Lifter Code
        robot.threadMineralLifter = new REVTrixbot.JavaThreadMineralLifter();
        robot.threadMineralLifter.threadedLinearActuatorArm = new REVTrixbot.JavaThreadMineralLifter.ThreadedLinearActuatorArm();
        robot.threadMineralLifter.threadedArmLifter = new REVTrixbot.JavaThreadMineralLifter.ThreadedArmLifter(){ //define the arm behaviour
            @Override
            public void run() {
                super.run();
                telemetry.addData(MINERAL_LIFTER_STATUS_STRING, "Landing");
                telemetry.update();
                move(1, 30); //TODO decide if breaking is necesarry
                try {
                    sleep(1000); //allow time for robot to fall
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isLanded = true;
                //TODO lock the linear actuator
                telemetry.addData(MINERAL_LIFTER_STATUS_STRING, "Waiting for robot to unhook");
                telemetry.update();
                while(!isUnhooked); //wait for robot to unhook itself
                telemetry.addData(MINERAL_LIFTER_STATUS_STRING, "Retracting Arm");
                telemetry.update();
               moveToMinPos(0.1); //TODO check if bug is here in method moveToMinPos()
                try { //TEMPORARY to simulate the time it takes for above statement
                    sleep(1000); //allow time for robot to fall
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                telemetry.addData(MINERAL_LIFTER_STATUS_STRING, EXECUTION_COMPLETE_STRING);
                telemetry.update();
            }
        };
        robot.threadMineralLifter.initHardware(hardwareMap);
        telemetry.addData(MINERAL_LIFTER_STATUS_STRING, INITIALIZED);

        //Gold Locator Code
        robot.goldLocator = new GoldMineralDetector_2(){ //redefine behaviour
            @Override
            public void tune() {
                super.tune();
                alignSize = 640; // How wide (in pixels) is the range in which the gold object will be aligned. (Represented by green bars in the preview)
                alignPosOffset = 0; // How far from center frame to offset this alignment zone.
                downscale = 0.4; // How much to downscale the input frames

                areaScoringMethod = DogeCV.AreaScoringMethod.PERFECT_AREA;
                perfectAreaScorer.perfectArea = 2400;
                perfectAreaScorer.weight = 50;

                ratioScorer.weight = 50;
                ratioScorer.perfectRatio = 1.25;
            }
        };
        robot.goldLocationUpdater = new Thread(robot.goldLocator){
            public boolean visible = false;
            public Pos pos = Pos.UNKNOWN;
            private double x = 0.0;
            private double y = 0.0;
            private final static int MIDPOINT = 0;
            private final static int LEFTPOINT = -106;
            private final static int RIGHTPOINT = 106;

            @Override
            public void run() {
                super.run();
                while(!isInterrupted()){
                    visible = robot.goldLocator.isFound();
                    x = robot.goldLocator.getXPosition() - MIDPOINT;
                    y = robot.goldLocator.getYPosition();

                    if (robot.goldLocator.getArea() < 1200 )
                        visible = false;

                    if (robot.goldLocator.getRatio() > 2.5)
                        visible = false;

                    if (robot.goldLocator.getScore() > 10)
                        visible = false;

                    if (robot.goldLocator.getYPosition() < 120)
                        visible = false;

                    if(visible) {
                        if (x < 0)
                            pos = Pos.MID;
                        else if (x >= 0)
                            pos = Pos.RIGHT;
                    }   else {
                            pos = Pos.LEFT;
                    }
                    robot.goldLocator.updateGoldPos(pos);

                    /*TODO move this to the specialized telementry reporting funciotn in OPMOde
                    telemetry.addData("IsFound", visible);
                    telemetry.addData("X Pos", x);
                    telemetry.addData("Pos", pos);
                    telemetry.update();
                    */
                }
            }
        };
        robot.goldLocator.tune();
        robot.goldLocator.initHardware(hardwareMap);
        telemetry.addData(GOLD_LOCATOR_STATUS_STRING, INITIALIZED); //TODO move all telementry to speical telementry reporting funciotn

        telemetry.update();
    }

    @Override
    public void start() {  //Start threads
        super.start();
        robot.threadDT.start();
         //TODO see if telemetry.update necesarry or use a telemetry loop

        robot.goldLocator.enable();
        robot.goldLocationUpdater.start();
        telemetry.addData("Locator Status", "Enabled");
        telemetry.update();

        robot.threadMineralLifter.threadedArmLifter.start();


    }

    @Override
    public void loop() {  //Control threads as needed.

    }

    @Override
    public void stop() { //Interrupt threads
        super.stop();
        robot.threadDT.interrupt();
        robot.threadMineralLifter.interrupt();
        robot.goldLocationUpdater.interrupt();
        robot.goldLocator.disable(); //
    }

}
