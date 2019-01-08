package pt.dg7.controney.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pt.dg7.controney.adapters.MyTransactionRecyclerViewAdapter
import pt.dg7.controney.R

import pt.dg7.controney.models.Transaction

class TransactionFragment : Fragment() {
    companion object {
        var transactions : List<Transaction> = listOf()

        @JvmStatic
        fun newInstance(data : List<Transaction>) =
            TransactionFragment().apply {
                transactions = data
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter =
                        MyTransactionRecyclerViewAdapter(transactions)
            }
        }
        return view
    }
}
