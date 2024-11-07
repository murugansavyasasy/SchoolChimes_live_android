package com.vs.schoolmessenger.util;


import com.vs.schoolmessenger.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private static EventManager instance;
    private Map<String, ArrayList<Event>> eventsMap;

    private EventManager() {
        eventsMap = new HashMap<>();
    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void addEvent(Event event) {
        String dateKey = event.getDate();
        if (!eventsMap.containsKey(dateKey)) {
            eventsMap.put(dateKey, new ArrayList<>());
        }
        eventsMap.get(dateKey).add(event);
    }

    public void clearEvents() {
        eventsMap.clear();
    }


    public List<Event> getEventsForDate(String date) {
        return eventsMap.get(date);
    }

    // Add more methods as needed for event management

}
