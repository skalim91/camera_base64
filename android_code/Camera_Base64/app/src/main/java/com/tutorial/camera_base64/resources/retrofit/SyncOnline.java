package com.tutorial.camera_base64.resources.retrofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.tutorial.camera_base64.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.tutorial.camera_base64.resources.AppConstant.RetrofitDummy;

public class SyncOnline {

    static Context ctx;

    public SyncOnline( Context c  ){
        this.ctx = c;
    }


    public boolean networkAvailable() {

        Runtime runtime = Runtime.getRuntime();
        try {

            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 67.222.152.138");
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 google.com");
            int exitValue = ipProcess.waitFor();

            Log.d("IP ADDR", ipProcess.toString());
            Log.d("IP ADDR", String.valueOf(exitValue));
            return (exitValue == 0 || exitValue == 1);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;


    }


    public void CustomDialogNoInternet(){
        /*
         * Custom Dialog box starts
         */

        AlertDialog alert = new AlertDialog.Builder( ctx).create();
        alert.setTitle("Sorry");
        alert.setMessage("Please Check Your Internet Connection");
        alert.setIcon(R.drawable.ic_alert);
        alert.show();
        /*
         * Custom dialog box ends
         */

    }


    public void getData(final Loader ldr, final TextView TxtView ){

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "2");

        //RetrofitWebService api = RetrofitInitilizer();
        RetrofitWebService api = RetrofitDummy();

        api.getData(

                params,
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {


                        BufferedReader reader = null;

                        String output = "";

                        try {
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                            output = reader.readLine();

                            Log.d("Prof",output+"--- output" );

                            JSONObject json  = new JSONObject(output);

                            Log.d("Prof",json.toString()+"---");

                            ldr.HideLoader();

                            TxtView.setText( json.getString("data") );



                        } catch (IOException e) {

                            ldr.HideLoader();

                            Log.d("Prof","Excep "+e.toString());
                            Toast.makeText(ctx, "I/O Exception\n"+e.toString(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        } catch (JSONException e) {
                            ldr.HideLoader();

                            Log.d("Prof","Excep JSOn "+e.toString());
                            Toast.makeText(ctx, "Json Exception\n"+e.toString(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ldr.HideLoader();

                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_LONG).show();
                    }

                }
        );


    }

    public void sendDataOnline(Activity act, final Loader ldr, final String Base64String, final TextView ResultTxtView ){

        //RetrofitWebService api = RetrofitInitilizer();
        RetrofitWebService api = RetrofitDummy();

        ldr.showDialog( act );
        api.sendImageOnline(

                Base64String,

                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {


                        BufferedReader reader = null;

                        String output = "";

                        try {
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                            output = reader.readLine();

                            Log.d("Prof",output+"--- output" );

                            JSONObject json  = new JSONObject(output);

                            Log.d("Prof",json.toString()+"---");

                            ldr.HideLoader();

                            ResultTxtView.setText( json.getString("message") );



                        } catch (IOException e) {

                            ldr.HideLoader();

                            Log.d("Prof","Excep "+e.toString());
                            Toast.makeText(ctx, "I/O Exception\n"+e.toString(), Toast.LENGTH_SHORT).show();
                            ResultTxtView.setText( "I/O Exception : \n"+e.toString() );

                            e.printStackTrace();
                        } catch (JSONException e) {
                            ldr.HideLoader();

                            Log.d("Prof","Excep JSOn "+e.toString());
                            Toast.makeText(ctx, "Json Exception\n"+e.toString(), Toast.LENGTH_SHORT).show();
                            ResultTxtView.setText( "JSON Exception : \n"+e.toString() );


                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ldr.HideLoader();

                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_LONG).show();
                        ResultTxtView.setText( "Failure Exception : \n"+error.toString() );

                    }

                }
        );


    }



}

