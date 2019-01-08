package pt.dg7.controney.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_transaction.view.*
import pt.dg7.controney.R
import pt.dg7.controney.models.Transaction

class MyTransactionRecyclerViewAdapter(
    private val mValues: List<Transaction>
) : RecyclerView.Adapter<MyTransactionRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.date.text = item.date.toString()
        holder.action.text = item.type
        holder.amount.text = item.amount.toString()

        when (item.type) {
            "deposit" -> {
                holder.action.text = "Deposit"
                holder.icon.setImageResource(R.drawable.ic_arrow_downward_black_24dp)
                holder.icon.setColorFilter(android.R.color.holo_green_dark)
            }
            "withdraw" -> {
                holder.action.text = "Withdraw"
                holder.icon.setImageResource(R.drawable.ic_arrow_upward_black_24dp)
                holder.icon.setColorFilter(android.R.color.holo_red_dark)
            }
        }

        with(holder.mView) {
            tag = item
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val date: TextView = mView.tv_date
        val action: TextView = mView.tv_action
        val amount: TextView = mView.tv_amount
        val icon: ImageView = mView.iv_icon
    }
}
