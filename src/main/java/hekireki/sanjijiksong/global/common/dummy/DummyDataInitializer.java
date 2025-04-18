package hekireki.sanjijiksong.global.common.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyDataInitializer implements CommandLineRunner {

    private final UserBatchInserter userBatchInserter;
    private final StoreBatchInserter storeBatchInserter;
    private final ItemBatchInserter itemBatchInserter;

    @Override
    public void run(String... args) {
        int count = 10000; // 원하는 개수
        userBatchInserter.insertUsers(count);
        storeBatchInserter.insertStores(count);
        itemBatchInserter.insertItems(count);
    }
}
