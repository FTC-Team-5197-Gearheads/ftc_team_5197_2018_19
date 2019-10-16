package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name ="TrainerbotMovementMethods")
@Disabled
public class TrainebotMovementMethods extends LinearOpMode {
    Trainerbot trainerbot = new Trainerbot();
    @Override
    public void runOpMode() {
        trainerbot.dt.initHardware(hardwareMap);
        waitForStart();
        //trainerbot.dt.encoderDrive(0.5, 9, 9);
        trainerbot.dt.turnAngleRadiusDrive(0.25, 90*Math.PI/180, 20.0);
        sleep(2000);
    }
}
