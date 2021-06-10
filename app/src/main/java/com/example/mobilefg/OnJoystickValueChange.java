package com.example.mobilefg;


public interface OnJoystickValueChange {

    /**
     * Called whenever the joystick value changes
     * @param x the x value of the joystick, normalized [-1, 1]
     * @param y the y value of the joystick, normalized [-1, 1]
     */
    void onChanged(float x, float y);
}