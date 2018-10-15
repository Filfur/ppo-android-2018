package com.filfur.softwaredesign

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.support.design.widget.Snackbar
import android.telephony.TelephonyManager
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var imeiTextView: TextView
    private lateinit var versionTextView: TextView
    private lateinit var showImeiButton: Button
    private lateinit var mainActivityLayout: View

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showImeiButton = findViewById(R.id.showIMEIButton)
        imeiTextView = findViewById(R.id.IMEITextView)
        versionTextView = findViewById(R.id.versionTextView)
        mainActivityLayout = findViewById(R.id.mainActivityLayout)

        try {
            val packageInfo = BuildConfig.VERSION_NAME
            versionTextView.text = "${getString(R.string.app_version)} ${packageInfo}"
        } catch (e: PackageManager.NameNotFoundException) { }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            showImeiButton.setOnClickListener { askForReadPhoneStatePermission() }
        }else{
            imeiTextView.text = getString(R.string.imei) + getIMEI()
            showImeiButton.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Companion.PERMISSION_REQUEST_READ_PHONE_STATE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showImeiButton.visibility = View.GONE
                    imeiTextView.text = getString(R.string.imei) + getIMEI()
                } else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.READ_PHONE_STATE)){
                        Toast.makeText(this, "You have to give permission to see the IMEI",
                                Toast.LENGTH_LONG).show()
                    }else{
                        Snackbar.make(mainActivityLayout, "You have to give permission manually",
                                Snackbar.LENGTH_LONG)
                                .setAction("SETTINGS") { openSettingsForPhoneStatePermission() }
                                .show()
                    }
                }
                return
            }
            else -> {

            }
        }
    }

    private fun askForReadPhoneStatePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                val message = "Permission is needed to have this permission to show imei"
                Snackbar.make(mainActivityLayout, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("GRANT") {
                            ActivityCompat.requestPermissions(this,
                                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                                    Companion.PERMISSION_REQUEST_READ_PHONE_STATE)
                        }
                        .show()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        Companion.PERMISSION_REQUEST_READ_PHONE_STATE)
            }
        }
    }

    private fun openSettingsForPhoneStatePermission(){
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName"))
        startActivityForResult(appSettingsIntent, Companion.PERMISSION_REQUEST_READ_PHONE_STATE)

        finish()
        startActivity(intent)
    }

    @SuppressLint("HardwareIds")
    private fun getIMEI(): String {
        try{
            val phoneManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return phoneManager.deviceId
        } catch (e: SecurityException){
            throw e
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_READ_PHONE_STATE = 1
    }
}
