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
public class ItemBatchInserter {

    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    public void insertItems(int totalCount) {
        String sql = "INSERT INTO item (id, store_id, name, price, image, stock, description, active, item_status, category, created_at, modified_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batch = new ArrayList<>();

        for (int i = 1; i <= totalCount; i++) {
            batch.add(new Object[]{
                    i,
                    i, // store_id
                    "Item " + i,
                    1000 + i,
                    "https://picsum.photos/seed/item" + i + "/100",
                    10 + i,
                    "설명입니다.",
                    true,
                    "ONSALE",
                    "채소",
                    Timestamp.valueOf(LocalDateTime.now()),
                    Timestamp.valueOf(LocalDateTime.now())
            });

            if (i % BATCH_SIZE == 0 || i == totalCount) {
                jdbcTemplate.batchUpdate(sql, batch);
                batch.clear();
            }
        }

        System.out.println("✅ 아이템 데이터 삽입 완료: " + totalCount + "건");
    }
}