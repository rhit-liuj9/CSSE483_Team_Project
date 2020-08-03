package edu.rosehulman.fangr.kitchenkit

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.ingredient_card_view.view.*

class IngredientsViewHolder(
    itemView: View,
    adapter: IngredientsAdapter,
    private val context: Context
) :
    RecyclerView.ViewHolder(itemView) {

    private val nameTextView = itemView.ingredient_name as TextView
    private val amountTextView = itemView.amount_num as TextView
    private val isFrozenView = itemView.snow_icon as ImageButton
    private val boughtTextView = itemView.bought_time as TextView

    fun bind(ingredient: Ingredient) {
        this.nameTextView.text = ingredient.name
        this.amountTextView.text =
            this.context.getString(R.string.amount_display, ingredient.amount)
        this.isFrozenView.isVisible = ingredient.isFrozen

        if (ingredient.bought == null) {
            this.boughtTextView.text = this.context.getString(R.string.zero_day_display)
            return
        }

        val time = ingredient.bought?.toDate()?.time
        val currentTime = Timestamp.now().toDate().time

        Log.d(Constants.TAG, "bought: $time")
        Log.d(Constants.TAG, "current: $currentTime")

        val difference = currentTime - time!!
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        Log.d(Constants.TAG, "days: $days")
        if (days == 0)
            this.boughtTextView.text = this.context.getString(R.string.zero_day_display)
        else
            this.boughtTextView.text = this.context.resources.getQuantityString(
                R.plurals.day_display,
                days,
                days
            )
    }
}