/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.support.annotation.Nullable;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single
 * robot, our competition REVTrixbot. It will compete in 2018-2019 FTC game
 * "Rover Ruckus".
 *
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot"
 * for usage examples easily converted to run on a REVTrixbot.
 *
 * This robot class operates a 4-wheel drive pushbot with a linear slide for
 * latching onto the Lander, swappable claws to grab Minerals, and OpenCV for
 * Mineral color discrimination.
 *
 * Version history
 * ======= ======
 * v 0.1    10/11/18 jmr primitive version, just enough to test the drive train.
 *          No class hierarchy, no initialization or run mode methods. Yet.
 *
 * v 0.2    10/28/18 Use of modular code for four wheel drivetrain.
 *
 * v 0.3    (In development) REVTrix has one port controlling each side of the DT now. Therefore, it
 *           now uses a TwoWheelDriveTrain although each of the four wheels are powered. Lifter code
 *
 * v 0.3.5    Deposit team identifier.
 *
 * v 0.4.0    Updated gold detector
 */

public class REVTrixbot extends GenericFTCRobot
{
    // REVTrixbot specific measurements
    // ** to do: calibration.
    private static final double     COUNTS_PER_MOTOR_REV    = 288 ;
    private static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;

    // REVTrixbot specific drive train members.
    // ** to do: check these for REVTrixbot dimensions.
    private static final double     WHEEL_DIAMETER_INCHES   = 3.5 ; // 90mm Traction Wheel
    private static final double     DRIVE_WHEEL_SEPARATION  = 15.0 ;
    private static final DcMotor.RunMode RUNMODE = DcMotor.RunMode.RUN_USING_ENCODER; //encoder cables installed 10/27/18

    REVTrixbot(){
        super.init();
    }

    FourWheelDriveTrain dt = new FourWheelDriveTrain(COUNTS_PER_MOTOR_REV, DRIVE_GEAR_REDUCTION,
            WHEEL_DIAMETER_INCHES, DRIVE_WHEEL_SEPARATION, RUNMODE, "EH1motor0", "EH1motor1",
            "EH1motor2", "EH1motor3");

    JavaThreadDrivetrain threadDT; //instantiate in opmode, where run method is defined

    JavaThreadMineralLifter threadMineralLifter;


    GoldMineralDetector_2 goldLocator;

    Thread goldLocationUpdater; //need to use Thread as GoldLocator is Runnable since it cannot be subclassed

    TeamIdenfifierDepositer idenfierFor5197Depositer = new TeamIdenfifierDepositer(0.62,0.2, "EH1servo5"); //move to 180 at initHardware. Then to close to

    MineralPushingPaddles revTrixbotMineralPaddles = new MineralPushingPaddles(0.0, 0.0, 0.4, "EH1servo3", "EH1servo4");

    /*

    LimitedDcMotorDrivenActuator roverRuckusRevTrixBotLift = new LimitedDcMotorDrivenActuator("EH2motor1",  //Clockwise rotation is up
            0, 3500, DcMotorSimple.Direction.FORWARD, true,  //TODO add a "tolerance" encoder counts value to allow a few encoders count off. Of course limit switches are always better.
            false, true, "EH1touchsensor1", null,
            30,
            true, false, true, 0.01); //TODO maybe thorw IllegalArgument exception for going to Min or Max without limit switch. Need to see if rotations being counted before runtime.
     */
    MineralLifter revTrixBotMineralArm = new MineralLifter(0, 0.9,
            0, 3200, 0,
            10, "EH2servo0", "EH2servo1",
            "EH2motor0", "EH2motor1"); //Not ready.


    public static class JavaThreadDrivetrain extends FourWheelDriveTrain{  //TODO: Temporaily giving it this name to ease transition to multithreaded code.
        JavaThreadDrivetrain(){
            super(REVTrixbot.COUNTS_PER_MOTOR_REV, REVTrixbot.DRIVE_GEAR_REDUCTION,
                    REVTrixbot.WHEEL_DIAMETER_INCHES, REVTrixbot.DRIVE_WHEEL_SEPARATION,
                    REVTrixbot.RUNMODE, "EH1motor0",
                    "EH1motor1", "EH1motor2",
                    "EH1motor3");
        }
    }

    public static class JavaThreadMineralLifter extends MineralLifter{ //wrapper so parameters don't need to be inputted again in opmodes
        JavaThreadMineralLifter(){
            super(0, 0.9,
                    0, 3200, 0,
                    10, "EH2servo0", "EH2servo1",
                    "EH2motor0", "EH2motor1");
        }

        ThreadedArmLifter threadedArmLifter;
        ThreadedLinearActuatorArm threadedLinearActuatorArm;

        @Override
        public void interrupt() {  //probably more professional way, but for now, this interrupts the entire MineralLifterSystem
            super.interrupt();
            threadedArmLifter.interrupt();
            threadedLinearActuatorArm.interrupt();
        }

        @Override
        public void initHardware(HardwareMap ahwMap) {
            threadedArmLifter.initHardware(ahwMap);
            threadedLinearActuatorArm.initHardware(ahwMap);
        }

        public static class ThreadedArmLifter extends LimitedDcMotorDrivenActuator{


            private static final String LA_ARM_LIFTER_MOTOR_NAME = "EH2motor0";
            private static final int LA_ARM_LIFTER_STOWED_ROTATIONS = 0;
            private static final int LA_ARM_LIFTER_ERECT_ROTATIONS = 3200;

            ThreadedArmLifter(){
                super(LA_ARM_LIFTER_MOTOR_NAME,
                        LA_ARM_LIFTER_STOWED_ROTATIONS, LA_ARM_LIFTER_ERECT_ROTATIONS, DcMotorSimple.Direction.FORWARD,
                        false, false, true, null,
                        null, null,
                        true, false, true,0.0);
            }
        }

        public static class ThreadedLinearActuatorArm extends LimitedDcMotorDrivenActuator{
            private static final String LA_MOTOR_NAME = "EH2motor1";
            private static final int LA_RETRACTED_ROTATIONS = 0;
            private static final int LA_EXTENDED_ROTATIONS = 50;
            public ThreadedLinearActuatorArm(){
                super(LA_MOTOR_NAME, LA_RETRACTED_ROTATIONS, LA_EXTENDED_ROTATIONS, DcMotorSimple.Direction.FORWARD, false,
                        false, true, null, null,
                        null,
                        true, false, true,1);
            }
        }


    }

    public static class MineralLifter extends Thread implements FTCModularizableSystems{ //nested since it is technically not modularizable
        private Servo gripper = null;
        private Servo gripper_wrist = null;
        private final double GRIPPER_CLOSED;
        private final double GRIPPER_OPEN;
        private final String GRIPPER_SERVO_NAME;
        private final String GRIPPER_WRIST_NAME;

        LimitedDcMotorDrivenActuator laArmLifter;
        LimitedDcMotorDrivenActuator laArm;

        MineralLifter(final double GRIPPER_CLOSED, final double GRIPPER_OPEN, final int LA_ARM_LIFTER_STOWED_ROTATIONS,
                      final int LA_ARM_LIFTER_ERECT_ROTATIONS, final int LA_RETRACTED_ROTATIONS, final int LA_EXTENDED_ROTATIONS,
                      final String GRIPPER_SERVO_NAME, final String GRIPPER_WRIST_NAME, final String LA_ARM_LIFTER_MOTOR_NAME, final String LA_MOTOR_NAME){ //TODO Maybe Multithread Lifter and LA so they run simultaneosly
            this.GRIPPER_CLOSED = GRIPPER_CLOSED;
            this.GRIPPER_OPEN = GRIPPER_OPEN;
            this.GRIPPER_SERVO_NAME = GRIPPER_SERVO_NAME;
            this.GRIPPER_WRIST_NAME = GRIPPER_WRIST_NAME;


            laArmLifter = new LimitedDcMotorDrivenActuator(LA_ARM_LIFTER_MOTOR_NAME,
                    LA_ARM_LIFTER_STOWED_ROTATIONS, LA_ARM_LIFTER_ERECT_ROTATIONS, DcMotorSimple.Direction.FORWARD,
                    false, false, true, null,
                    null, null,
                    true, false, true,0.0);

            laArm = new LimitedDcMotorDrivenActuator(LA_MOTOR_NAME,
                    LA_RETRACTED_ROTATIONS, LA_EXTENDED_ROTATIONS, DcMotorSimple.Direction.FORWARD, false,
                    false, true, null, null,
                    null,
                    true, false, true,1);
        }

        public void initHardware(HardwareMap ahwMap){
            gripper = ahwMap.get(Servo.class, GRIPPER_SERVO_NAME);
            gripper_wrist = ahwMap.get(Servo.class, GRIPPER_WRIST_NAME);
            //gripper_wrist = ahwMap.get(Servo.class, );
            closeGripper();


            laArmLifter.initHardware(ahwMap);
            laArm.initHardware(ahwMap);

        }

        public void teleOpGrip(boolean gripButton, boolean openButton){ //preferably a trigger
            if(gripButton)
                closeGripper();
            if(openButton)
                openGripper();
        }

        /*
        public void teleOpGripArmUpDown (boolean upButton, boolean downButton){
            if (upButton)
                upGripperArm();
            if (downButton)
                downGripperArm();
        }
        */

        public void openGripper(){
            gripper.setPosition(GRIPPER_OPEN);
        }

        public void closeGripper(){
            gripper.setPosition(GRIPPER_CLOSED);
        }

       // public void upGripperArm(){ gripper_wrist.setPosition( UP_AND_DOWN + 1.0 ); } TODO study this

       // public void downGripperArm(){ gripper_wrist.setPosition( UP_AND_DOWN - 1.0 );} TODO study this


    }

    public class TeamIdenfifierDepositer implements FTCModularizableSystems{
        private Servo glypgDepositServo = null;
        private final double INIT_POS;
        private final double DEPOSIT_POS;
        private final String SERVO_NAME;

        TeamIdenfifierDepositer(final double INIT_POS, final double DEPOSIT_POS, final String SERVO_NAME){
            this.INIT_POS = INIT_POS;
            this.DEPOSIT_POS = DEPOSIT_POS;
            this.SERVO_NAME = SERVO_NAME;
        }

        public void initHardware(HardwareMap ahwMap) {
            glypgDepositServo = ahwMap.get(Servo.class, SERVO_NAME);
            glypgDepositServo.setPosition(INIT_POS);
        }

        public void depositTeamIdentifier(){
            glypgDepositServo.setPosition(DEPOSIT_POS);
        }
    }

    public class MineralPushingPaddles implements FTCModularizableSystems{
        private Servo leftPaddle = null;
        private Servo rightPaddle = null;
        private final double INIT_POS;
        private final double RETRACTED_POS;
        private final double DEPLOYED_POS;
        private final String LEFT_SERVO_NAME;
        private final String RIGHT_SERVO_NAME;
        private boolean deployRetractButtonWasPressed = false;

        MineralPushingPaddles(final double INIT_POS, final double RETRACTED_POS, final double DEPLOYED_POS,
                              final String LEFT_SERVO_NAME, final String RIGHT_SERVO_NAME){
            this.INIT_POS = INIT_POS;
            this.RETRACTED_POS = RETRACTED_POS;
            this.DEPLOYED_POS = DEPLOYED_POS;
            this.LEFT_SERVO_NAME = LEFT_SERVO_NAME;
            this.RIGHT_SERVO_NAME = RIGHT_SERVO_NAME;
        }

        @Override
        public void initHardware(HardwareMap ahwMap) {
            leftPaddle = ahwMap.get(Servo.class, LEFT_SERVO_NAME);
            rightPaddle = ahwMap.get(Servo.class, RIGHT_SERVO_NAME);
            leftPaddle.setPosition(INIT_POS);
            rightPaddle.setPosition(INIT_POS);
        }

        public void teleOpDeployRetractPaddles(boolean deployRetractButton){
            if(deployRetractButton){
                leftPaddle.setPosition(DEPLOYED_POS);
                rightPaddle.setPosition(DEPLOYED_POS);
                deployRetractButtonWasPressed = true;
            }
            else if (deployRetractButtonWasPressed){
                leftPaddle.setPosition(RETRACTED_POS);
                rightPaddle.setPosition(RETRACTED_POS);
                deployRetractButtonWasPressed = false; //reset. Now
            }
        }
    }
}
