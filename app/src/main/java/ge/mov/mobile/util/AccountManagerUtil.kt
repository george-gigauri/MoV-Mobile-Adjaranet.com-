package ge.mov.mobile.util

object AccountManagerUtil {
  /*  private fun getGoogleAccounts(context: Context): List<String> {
        val manager = context.getSystemService(ACCOUNT_SERVICE) as AccountManager?
        val list = manager!!.accounts
        return list.filter { it.type == "com.google" }.map { it.name }
    }

    fun isEmailApproved(activity: Activity): Boolean {
        val emails = getApprovedEmails()?.approved?.map { it.email }
        val localEmails = getGoogleAccounts(activity.applicationContext)
        if (emails != null)
            for (i in localEmails)
                if (emails.contains(i))
                    return true
        return false
    }

    private fun getApprovedEmails(): Approved? {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://raw.githubusercontent.com/george-gigauri/MoV-Mobile-Adjaranet.com-/")
            .build()
            .create(ApprovedAPIService::class.java)

        return runBlocking { retrofit.getApprovedEmails() }
    }

    private fun askForPermission(thisActivity: Activity) {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.GET_ACCOUNTS)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.GET_ACCOUNTS)) {
                AlertDialog.Builder(thisActivity)
            } else {
                ActivityCompat.requestPermissions(thisActivity,
                    arrayOf( Manifest.permission.READ_CONTACTS),
                1)
            }
        }
    } */
}