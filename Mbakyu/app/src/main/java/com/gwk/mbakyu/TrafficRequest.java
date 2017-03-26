package com.gwk.mbakyu;

import com.gwk.pikodove.annotation.PikoString;

/**
 * Created by Michinggun on 3/26/2017.
 *
 */

public class TrafficRequest extends Request{
    @PikoString public String from;
    @PikoString public String to;
    public short duration;
}
