package com.shubham.enamora

import android.app.Application
import com.shubham.enamora.data.local.database.EnamoraDatabase
import com.shubham.enamora.data.local.database.EnamoraDatabaseProvider

class EnamoraApplication : Application() {

    val database: EnamoraDatabase by lazy {
        EnamoraDatabaseProvider.getDatabase(this)
    }
}