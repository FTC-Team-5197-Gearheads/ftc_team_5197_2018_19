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

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import static java.lang.Math.*;

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
 * v 0.2    (In development) Use of modular code for four wheel driveetrain.
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
    private static final DcMotor.RunMode RUNMODE = DcMotor.RunMode.RUN_USING_ENCODER;

    fourWheelDriveTrain dt = new fourWheelDriveTrain(COUNTS_PER_MOTOR_REV, DRIVE_GEAR_REDUCTION,
            WHEEL_DIAMETER_INCHES, DRIVE_WHEEL_SEPARATION, RUNMODE);

}
