package com.example.galaxy

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import com.example.galaxy.ui.theme.GalaxyTheme
import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissions: ActivityResultLauncher<Set<String>>

    // HealthConnectClient 초기화
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(this) }

    fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    @SuppressLint("MissingPermission")
    fun PairingBluetoothListState(activity: Activity) {


        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // 블루투스 어댑터가 null인 경우 처리
            // TODO: 블루투스 서비스를 사용할 수 없는 경우
            Log.d("Bluetooth", "블루투스 어댑터를 사용할 수 없습니다.")
            return
        }

        val bluetoothDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
        for (bluetoothDevice in bluetoothDevices) {
            if (isConnected(bluetoothDevice)) {
                // TODO: 연결중인 상태
                Log.d("Bluetooth", "${bluetoothDevice.name}가 연결 중입니다.")
            } else {
                // TODO: 연결중이 아닌 상태
                Log.d("Bluetooth", "${bluetoothDevice.name}가 연결되어 있지 않습니다.")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val status = HealthConnectClient.getSdkStatus(this)

        if (HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED == status) {
            val uriString =
                "market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding"
            this.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", this@MainActivity.packageName)
                }
            )
        }

        PairingBluetoothListState(this)

        setContent {
            GalaxyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "$status",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {


        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        GalaxyTheme {
            Greeting("Android")
        }
    }
}
