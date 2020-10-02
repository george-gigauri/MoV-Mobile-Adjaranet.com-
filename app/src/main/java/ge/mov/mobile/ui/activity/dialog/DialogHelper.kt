package ge.mov.mobile.ui.activity.dialog

import android.content.Context
import android.content.LocusId
import ge.mov.mobile.database.DBService
import ge.mov.mobile.database.MovieSubscriptionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object DialogHelper {
    fun subscribe(context: Context, movieSubscriptionEntity: MovieSubscriptionEntity) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isSaved(context, movieSubscriptionEntity.id)) {
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

    fun load(context: Context, id: Long) : MovieSubscriptionEntity? = runBlocking {
        DBService.getInstance(context)
            .subscriptionDao()
            .get(id)
    }

    fun isSaved(context: Context, id: Long): Boolean = runBlocking {
        DBService.getInstance(context)
            .subscriptionDao()
            .isSaved(id)
    }
}