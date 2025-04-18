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
public class StoreBatchInserter {

    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    public void insertStores(int totalCount) {
        String sql = "INSERT INTO store (id, user_id, name, address, description, image, active, created_at, modified_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batch = new ArrayList<>();

        for (int i = 1; i <= totalCount; i++) {
            batch.add(new Object[]{
                    i,
                    i, // user_id = store_id (1:1 대응)
                    "Store " + i,
                    "서울시 강남구 " + i + "번지",
                    "설명입니다",
                    "https://picsum.photos/seed/" + i + "/200",
                    true,
                    Timestamp.valueOf(LocalDateTime.now()),
                    Timestamp.valueOf(LocalDateTime.now())
            });

            if (i % BATCH_SIZE == 0 || i == totalCount) {
                jdbcTemplate.batchUpdate(sql, batch);
                batch.clear();
            }
        }

        System.out.println("✅ 가게 데이터 삽입 완료: " + totalCount + "건");
    }
}
