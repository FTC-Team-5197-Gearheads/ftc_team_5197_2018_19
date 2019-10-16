package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@Autonomous(name = "Linear OpMode Limit Switch Test", group = "Limit Switch")
@Disabled
public class LinearOpModeLimitSwitchTest extends LinearOpMode {
    LimitedDcMotorDrivenActuator tiltMotor = new LimitedDcMotorDrivenActuator("EH1motor0", 0, 20,
            DcMotorSimple.Direction.REVERSE, true, false, true, "EH1touchSensor0",
            null, 50, true, false, true, 0.1);
    //DigitalChannel limitSwitch;

    //LimitSwitch limitSwitch = new LimitSwitch(hardwareMap);


    @Override
    public void internalPreInit() {
        super.internalPreInit();
       // limitSwitch = hardwareMap.get(DigitalChannel.class, "touchSensor1");
        tiltMotor.initHardware(hardwareMap);

    }

    @Override
    public void internalPostInitLoop() { //works before initHardware
        super.internalPostInitLoop();
        telemetry.addData("Pressed", tiltMotor.getLimitSwitchStates());
        telemetry.update();
    }

    @Override
    public void internalPostLoop() { //works after start
        super.internalPostLoop();


    }

    @Override
    public void runOpMode() {
        //tiltMotor.initHardware(hardwareMap);
        /*
        while(!limitSwitch.limitSwitch.getState())
        {

        }
        telemetry.addData("Past initHardware", "Past Init");
        */


        waitForStart();



    }

    @Override
    protected void handleLoop() {
        super.handleLoop();
    }
}

class LimitSwitchOpMode extends Thread{
    DigitalChannel limitSwitch = null;


    @Override
    public void run() {
        super.run();
    }
}
