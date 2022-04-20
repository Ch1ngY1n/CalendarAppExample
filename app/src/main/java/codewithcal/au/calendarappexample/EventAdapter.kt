package codewithcal.au.calendarappexample

import android.content.Context
import codewithcal.au.calendarappexample.CalendarUtils.formattedTime
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import codewithcal.au.calendarappexample.R
import android.widget.TextView

class EventAdapter(context: Context, events: List<Event?>?) :
    ArrayAdapter<Event?>(context, 0, events!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val event = getItem(position)
        if (convertView == null) convertView =
            LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false)
        val eventCellTV = convertView!!.findViewById<TextView>(R.id.eventCellTV)
        val eventCellTV2 = convertView!!.findViewById<TextView>(R.id.eventCellTV2)
        val eventCellTV3 = convertView.findViewById<TextView>(R.id.eventCellTV3)
        val eventTitle = event!!.name
        val eventTime = formattedTime(event.time)
        val eventRemark = event.remark
        eventCellTV.text = eventTitle
        eventCellTV2.text = eventTime
        eventCellTV3.text = eventRemark
        return convertView
    }
}