package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Lifter Using DcMotor Lifter class Test")
@Disabled
public class Lift_Motor_Test_With_Using_Limited_Dc_Motor extends OpMode {
    private REVTrixbot robot = new REVTrixbot();
    @Override
    public void init() {
        robot.revTrixBotMineralArm.laArmLifter.initHardware(hardwareMap);
    }

    @Override
    public void internalPostInitLoop() {
        super.internalPostInitLoop();
        telemetry.addData("LA ARM LIFTER", robot.revTrixBotMineralArm.laArmLifter.getCurrentPosition());
    }

    @Override
    public void loop() {
        robot.revTrixBotMineralArm.laArmLifter.teleOpMoveWithButtons(gamepad1.a, gamepad1.b, 0.3);
    }
}
