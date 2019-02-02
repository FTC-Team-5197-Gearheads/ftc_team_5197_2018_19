/**
 * Meet 3 TeleOp code
 * @Author Lorenzo Pedroza
 * Date 1/7/18
 *
 */


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "REVTrixbot Monrovia ILT TeleOp", group = "ILT")
//@Disabled
public class REVTrixbotMultiThreadedTankDrive extends OpMode {

    REVTrixbot robot = new REVTrixbot();

    //TODO check concern that methods with while loops will stop if thread is interrupted (shouldn't be a problem per https://www.tutorialspoint.com/java/lang/thread_isinterrupted.htm)

    volatile private static String driveTrainStatus = "Drivetrain Status";
    volatile private static String mineralArmStatus = "Mineral Arm Status";
    volatile private static String mineralLifterArmRaiserStatus = "Arm Raiser Status";
    volatile private static String mineralLifterArmExtenderStatus = "Arm Extender Status";
    volatile private static String mineralLifterWristStatus = "Arm Wrist Status";
    volatile private static String mineralLifterGripperStatus = "Arm Gripper Status";

    private static final String INITIALIZED = "INITIALIZED";
    private static final String RUNNING = "Running";

    @Override
    public void init() {
        robot.threadDT = new REVTrixbot.REVTrixbotMTDrivetrain(){
            @Override
            public void run() {
                super.run();
                driveTrainStatus = RUNNING;
                while(!isInterrupted()){
                    //teleOpTankDrive(gamepad1);
                    teleOpArcadeDrive(gamepad1, F310JoystickInputNames.Joysticks.LEFT_STICK);
                }
            }
        };
        robot.threadDT.initHardware(hardwareMap);
        driveTrainStatus = INITIALIZED;

        robot.threadMineralLifter = new REVTrixbot.REVTrixbotMTMineralLifter(){
            private static final int LIFTER_PARALLEL_TO_ROBOT_POSITION = 100;
            @Override
            public void teleOpFullyStowMTMineralLifter(double laArmSpeed, double laArmLifterSpeed, boolean button) { //macro
                if(button)
                {
                    mineralArmStatus = "Fully Stowing Mineral Arm";
                    mineralLifterArmRaiserStatus = "Stowing";
                    mineralLifterArmExtenderStatus = "Stowing";
                    mineralLifterWristStatus = "Stowing";
                    mineralLifterGripperStatus = "Stowing";
                    super.teleOpFullyStowMTMineralLifter(laArmSpeed, laArmLifterSpeed, button);
                    mineralArmStatus = RUNNING;
                    mineralLifterArmRaiserStatus = RUNNING;
                    mineralLifterArmExtenderStatus = RUNNING;
                    mineralLifterWristStatus = RUNNING;
                    mineralLifterGripperStatus = RUNNING;
                }
            }

            public void getInMineralCargoBayDropPosition(boolean button){ //macro
                final int LA_ARM_LIFTER_MINERAL_DROP_POS = 3100; //TODO figure out this value
                final int LA_ARM_DROP_POS = 10; //TODO and this value

                if(gripper.getPosition() == GRIPPER_CLOSED && button) //only do once gripper closed with mineral. May need to add tolerance
                {
                    mineralArmStatus = "Moving to Mineral Drop Position";
                    mineralLifterWristStatus = "Moving to Mineral Drop Position";
                    mineralLifterGripperStatus = "Moving to Mineral Drop Position";
                    mineralLifterArmExtenderStatus = "Moving to Mineral Drop Position";
                    mineralLifterArmRaiserStatus = "Moving to Mineral Drop Position";
                    //TODO tell the wrist to move parallel to linera actuator

                    threadedLinearActuatorArm.moveToMinPos(0.3);

                    threadedArmLifter.moveToRotationCount(0.3, LA_ARM_LIFTER_MINERAL_DROP_POS);
                    threadedLinearActuatorArm.moveToRotationCount(0.3, LA_ARM_DROP_POS);
                    //TODO tell wrist to move to drop position above cargo bay
                }


                mineralArmStatus = RUNNING;
                mineralLifterWristStatus = RUNNING;
                mineralLifterGripperStatus = RUNNING;
                mineralLifterArmExtenderStatus = RUNNING;
                mineralLifterArmRaiserStatus = RUNNING;

            }

            public void getInMineralCraterCollectPosition(boolean button){
                if(button)
                {
                    mineralArmStatus = "Moving to Mineral Collect Position";
                    mineralLifterWristStatus = "Moving to Mineral Collect Position";
                    mineralLifterGripperStatus = "Moving to Mineral Collect Position";
                    mineralLifterArmExtenderStatus = "Moving to Mineral Collect Position";
                    mineralLifterArmRaiserStatus = "Moving to Mineral Collect Position";



                    //TODO tell the wrist to move parallel to linear actuator arm
                    threadedLinearActuatorArm.moveToMinPos(0.3);
                    threadedArmLifter.moveToRotationCount(0.3, LIFTER_PARALLEL_TO_ROBOT_POSITION);
                    //TODO tell the wrist to move down facing crater
                    gripper.setPosition(GRIPPER_OPEN);


                    mineralArmStatus = RUNNING;
                    mineralLifterWristStatus = RUNNING;
                    mineralLifterGripperStatus = RUNNING;
                    mineralLifterArmExtenderStatus = RUNNING;
                    mineralLifterArmRaiserStatus = RUNNING;
                }



            }

            @Override
            public void run() {
                super.run();
                //Declare all running systems
                mineralArmStatus = RUNNING;
                mineralLifterGripperStatus = RUNNING;
                mineralLifterWristStatus = RUNNING;
                while(!isInterrupted()){
                    //teleOpFullyStowMTMineralLifter(0.3, 0.3, gamepad2.y);
                    //getInMineralCargoBayDropPosition(gamepad2.dpad_up);
                   // getInMineralCraterCollectPosition(gamepad2.dpad_down);
                    //teleOpSingleButtonGrip(gamepad2.a);

                    teleOpRotateWrist(gamepad2.x && mineralLifterWristStatus == RUNNING, gamepad2.y && mineralLifterWristStatus == RUNNING);
                    //teleOpRotateWristWithGamepadTriggers(gamepad2); //Doesn't work too well
                    //teleOpGrip(gamepad2.a, gamepad2.b);
                    keepServoLevelToGround(gamepad2.b && mineralLifterWristStatus == RUNNING); //TODO test and refine this method

                }

            }
        };

        robot.threadMineralLifter.threadedLinearActuatorArm = new REVTrixbot.REVTrixbotMTMineralLifter.ThreadedLinearActuatorArm(){
            @Override
            public void teleOpMoveWithJoystick(double joyStickDouble) {
                if(mineralLifterArmExtenderStatus == RUNNING) //be sure no other method is using it
                    super.teleOpMoveWithJoystick(joyStickDouble);
            }

            @Override
            public void run() {
                super.run();
                mineralLifterArmExtenderStatus = RUNNING;
                while (!isInterrupted()){
                    teleOpMoveWithJoystick(gamepad2.left_stick_y); //for Clinston Zeng's preference
                    //teleOpMoveWithButtons(gamepad2.a, gamepad2.b, 1);
                }

            }
        };

        robot.threadMineralLifter.threadedArmLifter = new REVTrixbot.REVTrixbotMTMineralLifter.ThreadedArmLifter(){

            @Override
            public void teleOpMoveToHighestPosition(double speed, boolean button) {
                if(button)
                {
                    mineralLifterArmRaiserStatus = "Moving to highest position";
                    super.teleOpMoveToHighestPosition(speed, button);
                    mineralLifterArmRaiserStatus = RUNNING;
                }

            }

            @Override
            public void teleOpMoveWithJoystick(double joyStickDouble) {
                if(mineralLifterArmRaiserStatus == RUNNING) //do not move when other methods are using this actuator to avoid damage to the robot.
                    super.teleOpMoveWithJoystick(joyStickDouble);
            }

            @Override
            public void run() {
                super.run();
                mineralLifterArmRaiserStatus = RUNNING;
                while (!isInterrupted()){
                    //teleOpMoveToHighestPosition(0.3, gamepad1.y);
                    //teleOpMoveWithButtons(gamepad2.dpad_up, gamepad2.dpad_down, 1.0);
                    teleOpMoveWithJoystick(gamepad1.right_stick_y);
                }
            }
        };

        robot.threadMineralLifter.initHardware(hardwareMap);
        mineralArmStatus = INITIALIZED;
        mineralLifterWristStatus = INITIALIZED;
        mineralLifterGripperStatus = INITIALIZED;
        mineralLifterArmExtenderStatus = INITIALIZED;
        mineralLifterArmRaiserStatus = INITIALIZED;

    }

    @Override
    public void start() {
        super.start();
        robot.threadMineralLifter.start();
        robot.threadDT.start();

    }

    @Override
    public void internalPostInitLoop() {
        super.internalPostInitLoop();
        telemetry.addData("Drivetrain Status", driveTrainStatus);
        telemetry.addData("Mineral Lifter Status", mineralArmStatus);
        telemetry.addData("   Raiser Status", mineralLifterArmRaiserStatus);
        telemetry.addData("   Extender Status", mineralLifterArmExtenderStatus);
        telemetry.addData("   Wrist Status", mineralLifterWristStatus);
        telemetry.addData("   Gripper Status", mineralLifterGripperStatus);
    }

    @Override
    public void loop() {
       //robot.threadMineralLifter.teleOpSingleButtonGrip(gamepad2.a);
       //robot.threadMineralLifter.teleOpRotateWristWithGamepadTriggers(gamepad2.y, gamepad2.b);
    }

    @Override
    public void internalPostLoop() {
        super.internalPostLoop();
        telemetry.addData("Drivetrain Status", driveTrainStatus);
        telemetry.addData("Mineral Lifter Status", mineralArmStatus);
        telemetry.addData("   Raiser Status", mineralLifterArmRaiserStatus);
        telemetry.addData("   Extender Status", mineralLifterArmExtenderStatus);
        telemetry.addData("   Wrist Status", mineralLifterWristStatus);
        telemetry.addData("   Gripper Status", mineralLifterGripperStatus);
    }

    @Override
    public void stop() {
        super.stop();
        robot.threadMineralLifter.interrupt();
        robot.threadDT.interrupt();
    }
}
