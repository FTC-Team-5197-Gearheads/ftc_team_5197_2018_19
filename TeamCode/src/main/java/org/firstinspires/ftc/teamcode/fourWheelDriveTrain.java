package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class fourWheelDriveTrain {

    // drivetrain specific measurements
    private final double     COUNTS_PER_MOTOR_REV; //maybe put in interface
    private final double     DRIVE_GEAR_REDUCTION; //maybe put in interface (probably for all variables)

    // drivetrain specific drive train members.
    private final double     WHEEL_DIAMETER_INCHES;
    private final double     DRIVE_WHEEL_SEPARATION;
    private final double     COUNTS_PER_INCH;
    private final DcMotor.RunMode RUNMODE;

    // REVTrix specific motor and actuator members.
    private DcMotor FrontLeftDrive   = null;
    private DcMotor FrontRightDrive  = null;
    private DcMotor RearLeftDrive    = null;
    private DcMotor RearRightDrive   = null;

    fourWheelDriveTrain( double COUNTS_PER_MOTOR_REV, double DRIVE_GEAR_REDUCTION, double WHEEL_DIAMETER_INCHES, double DRIVE_WHEEL_SEPARATION, DcMotor.RunMode RUNMODE){
        this.COUNTS_PER_MOTOR_REV = COUNTS_PER_MOTOR_REV;
        this.DRIVE_GEAR_REDUCTION = DRIVE_GEAR_REDUCTION;
        this.WHEEL_DIAMETER_INCHES = WHEEL_DIAMETER_INCHES;
        this.DRIVE_WHEEL_SEPARATION = DRIVE_WHEEL_SEPARATION;
        this.RUNMODE = RUNMODE;
        COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                (WHEEL_DIAMETER_INCHES * 3.1415);
    }

    public void DriveForwardInches(){

    }

    public void init(HardwareMap ahwMap){

        FrontLeftDrive = ahwMap.get(DcMotor.class, "motor0");
        FrontRightDrive = ahwMap.get(DcMotor.class, "motor1");
        RearLeftDrive = ahwMap.get(DcMotor.class, "motor2");
        RearRightDrive = ahwMap.get(DcMotor.class, "motor3");

        FrontLeftDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if
        // using AndyMark motors
        FrontRightDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if
        // using AndyMark motors
        RearLeftDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if
        // using AndyMark motors
        RearRightDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if
        // using AndyMark motors

        // Set all motors to zero power
        FrontLeftDrive.setPower(0);
        FrontRightDrive.setPower(0);
        RearLeftDrive.setPower(0);
        RearRightDrive.setPower(0);

        // Set both motors to run with encoders.
        FrontLeftDrive.setMode(RUNMODE);
        FrontRightDrive.setMode(RUNMODE);
        RearLeftDrive.setMode(RUNMODE);
        RearRightDrive.setMode(RUNMODE);
    }
}

