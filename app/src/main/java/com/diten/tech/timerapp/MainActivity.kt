package com.diten.tech.timerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cn.iwgang.countdownview.CountdownView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val LIMIT_TIME: Long = 1000*60*60*24*21
    // 1000 Milisaniye
    // 60 Saniye
    // 60 dakika
    // 24 Saat
    //  21 Gün
    private var isStart = false

    companion object {
        private const val IS_START_KEY = "IS_START"
        private const val LAST_TIME_SAVED_KEY = "LAST_TIME_SAVED"
        private const val TIME_REMAIN = "TIME_REMAIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init() //*
    }

    private fun init() {
        Paper.init(this)
        isStart = Paper.book().read(IS_START_KEY,false)!!

        if (isStart) {
            btn_start.isEnabled = false
            checkTime()
        } else btn_start.isEnabled = true

        //Event
        btn_start.setOnClickListener {
            if (!isStart) {
                countdown_view.start(LIMIT_TIME)
                Paper.book().write(IS_START_KEY,true)
            }
        }

        countdown_view.setOnCountdownEndListener { //*
            Toast.makeText(this,"Finish!!",Toast.LENGTH_SHORT).show()
            reset()
        }

        countdown_view.setOnCountdownIntervalListener(1000,
            object : CountdownView.OnCountdownIntervalListener {
            override fun onInterval(cv: CountdownView?, remainTime: Long) {
                Log.d("TIMER",""+remainTime)
                println("TIMER: $remainTime")
            }
        })
    }

    override fun onStop() {
        Paper.book().write(TIME_REMAIN,countdown_view.remainTime)
        Paper.book().write(LAST_TIME_SAVED_KEY,System.currentTimeMillis())
        super.onStop()
    }

    private fun checkTime() {
        val currentTime = System.currentTimeMillis()
        val lastTimeSaved: Long =  Paper.book().read<Long>(LAST_TIME_SAVED_KEY,0)!!
        val timeRemain: Long = Paper.book().read(TIME_REMAIN,0)!!.toLong()
        val result = timeRemain + (lastTimeSaved - currentTime)
        if (result > 0) {
            countdown_view!!.start(result)
        } else {
            countdown_view.stop()
            reset()
        }
    }

    private fun reset() {
        btn_start.isEnabled = true
        Paper.book().delete(IS_START_KEY)
        Paper.book().delete(LAST_TIME_SAVED_KEY)
        Paper.book().delete(TIME_REMAIN)
        isStart = false
    }
}