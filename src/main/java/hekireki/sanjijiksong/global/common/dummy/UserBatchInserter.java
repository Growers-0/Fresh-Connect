package hekireki.sanjijiksong.global.common.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserBatchInserter {

    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    public void insertUsers(int totalCount) {
        String sql = "INSERT INTO users (id, email, password, nickname, role, active, created_at, modified_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batch = new ArrayList<>();

        for (int i = 1; i <= totalCount; i++) {
            batch.add(new Object[]{
                    i,
                    "user" + i + "@test.com",
                    "password",
                    "nickname" + i,
                    "SELLER",
                    true,
                    Timestamp.valueOf(LocalDateTime.now()),
                    Timestamp.valueOf(LocalDateTime.now())
            });

            if (i % BATCH_SIZE == 0 || i == totalCount) {
                jdbcTemplate.batchUpdate(sql, batch);
                batch.clear();
            }
        }

        System.out.println("✅ 사용자 데이터 삽입 완료: " + totalCount + "건");
    }
}
