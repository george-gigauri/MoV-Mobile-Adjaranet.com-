package ge.mov.mobile.ui.activity.dialog

import android.content.Context
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.database.MovieSubscriptionEntity
import kotlinx.coroutines.*

object DialogHelper {
    fun subscribe(context: Context, movieSubscriptionEntity: MovieSubscriptionEntity) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isSaved(context, movieSubscriptionEntity.id)) {
                movieSubscriptionEntity.time = async { getCurrentPosition(context, movieSubscriptionEntity.id) }.await()
                DBService.getInstance(context)
                    .subscriptionDao()
                    .updateSubscription(movieSubscriptionEntity)
            } else {
                DBService.getInstance(context)
                    .subscriptionDao()
                    .save(movieSubscriptionEntity)
            }
        }
    }

    fun load(context: Context, id: Int) : MovieSubscriptionEntity? = runBlocking {
        DBService.getInstance(context)
            .subscriptionDao()
            .get(id)
    }

    private fun getCurrentPosition(context: Context, id: Int) : Long? = runBlocking {
        DBService.getInstance(context)
            .subscriptionDao()
            .get(id)
            ?.time
    }

    private fun isSaved(context: Context, id: Int): Boolean = runBlocking {
        DBService.getInstance(context)
            .subscriptionDao()
            .isSaved(id)
    }
}