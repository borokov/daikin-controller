package com.example.pyros.daikin_controller;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity
{
    protected DaikinModel m_daikinModel;
    protected DaikinHTTPController m_httpController;

    // main power switch
    protected Switch m_switchOnOff;
    protected SeekBar m_sliderTemperature;
    protected SeekBar m_sliderFanSpeed;

    // layout containing params
    protected LinearLayout m_layoutParam;
    protected LinearLayout m_waitConnectionLayout;
    protected LinearLayout m_mainLayout;

    protected TextView m_txtTargetTemp;
    protected TextView m_txtCurTemp;
    protected TextView m_txtFanSpeed;

    protected RadioGroup m_rbMode;

    final float MIN_SLIDER_TEMP = 18;
    final float MAX_SLIDER_TEMP = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //-------------------------------------------------------------------------------
        // init members
        m_daikinModel = new DaikinModel();
        m_httpController = new DaikinHTTPController();

        m_switchOnOff = (Switch)findViewById(R.id.switch_onoff);
        m_sliderTemperature = (SeekBar) findViewById(R.id.slider_temp);
        m_sliderFanSpeed = (SeekBar) findViewById(R.id.slider_fan_speed);
        m_layoutParam = (LinearLayout) findViewById(R.id.layout_params);
        m_mainLayout = (LinearLayout)findViewById(R.id.layout_main);
        m_waitConnectionLayout = (LinearLayout)findViewById(R.id.layout_wait_connection);
        m_txtTargetTemp = (TextView)findViewById(R.id.txt_target_temp);
        m_txtCurTemp = (TextView)findViewById(R.id.txt_cur_temp);
        m_txtFanSpeed = (TextView)findViewById(R.id.txt_fanSpeed);
        m_rbMode = (RadioGroup) findViewById(R.id.rb_mode);

        //-------------------------------------------------------------------------------
        // plug event
        m_switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.this.m_daikinModel.status = isChecked ? DaikinModel.DaikinParamValue.Status.ON : DaikinModel.DaikinParamValue.Status.OFF;
                new SendParams(MainActivity.this.m_daikinModel, MainActivity.this.m_httpController, MainActivity.this).execute();
            }
        });

        m_rbMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case 3:
                        MainActivity.this.m_daikinModel.mode = DaikinModel.DaikinParamValue.Mode.COLD;
                        break;
                    case 4:
                        MainActivity.this.m_daikinModel.mode = DaikinModel.DaikinParamValue.Mode.HOT;
                        break;
                    case 6:
                        MainActivity.this.m_daikinModel.mode = DaikinModel.DaikinParamValue.Mode.FAN;
                        break;
                }
                new SendParams(MainActivity.this.m_daikinModel, MainActivity.this.m_httpController, MainActivity.this).execute();
            }
        });

        m_sliderFanSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch ( seekBar.getProgress() )
                {
                    case 0:
                        MainActivity.this.m_daikinModel.fanRate = DaikinModel.DaikinParamValue.FanRate.AUTO;
                        break;
                    case 1:
                        MainActivity.this.m_daikinModel.fanRate = DaikinModel.DaikinParamValue.FanRate.SILENCE;
                        break;
                    case 2:
                        MainActivity.this.m_daikinModel.fanRate = DaikinModel.DaikinParamValue.FanRate.lvl_1;
                        break;
                    case 3:
                        MainActivity.this.m_daikinModel.fanRate = DaikinModel.DaikinParamValue.FanRate.lvl_2;
                        break;
                    case 4:
                        MainActivity.this.m_daikinModel.fanRate = DaikinModel.DaikinParamValue.FanRate.lvl_3;
                        break;
                    case 5:
                        MainActivity.this.m_daikinModel.fanRate = DaikinModel.DaikinParamValue.FanRate.lvl_4;
                        break;
                    case 6:
                        MainActivity.this.m_daikinModel.fanRate = DaikinModel.DaikinParamValue.FanRate.lvl_5;
                        break;
                }
                new SendParams(MainActivity.this.m_daikinModel, MainActivity.this.m_httpController, MainActivity.this).execute();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                switch ( progress )
                {
                    case 0:
                        m_txtFanSpeed.setText("Auto");
                        break;
                    case 1:
                        m_txtFanSpeed.setText("Quiet");
                        break;
                    case 2:
                        m_txtFanSpeed.setText("Slow");
                        break;
                    case 3:
                        m_txtFanSpeed.setText("Medium");
                        break;
                    case 4:
                        m_txtFanSpeed.setText("High");
                        break;
                    case 5:
                        m_txtFanSpeed.setText("Higher");
                        break;
                    case 6:
                        m_txtFanSpeed.setText("Jean-claude SOUFFLERIE !!!");
                        break;
                }
            }

        });

        m_sliderTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MainActivity.this.m_daikinModel.targetTemp = getSliderValue(seekBar);
                new SendParams(MainActivity.this.m_daikinModel, MainActivity.this.m_httpController, MainActivity.this).execute();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                m_txtTargetTemp.setText(getSliderValue(seekBar).toString() + "°");
            }

            private Integer getSliderValue(SeekBar seekBar) {

                float temp = MIN_SLIDER_TEMP + (MAX_SLIDER_TEMP - MIN_SLIDER_TEMP) * (seekBar.getProgress()/100.0f);
                return new Integer(Math.round(temp));
            }
        });

        new GetParams(m_daikinModel, m_httpController, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sync(DaikinModel daikinModel)
    {
        m_switchOnOff.setChecked(daikinModel.status == DaikinModel.DaikinParamValue.Status.ON);
        m_layoutParam.setVisibility(daikinModel.status == DaikinModel.DaikinParamValue.Status.ON ? View.VISIBLE : View.GONE);
        m_txtTargetTemp.setText( new Integer(Math.round(daikinModel.targetTemp)).toString() + "°" );
        m_txtCurTemp.setText( new Integer(Math.round(daikinModel.currentTemp)).toString() + "°" );
        m_mainLayout.setVisibility(View.VISIBLE);
        m_waitConnectionLayout.setVisibility(View.GONE);

        switch ( daikinModel.fanRate )
        {
            case AUTO:
                m_txtFanSpeed.setText("Auto");
                m_sliderFanSpeed.setProgress(0);
                break;
            case SILENCE:
                m_txtFanSpeed.setText("Quiet");
                m_sliderFanSpeed.setProgress(1);
                break;
            case lvl_1:
                m_txtFanSpeed.setText("Slow");
                m_sliderFanSpeed.setProgress(2);
                break;
            case lvl_2:
                m_txtFanSpeed.setText("Medium");
                m_sliderFanSpeed.setProgress(3);
                break;
            case lvl_3:
                m_txtFanSpeed.setText("High");
                m_sliderFanSpeed.setProgress(4);
                break;
            case lvl_4:
                m_txtFanSpeed.setText("Higher");
                m_sliderFanSpeed.setProgress(5);
                break;
            case lvl_5:
                m_txtFanSpeed.setText("Jean-claude SOUFFLERIE !!!");
                m_sliderFanSpeed.setProgress(6);
                break;
        }

        switch (daikinModel.mode)
        {
            case HOT:
                m_rbMode.check(m_rbMode.getChildAt(0).getId());
                break;
            case COLD:
                m_rbMode.check(m_rbMode.getChildAt(1).getId());
                break;
            case FAN:
                m_rbMode.check(m_rbMode.getChildAt(2).getId());
                break;
        }

        int progress = Math.round( 100*(daikinModel.targetTemp - (float)MIN_SLIDER_TEMP) / ((float)MAX_SLIDER_TEMP - (float)MIN_SLIDER_TEMP) );
        m_sliderTemperature.setProgress(progress);
    }
}


class GetParams extends AsyncTask<Void,Void,Void>
{
    GetParams(DaikinModel daikinModel_, DaikinHTTPController httpController_, MainActivity mainActivity_)
    {
        m_daikinModel = daikinModel_;
        m_httpController = httpController_;
        m_mainActivity = mainActivity_;
    }

    protected void onPreExecute() {
    }

    protected Void doInBackground(Void... param) {
        m_httpController.getParams(m_daikinModel);
        return null;
    }

    protected void onPostExecute(Void result) {
        m_mainActivity.sync(m_daikinModel);
    }

    private DaikinModel m_daikinModel;
    private DaikinHTTPController m_httpController;
    private MainActivity m_mainActivity;
}


class SendParams extends AsyncTask<Void,Void,Void>
{
    SendParams(DaikinModel daikinModel_, DaikinHTTPController httpController_, MainActivity mainActivity_)
    {
        m_daikinModel = daikinModel_;
        m_httpController = httpController_;
        m_mainActivity = mainActivity_;
    }

    protected void onPreExecute() {
    }

    protected Void doInBackground(Void... param) {
        m_httpController.sendParams(m_daikinModel);
        return null;
    }

    protected void onPostExecute(Void result) {
        new GetParams(m_daikinModel, m_httpController, m_mainActivity).execute();
    }

    private DaikinModel m_daikinModel;
    private DaikinHTTPController m_httpController;
    private MainActivity m_mainActivity;
}
