/*
 * Copyright {2017} {Shudipto Trafder}
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blogspot.shudiptotrafder.soilscience

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.blogspot.shudiptotrafder.soilscience.theme.ThemeUtils
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.content_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeUtils.initialize(this)

        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)

        val spinner: Spinner = findViewById(R.id.spinner)

        val categories = ArrayList<String>()
        categories.add("EVERYDAY")
        categories.add("TODAY")
        categories.add("SCHEDULE")

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.adapter = dataAdapter

        about_dev.setOnClickListener {
            startActivity(Intent(this,DeveloperActivity::class.java))
        }

//        license.setOnClickListener {
//            customTab("http://www.apache.org/licenses/LICENSE-2.0")
//        }

        about_git.setOnClickListener {
            customTab("https://github.com/Iamsdt/SoilScience")
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun customTab(link:String){
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(R.attr.colorPrimary)
        builder.setShowTitle(false)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(link))
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
