import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.placemark.R
import com.example.placemark.models.GameModel
import kotlinx.android.synthetic.main.card_game.*
import kotlinx.android.synthetic.main.card_game.view.*
import org.jetbrains.anko.AnkoLogger

class GameListAdapter constructor(
    private var games: List<GameModel>,
    private val listener: GameListener
) : RecyclerView.Adapter<GameListAdapter.MainHolder>(), AnkoLogger {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_game,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val game = games[holder.adapterPosition]
        holder.bind(game, listener)
    }

    override fun getItemCount(): Int = games.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(game: GameModel, listener: GameListener) {
            itemView.gameTitle.text = game.name
            itemView.notes.text = game.notes
            itemView.setOnClickListener {
                TransitionManager.beginDelayedTransition(it.gameCardView, AutoTransition())
                if(it.expandableCard.visibility == View.GONE)
                    it.expandableCard.visibility = View.VISIBLE
                else
                    it.expandableCard.visibility = View.GONE
            }
//            itemView.expandableCard.visibility = View.VISIBLE
        }
    }
}

interface GameListener {
    fun onGameClick(game: GameModel)
}