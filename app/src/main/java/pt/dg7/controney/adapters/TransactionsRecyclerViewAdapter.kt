package pt.dg7.controney.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import kotlinx.android.synthetic.main.item_transaction.view.*
import pt.dg7.controney.R
import pt.dg7.controney.models.Transaction
import java.text.NumberFormat
import java.util.*

class TransactionsRecyclerViewAdapter(
    val context: Context,
    var transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactions[position]
        holder.date.text = item.date.toString()
        holder.action.text = item.type

        when (item.type) {
            "deposit" -> {
                holder.action.text = item.description ?: context.getString(R.string.deposit)
                holder.action.setTextColor(context.getColor(R.color.deposit))
                holder.amount.setTextColor(context.getColor(R.color.deposit))
                holder.icon.setImageResource(R.drawable.ic_arrow_downward_black_24dp)
                holder.icon.setColorFilter(context.getColor(R.color.deposit))
                holder.amount.text = NumberFormat.getCurrencyInstance(Locale("pt", "PT"))
                    .format(item.amount)
            }
            "withdraw" -> {
                holder.action.text = item.description ?: context.getString(R.string.withdraw)
                holder.action.setTextColor(context.getColor(R.color.withdraw))
                holder.amount.setTextColor(context.getColor(R.color.withdraw))
                holder.icon.setImageResource(R.drawable.ic_arrow_upward_black_24dp)
                holder.icon.setColorFilter(context.getColor(R.color.withdraw))
                holder.amount.text = NumberFormat.getCurrencyInstance(Locale("pt", "PT"))
                    .format(-Math.abs(item.amount))
            }
            "check" -> {
                holder.action.text = item.description ?: context.getString(R.string.check)
                holder.action.setTextColor(context.getColor(R.color.check))
                holder.amount.setTextColor(context.getColor(R.color.check))
                holder.icon.setImageResource(R.drawable.ic_check_black_24dp)
                holder.icon.setColorFilter(context.getColor(R.color.check))
                holder.amount.text = NumberFormat.getCurrencyInstance(Locale("pt", "PT"))
                    .format(item.amount)
            }
        }

        with(holder.mView) {
            tag = item
        }
    }

    override fun getItemCount(): Int = transactions.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val date: TextView = mView.tv_date
        val action: TextView = mView.tv_action
        val amount: TextView = mView.tv_amount
        val icon: ImageView = mView.iv_icon
    }
}
