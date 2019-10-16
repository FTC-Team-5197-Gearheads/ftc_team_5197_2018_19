package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name="Limited Motion Motor Test")
@Disabled
public class LiftMotorTest extends ModularRobotIterativeOpMode{
    LimitedDcMotorDrivenActuator motor = new LimitedDcMotorDrivenActuator("motor3",
            0, 500, DcMotorSimple.Direction.FORWARD, false,
            false, true, null,
            null, null,
            true, false, true, 1);


    @Override
    public void init() {
       motor.initHardware(hardwareMap);

    }

    @Override
    public void init_loop() {
        super.init_loop();
        //telemetry.addData("TouchSensorPredded", tiltMotor.getLimitSwitchStates());
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void loop() {
        //telemetry.addData("TouchSensorPredded", tiltMotor.getLimitSwitchStates());
    }

    @Override
    public void stop() {

    }
}
