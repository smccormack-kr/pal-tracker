package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private long nextId = 1L;
    private Map<Long,TimeEntry> timeEntryMap = new HashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        TimeEntry newTimeEntry = new TimeEntry(nextId, timeEntry.getProjectId(), timeEntry.getUserId(),timeEntry.getDate(),timeEntry.getHours());
        timeEntryMap.put(nextId, newTimeEntry);
        nextId++;
        return newTimeEntry;
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return timeEntryMap.get(timeEntryId);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(timeEntryMap.values());
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry timeEntry) {
        TimeEntry timeEntryOld = this.find(timeEntryId);
        if (timeEntryOld != null) {
            TimeEntry newTimeEntry = new TimeEntry(timeEntryId, timeEntry.getProjectId(), timeEntry.getUserId(),timeEntry.getDate(),timeEntry.getHours());
            timeEntryMap.put(timeEntryId,newTimeEntry);
            return newTimeEntry;
        }
        return null;
    }

    @Override
    public void delete(long timeEntryId) {
        timeEntryMap.remove(timeEntryId);
    }
}
