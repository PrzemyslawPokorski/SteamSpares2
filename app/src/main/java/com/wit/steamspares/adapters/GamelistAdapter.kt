import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wit.steamspares.R
import com.wit.steamspares.models.GameModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_game.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class GameListAdapter constructor(
    private var games: MutableList<GameModel>
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
        holder.bind(game)
    }

    override fun getItemCount(): Int = games.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), AnkoLogger{
        /**
         * Updates each game card view with data passed by adapter
         */
        fun bind(game: GameModel) {
            val color = if(game.status) R.color.used_game else R.color.unused_game
            itemView.gameCardFrame.setBackgroundColor(getColor(itemView.context, color))
            itemView.tag = game
            itemView.gameTitle.text = game.name
            itemView.notes.text = game.notes
            itemView.codeText.text = game.code
            itemView.storeLink.text = game.url
            Picasso.get().load(game.bannerUrl).into(itemView.image)
            info { "Debug: Picasso got image for ${game.name} : ${itemView.image}" }

            itemView.setOnClickListener {
                TransitionManager.beginDelayedTransition(it.gameCardView, AutoTransition())
                if(it.expandableCard.visibility == View.GONE)
                    it.expandableCard.visibility = View.VISIBLE
                else
                    it.expandableCard.visibility = View.GONE
            }

            itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->
                menu.add(adapterPosition, v.id, 0, R.string.edit)
                menu.add(adapterPosition, v.id, 2, R.string.delete)
                menu.add(adapterPosition, v.id, 1, R.string.status_swap)
                menu.add(adapterPosition, v.id, 3, R.string.share)
            }

            itemView.codeText.setOnClickListener {
                var clipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                var clip = ClipData.newPlainText("label", game.code)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(it.context, "CODE COPIED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}