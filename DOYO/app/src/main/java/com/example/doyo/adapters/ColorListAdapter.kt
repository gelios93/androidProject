package com.example.doyo.adapters

import com.example.doyo.R
import android.view.View
import android.graphics.Color
import android.view.ViewGroup
import android.content.Context
import android.widget.ImageView
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView

class ColorListAdapter(private val colors: Array<Int>,
                       private val inflater: LayoutInflater,
                       private var currentColor: Int,
                       private var listener: OnItemClickListener,
                       private val context: Context):
    RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClickColor(position: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val card: CardView = view.findViewById(R.id.cardColor)
        val mark: ImageView = view.findViewById(R.id.checkMark)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.anim_draw_item))
            notifyItemChanged(currentColor) //remove mark from previous chosen color
            currentColor= adapterPosition //set new chosen color
            notifyItemChanged(currentColor) //set mark for chosen color
            listener.onClickColor(currentColor) //pass value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.color_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.card.background.setTint(colors[position])
        if (currentColor == position) holder.mark.apply{visibility = View.VISIBLE; setColorFilter(colorMark(colors[currentColor]))}
        else holder.mark.visibility = View.INVISIBLE

    }

    override fun getItemCount(): Int {
        return colors.size
    }

    //Set mark color depending on chosen color
    private fun colorMark(color: Int): Int {
        return if (color in arrayOf(context.resources.getColor(R.color.white, null),
                                    context.resources.getColor(R.color.yellow_palette, null),
                                    context.resources.getColor(R.color.skin_palette, null)) )Color.BLACK else Color.WHITE
    }

}