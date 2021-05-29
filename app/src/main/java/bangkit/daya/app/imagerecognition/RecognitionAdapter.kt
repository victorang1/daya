package bangkit.daya.app.imagerecognition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.daya.databinding.RecognitionItemBinding
import bangkit.daya.model.Recognition

class RecognitionAdapter : RecyclerView.Adapter<RecognitionAdapter.RecognitionViewHolder>() {

    private val items = mutableListOf<Recognition>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecognitionViewHolder {
        val inflater = LayoutInflater.from(parent.context);
        val itemBinding = RecognitionItemBinding.inflate(inflater, parent, false)
        return RecognitionViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecognitionViewHolder, position: Int) {
        val recognitionItem = items[position]
        holder.bind(recognitionItem)
    }

    override fun getItemCount(): Int = items.count()

    fun setData(recognition: Recognition) {
        this.items.clear()
        this.items.add(recognition)
        notifyDataSetChanged()
    }

    inner class RecognitionViewHolder(private val recognitionBinding: RecognitionItemBinding) :
        RecyclerView.ViewHolder(recognitionBinding.root) {

            fun bind(recognitionItem: Recognition) {
                recognitionBinding.recognition = recognitionItem
            }
        }
}