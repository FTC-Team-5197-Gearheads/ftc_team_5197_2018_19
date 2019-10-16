package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="Limit Switch Test Program")
@Disabled
public class Limit_Switch_Tester extends LinearOpMode {
    REVSensorDigitalTouch revSensorDigitalTouch = hardwareMap.get(REVSensorDigitalTouch.class, "EH!touchSensor0");
    DcMotor dcMotor;

    @Override
    public void runOpMode() {
        //revSensorDigitalTouch

    }
}
