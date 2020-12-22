import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.placemark.R
import com.example.placemark.helpers.readImageFromPath
import com.example.placemark.models.PlacemarkModel
import kotlinx.android.synthetic.main.card_placemark.view.*

class PlacemarkAdapter constructor(
    private var placemarks: List<PlacemarkModel>,
    private val listener: PlacemarkListener
) : RecyclerView.Adapter<PlacemarkAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_placemark,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val placemark = placemarks[holder.adapterPosition]
        holder.bind(placemark, listener)
    }

    override fun getItemCount(): Int = placemarks.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(placemark: PlacemarkModel, listener: PlacemarkListener) {
            itemView.placemarkTitle.text = placemark.title
            itemView.description.text = placemark.description
            itemView.image.setImageBitmap(readImageFromPath(itemView.context, placemark.image))
            itemView.setOnClickListener { listener.onPlacemarkClick(placemark) }
        }
    }
}

interface PlacemarkListener {
    fun onPlacemarkClick(placemark: PlacemarkModel)
}