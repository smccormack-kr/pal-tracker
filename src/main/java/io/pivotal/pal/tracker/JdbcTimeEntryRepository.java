package io.pivotal.pal.tracker;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final JdbcTemplate template;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        System.out.println(timeEntry);
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
           PreparedStatement statement = connection.prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours) "
                   + "VALUES (?, ?, ?, ?)", RETURN_GENERATED_KEYS);
           statement.setLong(1, timeEntry.getProjectId());
           statement.setLong(2, timeEntry.getUserId());
           statement.setDate(3, Date.valueOf(timeEntry.getDate()));
           statement.setLong(4, timeEntry.getHours());
           System.out.println(statement);
           return statement;
        }, generatedKeyHolder);
        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return template.query(
                "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{timeEntryId},
                extractor);
    }

    @Override
    public List<TimeEntry> list() {
        return template.query(
                "SELECT id, project_id, user_id, date, hours FROM time_entries",
                mapper);
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry timeEntry) {
        TimeEntry timeEntryOld = this.find(timeEntryId);
        if (timeEntryOld != null) {
            template.update("UPDATE time_entries " +
                            "SET project_id = ?, user_id = ?, date = ?, hours = ? " +
                            "WHERE ID = ?",
                    timeEntry.getProjectId(),
                    timeEntry.getUserId(),
                    Date.valueOf(timeEntry.getDate()),
                    timeEntry.getHours(),
                    timeEntryId
            );
            return find(timeEntryId);
        }
        return null;
    }

    @Override
    public void delete(long timeEntryId) {
        template.update("DELETE FROM time_entries WHERE id = ?", timeEntryId);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}
