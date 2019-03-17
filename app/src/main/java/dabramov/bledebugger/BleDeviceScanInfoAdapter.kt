package dabramov.bledebugger

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class BleDeviceScanInfoAdapter(val listener: ((Int) -> Unit)?): RecyclerView.Adapter<BleDeviceScanInfoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.tvName)!!
        val tvMac = itemView.findViewById<TextView>(R.id.tvMac)!!
        val cvRoot = itemView.findViewById<CardView>(R.id.card_view)!!
    }

    override fun onBindViewHolder(holder: BleDeviceScanInfoAdapter.ViewHolder, position: Int) {
        val info = mDataSet.get(position)

        holder.tvName.text = info.name ?: "---"
        holder.tvMac.text = info.mac
        if (listener != null) {
            holder.cvRoot.setOnClickListener {
                listener.invoke(position)
            }
            holder.cvRoot.isClickable = true
        }
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    fun getMacAddress(position: Int): String {
        return mDataSet[position].mac
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceScanInfoAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_ble_device_scan_info, parent, false)
        return ViewHolder(v)
    }

    fun addOrUpdate(mac: String, name: String?) {
        for (info in mDataSet) {
            if (mac == info.mac) {
                return
            }
        }
        mDataSet.add(BleDeviceScanInfo(mac, name))
        notifyItemInserted(mDataSet.size - 1)
    }

    private val mDataSet = ArrayList<BleDeviceScanInfo>()

}

class BleDeviceScanInfo( val mac: String, val name: String?)
