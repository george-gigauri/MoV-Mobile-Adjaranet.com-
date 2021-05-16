package ge.mov.mobile.work_manager

/*class ReminderWork @WorkerInject constructor(
        @Assisted private val context: Context,
        private val api: APIService,
        @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val reminderMovies = DBService.getInstance(context)
                .reminderDao()
                .getAll()
                .flowOn(Dispatchers.IO)

        Log.i("REMINDER_WORK", "into work manager")

        val latestUpdates = api.getLatest()
        if (latestUpdates.isSuccessful) {
            val body = latestUpdates.body()
            if (body != null) {
                Log.i("REMINDER_WORK", "into work manager")
                val updates = body.data!![0].movies
                reminderMovies.collect { flowIt ->
                    flowIt.forEach { reminderMovieEntity ->
                        updates?.forEach {
                            if (it.id == reminderMovieEntity.id &&
                                    it.adjaraId == reminderMovieEntity.adjaraId) {
                                // Notify User
                                NotificationUtils.push(
                                        context,
                                        "სერია დამატებულია!",
                                        "${it.primaryName} - სეზონი ${it.lastUpdatedSeries?.data?.season}, სერია ${it.lastUpdatedSeries?.data?.episode}",
                                        null
                                )
                            }
                        }
                    }
                }
            } else {
                return Result.failure()
            }
        } else {
            return Result.failure()
        }

        return Result.success()
    }
} */