import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.placemark.R
import com.example.placemark.models.GameModel
import kotlinx.android.synthetic.main.card_placemark.view.*
import org.jetbrains.anko.AnkoLogger

class PlacemarkAdapter constructor(
    private var games: List<GameModel>,
    private val listener: PlacemarkListener
) : RecyclerView.Adapter<PlacemarkAdapter.MainHolder>(), AnkoLogger {

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
        val placemark = games[holder.adapterPosition]
        holder.bind(placemark, listener)
    }

    override fun getItemCount(): Int = games.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(game: GameModel, listener: PlacemarkListener) {
            itemView.placemarkTitle.text = game.title
            itemView.description.text = game.description
            itemView.setOnClickListener { listener.onPlacemarkClick(game) }
        }
    }
}

interface PlacemarkListener {
    fun onPlacemarkClick(game: GameModel)
}