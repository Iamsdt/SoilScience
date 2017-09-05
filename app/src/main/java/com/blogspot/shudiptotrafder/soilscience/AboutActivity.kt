package com.blogspot.shudiptotrafder.soilscience

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.content_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)

        about_dev.setOnClickListener {
            startActivity(Intent(this,DeveloperActivity::class.java))
        }

        license.setOnClickListener {
            val link = "http://www.apache.org/licenses/LICENSE-2.0\n"
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(R.attr.colorPrimary)
            builder.setShowTitle(false)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(link))
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //buy calling android.R.id.home

        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}
