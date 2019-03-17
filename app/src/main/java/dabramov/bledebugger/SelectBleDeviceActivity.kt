package dabramov.bledebugger

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_select_ble_device.*

class SelectBleDeviceActivity : AppCompatActivity() {

    private val adapter = BleDeviceScanInfoAdapter(this::selectBleDevice)
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_ble_device)
        window.statusBarColor = getColor(R.color.colorPrimaryDark)
        setResult(RESULT_CANCELED)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE)
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_BLUETOOTH_PERMISSION)
        }
    }

    private fun selectBleDevice(position: Int) {
        bluetoothAdapter.stopLeScan(leScanCallback)
        setResult(Activity.RESULT_OK)
        intent.putExtra(EXTRA_MAC_ADDRESS, adapter.getMacAddress(position))
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.isNotEmpty() and (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                bluetoothAdapter.startLeScan(leScanCallback)
            } else {
                Toast.makeText(this, "Couldn't start scanning because permissions had not been granted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            } else {
                Toast.makeText(this, "Couldn't start scanning because bluetooth is disabled", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
        runOnUiThread {
            adapter.addOrUpdate(device.address.toString(), device.name)
        }
    }

    companion object {
        const val REQUEST_BLUETOOTH_PERMISSION = 0
        const val REQUEST_BLUETOOTH_ENABLE = 1
        const val EXTRA_MAC_ADDRESS = "extra_mac_address"
    }

}
