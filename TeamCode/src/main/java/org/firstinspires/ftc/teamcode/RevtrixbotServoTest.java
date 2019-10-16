package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Revtrixbot Servo Test", group = "Test")
@Disabled
public class RevtrixbotServoTest extends OpMode {
    REVTrixbot robot = new REVTrixbot();
    @Override
    public void init() {
        robot.revTrixBotMineralArm.initHardware(hardwareMap);
    }

    @Override
    public void loop() {
        robot.revTrixBotMineralArm.teleOpRotateWrist(gamepad2.a, gamepad2.b);
    }

    @Override
    public void internalPostLoop() {
        super.internalPostLoop();
        telemetry.addData("Servo Position", robot.revTrixBotMineralArm.gripper_wrist.getPosition());
    }
}
