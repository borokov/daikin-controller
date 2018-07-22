package com.example.pyros.daikin_controller;

public class DaikinModel
{

    static public class DaikinParamValue
    {
        public enum Status
        {
            ON ("1"),
            OFF ("0");

            private String name;

            Status(String name){
                this.name = name;
            }

            public String toString(){
                return name;
            }
        }


        public enum Mode
        {
            AUTO ("1"),
            DEHUMDIFICATOR ("2"),
            COLD ("3"),
            HOT ("4"),
            FAN ("6");

            private String name;

            Mode(String name){
                this.name = name;
            }

            public String toString(){
                return name;
            }
        }

        public enum FanRate
        {
            AUTO ("A"),
            SILENCE ("B"),
            lvl_1 ("3"),
            lvl_2 ("4"),
            lvl_3 ("5"),
            lvl_4 ("6"),
            lvl_5 ("7");

            private String name;

            FanRate(String name){
                this.name = name;
            }

            public String toString(){
                return name;
            }
        }

        public enum FanDir {
            STOP("0"),
            VERTICAL("1"),
            HORIZONTAL("2"),
            ALL("3");

            private String name;

            FanDir(String name) {
                this.name = name;
            }

            public String toString() {
                return name;
            }
        }
    }

    void setStatus(String statusStr)
    {
        for ( DaikinParamValue.Status status_ : DaikinParamValue.Status.values())
        {
            if ( status_.toString().equals(statusStr) )
            {
                status = status_;
                break;
            }
        }
    }

    void setMode(String modeStr)
    {
        for ( DaikinParamValue.Mode mode_ : DaikinParamValue.Mode.values())
        {
            if ( mode_.toString().equals(modeStr) )
            {
                mode = mode_;
                break;
            }
        }
    }

    void setFanRate(String fanRateStr)
    {
        for ( DaikinParamValue.FanRate fanRate_ : DaikinParamValue.FanRate.values())
        {
            if ( fanRate_.toString().equals(fanRateStr) )
            {
                fanRate = fanRate_;
                break;
            }
        }
    }

    void setFanDir(String fanDirStr)
    {
        for ( DaikinParamValue.FanDir fanDir_ : DaikinParamValue.FanDir.values())
        {
            if ( fanDir_.toString().equals(fanDirStr) )
            {
                fanDir = fanDir_;
                break;
            }
        }
    }


    public DaikinParamValue.Status status;
    public DaikinParamValue.Mode mode;
    public DaikinParamValue.FanRate fanRate;
    public DaikinParamValue.FanDir fanDir;
    public float targetTemp = 25;
    public float currentTemp = 25;
    public float targetHumidity = 0;
}
