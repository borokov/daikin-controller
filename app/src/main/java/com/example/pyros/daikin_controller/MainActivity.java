package com.example.pyros.daikin_controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
    protected DaikinModel m_daikinModel;
    protected DaikinHTTPController m_httpController;

    // main power switch
    protected Switch m_switchOnOff;

    // layout containing params
    protected LinearLayout m_layoutParam;
    protected LinearLayout m_waitConnectionLayout;
    protected LinearLayout m_mainLayout;

    protected TextView m_txtTargetTemp;
    protected TextView m_txtCurTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_daikinModel = new DaikinModel();
        m_httpController = new DaikinHTTPController();

        m_switchOnOff = (Switch)findViewById(R.id.switch_onoff);
        m_layoutParam = (LinearLayout)findViewById(R.id.layout_params);
        m_mainLayout = (LinearLayout)findViewById(R.id.layout_main);
        m_waitConnectionLayout = (LinearLayout)findViewById(R.id.layout_wait_connection);
        m_txtTargetTemp = (TextView)findViewById(R.id.txt_target_temp);
        m_txtCurTemp = (TextView)findViewById(R.id.txt_cur_temp);


        m_switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.this.m_daikinModel.status = isChecked ? DaikinModel.DaikinParamValue.Status.ON : DaikinModel.DaikinParamValue.Status.OFF;
                new SendParams(MainActivity.this.m_daikinModel, MainActivity.this.m_httpController, MainActivity.this).execute();
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
        m_mainActivity.sync(m_daikinModel);
    }

    private DaikinModel m_daikinModel;
    private DaikinHTTPController m_httpController;
    private MainActivity m_mainActivity;
}
