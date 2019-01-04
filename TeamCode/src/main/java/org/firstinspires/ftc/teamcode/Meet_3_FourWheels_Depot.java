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
    volatile private boolean isLanded = false; //volatile as modified by different threads https://www.javamex.com/tutorials/synchronization_volatile.shtml
    volatile private boolean isUnhooked = false;
    volatile private static String driveTrainStatus = "Drivetrain Status";
    volatile private static String mineralLifterStatus = "Mineral Lifter Status";
    volatile private static String goldLocatorStatus = "Gold Locator Status";

    private static final String INITIALIZED = "INITIALIZED";
    private static final String EXECUTION_COMPLETE_STRING = "Execution Complete";

    volatile private boolean visible = false;
    volatile private Pos pos = Pos.UNKNOWN;
    volatile private double x = 0.0;
    volatile private double y = 0.0;


    @Override
    public void init() {  //Define the behaviour of systems and initialize hardware
        //Drivetrain Code
        robot.threadDT = new REVTrixbot.JavaThreadDrivetrain(){
            @Override
            public void run() {
                super.run();
                driveTrainStatus = "Waiting for Landing";
                while(!isLanded);
                driveTrainStatus = "Moving to Gold Mineral Inspection Location";
                encoderDrive(1, -3, 3);
                isUnhooked = true;
                encoderDrive(1, 3, 3); //TODO. Figure our driving to gold position
                driveTrainStatus = "Moving to Depot and then Crater";
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
                driveTrainStatus = EXECUTION_COMPLETE_STRING;
            }
        };
        robot.threadDT.initHardware(hardwareMap);
        driveTrainStatus = INITIALIZED;

        //Mineral Lifter Code
        robot.threadMineralLifter = new REVTrixbot.JavaThreadMineralLifter();
        robot.threadMineralLifter.threadedLinearActuatorArm = new REVTrixbot.JavaThreadMineralLifter.ThreadedLinearActuatorArm();
        robot.threadMineralLifter.threadedArmLifter = new REVTrixbot.JavaThreadMineralLifter.ThreadedArmLifter(){ //define the arm behaviour
            @Override
            public void run() {
                super.run();
                mineralLifterStatus = "Landing";
                move(1, 30); //TODO decide if breaking is necesarry
                try {
                    sleep(1000); //allow time for robot to fall
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isLanded = true;
                //TODO lock the linear actuator
                mineralLifterStatus = "Waiting for robot to unhook";
                while(!isUnhooked); //wait for robot to unhook itself
                mineralLifterStatus = "Retracting Arm";
               //moveToMinPos(0.1); //TODO check if bug is here in method moveToMinPos()
                try { //TEMPORARY to simulate the time it takes for above statement
                    sleep(1000); //allow time for robot to fall
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mineralLifterStatus = EXECUTION_COMPLETE_STRING;
            }
        };
        robot.threadMineralLifter.initHardware(hardwareMap);
        mineralLifterStatus = INITIALIZED;

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

            private final static int MIDPOINT = 0;
            private final static int LEFTPOINT = -106;
            private final static int RIGHTPOINT = 106;

            @Override
            public void run() {
                super.run();
                goldLocatorStatus = "Active";
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
                goldLocatorStatus = "Inactive"; //don't know if this will ever display;
            }
        };
        robot.goldLocator.tune();
        robot.goldLocator.initHardware(hardwareMap);
        goldLocatorStatus = INITIALIZED;
    }

    @Override
    public void internalPostInitLoop() {
        super.internalPostInitLoop();
        telemetry.addData("Drivetrain Status", driveTrainStatus);
        telemetry.addData("Mineral Lifter Status", mineralLifterStatus);
        telemetry.addData("Gold Locator Status", goldLocatorStatus);
        telemetry.addData("   Is Found:", visible);
        telemetry.addData("   X Pos:", x);
        telemetry.addData("   Gold Pos:", pos);
    }

    @Override
    public void start() {  //Start threads
        super.start();
        robot.threadDT.start();
         //TODO see if telemetry.update necesarry or use a telemetry loop

        robot.goldLocator.enable();
        robot.goldLocationUpdater.start();

        robot.threadMineralLifter.threadedArmLifter.start();


    }

    @Override
    public void loop() {  //Control threads as needed.

    }

    @Override
    public void internalPostLoop() { //for updating telemtry per FTC Javadocs.
        super.internalPostLoop();
        telemetry.addData("Drivetrain Status", driveTrainStatus);
        telemetry.addData("Mineral Lifter Status", mineralLifterStatus);
        telemetry.addData("Gold Locator Status", goldLocatorStatus);
        telemetry.addData("   Is Found:", visible);
        telemetry.addData("   X Pos:", x);
        telemetry.addData("   Gold Pos:", pos);
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
