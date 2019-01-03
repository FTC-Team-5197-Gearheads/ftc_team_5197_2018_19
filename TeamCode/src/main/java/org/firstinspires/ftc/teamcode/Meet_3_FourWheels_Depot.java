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
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class Meet_3_FourWheels_Depot extends OpMode {
    REVTrixbot robot = new REVTrixbot();

    @Override
    public void init() {  //Define the behaviour of systems and initialize hardware
        robot.threadDT = new REVTrixbot.JavaThreadDrivetrain(){
            @Override
            public void run() {
                super.run();
                encoderDrive(1, 10, 10);
                while (robot.goldLocator.isFound()){

                }
            }
        };
        robot.threadDT.initHardware(hardwareMap);
        telemetry.addData("Drivetrain Status", "Initialized");

        robot.goldLocator = new GoldMineralDetector_2(){ //TODO make static class in REVTrixbot that can be thread for gold detecting
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
        robot.goldLocator.tune();
        robot.goldLocator.initHardware(hardwareMap);
        telemetry.addData("Locator Status", "Initialized");


    }

    @Override
    public void start() {  //Start threads
        super.start();
        robot.threadDT.start();
        telemetry.addData("Drivetrain Status", "Started");  //TODO see if telemetry.update necesarry or use a telemetry loop
        robot.goldLocator.enable();
        telemetry.addData("Locator Status", "Enabled");
    }

    @Override
    public void loop() {  //Control threads as needed.

    }

    @Override
    public void stop() { //Interrupt threads
        super.stop();
        robot.threadDT.interrupt();
        robot.goldLocator.disable(); //
    }

}
