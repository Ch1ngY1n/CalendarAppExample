package codewithcal.au.calendarappexample

import android.app.AlertDialog
import android.app.DatePickerDialog
import codewithcal.au.calendarappexample.CalendarUtils.monthDayFromDate
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import codewithcal.au.calendarappexample.R
import codewithcal.au.calendarappexample.HourAdapter
import codewithcal.au.calendarappexample.HourEvent
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import codewithcal.au.calendarappexample.CalendarUtils.selectedDate
import codewithcal.au.calendarappexample.EventEditActivity
import codewithcal.au.calendarappexample.databinding.ActivityDailyCalendarBinding
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

class DailyCalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDailyCalendarBinding
    private  var currentDay = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDailyCalendarBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dayOfToday()
        onclickMenu()
        binding.monthDayText.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK,{ _, year, month, day->
                c.set(Calendar.YEAR,year)
                c.set(Calendar.MONTH,month)
                c.set(Calendar.DAY_OF_MONTH,day)
                binding.monthDayText.setText(""+ (month+1) +"月"+" "+day+"日")
                selectedDate = c.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                setDayView()
            },year,month,day).show()
        }
    }

    override fun onResume() {
        super.onResume()
        setDayView()
    }
    private fun dayOfToday(){
        //今天日期
        binding.navDaily.ucNavLayoutRight.text = currentDay.get(Calendar.DAY_OF_MONTH).toString()

        //回到今天日期
        binding.navDaily.ucNavLayoutRight.setOnClickListener {
            //今天的時間
            selectedDate = LocalDate.now()
            //設定日曆(月)
            setDayView()
        }
    }
    override fun onStart() {
        super.onStart()
        //抓取google登入資料
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val headerLayout: View = binding.navDrawer.navDrawer.getHeaderView(0)
        val DrawerUserName: TextView = headerLayout.findViewById(R.id.profile_name)
        DrawerUserName.setText(account?.displayName)
        val DrawerUserPhoto:ImageView = headerLayout.findViewById(R.id.profile_image)
        Glide.with(this).load(account?.photoUrl).into(DrawerUserPhoto)
    }
    private fun onclickMenu(){
        //側邊選單
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout_daily)
        val imgMenu = findViewById<ImageView>(R.id.uc_img_nav_left)
        val navView = findViewById<NavigationView>(R.id.nav_drawer)
        navView.itemIconTintList = null
        imgMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        //選單點擊(年)
        binding.navDrawer.BTNMainYear.setOnClickListener {
            startActivity(Intent(this,YearViewActivity::class.java))
            drawerLayout.closeDrawers()
        }
        //選單點擊週
        binding.navDrawer.BTNMainWeek.setOnClickListener {
            drawerLayout.closeDrawers()
            startActivity(Intent(this,WeekViewActivity::class.java))
        }
        //選單點擊月
        binding.navDrawer.BTNMainMonth.setOnClickListener {
            drawerLayout.closeDrawers()
            startActivity(Intent(this,MainActivity::class.java))
        }
        //選單點擊日
        binding.navDrawer.BTNMainDaily.setOnClickListener {
            drawerLayout.closeDrawers()
        }
    }
     fun addEvent(view: View?){
            startActivity(Intent(this, EventEditActivity::class.java))
    }

    private fun setDayView() {
        binding.monthDayText.text =
            selectedDate?.let { monthDayFromDate(it) }
        val dayOfWeek: String =
            selectedDate!!.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())
        binding.dayOfWeekTV.text = dayOfWeek
        setHourAdapter()
    }

    private fun setHourAdapter() {
        val hourAdapter = HourAdapter(applicationContext, hourEventList())
        binding.hourListView.adapter = hourAdapter
    }
    //0:00-23:00 一整天
    private fun hourEventList(): ArrayList<HourEvent> {
        val list = ArrayList<HourEvent>()
        for (hour in 0..23) {
            val time = LocalTime.of(hour, 0)
            val events = selectedDate?.let { Event.eventsForDateAndTime(it, time) }
            val hourEvent = events?.let { HourEvent(time, it) }
            if (hourEvent != null) {
                list.add(hourEvent)
            }
        }
        return list
    }

    fun previousDayAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.minusDays(1)
        setDayView()
    }

    fun nextDayAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.plusDays(1)
        setDayView()
    }

}